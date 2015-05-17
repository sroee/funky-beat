(ns funky-1.song-player
  (:require [overtone.at-at :as att])
  (:use [overtone.live]))

(def inc-sched-pull (att/mk-pool))

(defn time-len-to-abs [time-len-arr offset]
  (drop-last (reduce (fn [lst tm] (conj lst (+ (last lst) tm))) [offset] time-len-arr)))

(defn play-phrase-abs-time [metro beat time-arr play-arr]
  (map 
    (fn [plays t] 
      (map 
        (fn [play]
          (play (+ t beat))) 
        plays)
      ) play-arr time-arr))



(defn play-phrase [phrase metro beat] 
  (play-phrase-abs-time metro beat
                (time-len-to-abs (:times phrase) (get phrase :time-offset 0))
                (:sounds phrase)))

(defn song-to-bars [song-parts]
  (apply merge-with concat
              (map (fn [part] 
                     (into {} 
                           (map #( -> [% [(:p part)]] ) (:b part))))
                   song-parts)))

(defn play-song [song-fn]
  (let [metro (metronome 90)
        start-time (metro)
        song-parts (song-fn :metro metro)
        bars (song-to-bars song-parts)
        bars-count (int (apply max (map first bars)))]
    (letfn [(play-bar [bar-num]
              (if (> bars-count bar-num) 
                (att/at (metro (+ (* 8 bar-num) start-time 4)) (fn [] (eval (play-bar (+ bar-num 1)))) inc-sched-pull)) 
              (let [what-to-play (filter #( -> 
                                            (let [b (first %)]
                                              (and
                                                (>= b bar-num)
                                                (< b (+ bar-num 1))))) bars)]
                (map (fn [to-play]
                       (let [beat (first to-play)
                             phrases (second to-play)]
                         (map (fn [phrase] 
                                (play-phrase phrase metro (+ (* 8 beat) start-time))) phrases)
                         )) 
                     what-to-play))
              )]
      (att/at (metro) (fn [] (eval (play-bar 0))) inc-sched-pull)
  ))
  "playing..")






