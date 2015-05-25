(ns funky-1.song-player
  (:require [overtone.at-at :as att])
  (:use [overtone.live]))

;; todos:
;; V can run infinite running loop
;; can alter part schedule while running
;; V can append new part while running
;; can alter part while running
;; song can be stopped
;; multiple songs can be loaded.
;; * can read loaded part for playing outside player.

(def inc-sched-pull (att/mk-pool))

(defn- time-len-to-abs [time-len-arr offset]
  (drop-last (reduce (fn [lst tm] (conj lst (+ (last lst) tm))) [offset] time-len-arr)))

(defn- play-phrase-abs-time [metro beat time-arr play-arr]
  (map 
    (fn [plays t] 
      (map 
        (fn [play]
          (play (+ t beat))) 
        plays)
      ) play-arr time-arr))

(defn stop-pl [& {:keys [:im]} ]
  (if im
    (stop))
  (att/stop-and-reset-pool! inc-sched-pull :strategy :kill))



(defn play-phrase [phrase metro beat & {:keys [:on] :or {:on :beat}}]
  (let [phrase-offset (get phrase :time-offset 0)
        offset (case on
                 :beat phrase-offset
                 :im-next (- phrase-offset (Math/floor phrase-offset)))]
    (play-phrase-abs-time metro beat
                          (time-len-to-abs (:times phrase) offset)
                          (:sounds phrase))))


(defn- sorted-seq-yield-bar [sq]
  (let [cached (atom sq)]
    (fn [from to]
      (if (empty? @cached)
        nil
        (loop [sq @cached
               res []]
          (let [nxt (first sq)]
            (if (and (not (nil? nxt)) (< nxt to))
              (if (>= nxt from)
                (recur (drop 1 sq)
                       (conj res nxt))
                (recur (drop 1 sq)
                       res))
              (do
                (reset! cached sq)
                res))))))))

(defn- sorted-seq-yield-next [sq]
  (let [cached (atom sq)]
    (fn []
      (if (empty? @cached)
        nil
        (do 
          (let [res (first @cached)]
            (reset! cached (drop 1 @cached))
            res))))))

(defn part-yielder [part]
 (let [bar-yielder (sorted-seq-yield-bar (:b part))
       part (:p part)]
   (fn [bar-num]
     (if-let [beats (bar-yielder bar-num (+ bar-num 1))]
       (map #( -> [% part]) beats)
       nil))))

(defn repeat-phrase [phrase metro beats  & {:keys [:on] :or {:on :beat}}]
  (let [beat-yielder (sorted-seq-yield-next beats) 
        start-time (metro)]
    (letfn [(play-beat [beat]
              (if (not (nil? beat))
                (do
                  (att/at (metro (+ (* 8 beat) start-time)) (fn [] (eval (play-beat (beat-yielder)))) inc-sched-pull)
                  (play-phrase phrase metro (+ (* (:beats phrase) beat) start-time) :on on)
                  )))]
      (att/at (metro) (fn [] (eval (play-beat (beat-yielder)))) inc-sched-pull)))
  "playing..")
 

(defn play-song [song-fn]
  (let [metro (metronome 90)
        start-time (metro)
        song-parts (song-fn :metro metro)
        part-yielders (map part-yielder song-parts)]
    (letfn [(play-bar [bar-num]
              (let [part-yields (remove nil?
                                        (map #( -> (% bar-num)) part-yielders))
                    what-to-play (apply concat part-yields)
                    is-all-done (empty? part-yields)]
                (if (not is-all-done)
                  (att/at (metro (+ (* 8 bar-num) start-time 4)) (fn [] (eval (play-bar (+ bar-num 1)))) inc-sched-pull)) 
                (map (fn [to-play]
                       (let [beat (first to-play)
                             phrase (second to-play)]
                         (play-phrase phrase metro (+ (* 8 beat) start-time)))) 
                     what-to-play))
              )]
      (att/at (metro) (fn [] (eval (play-bar 0))) inc-sched-pull)
  ))
  "playing..")






