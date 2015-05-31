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

(def inc-sched-pool (att/mk-pool))

;; stupid hack until i fix it to run the lazy stuff..
(defn- void-println [text]
  (spit "/dev/null" text :append true))

(defn beats-to-ms [metro beats] 
  (* beats (- (metro 1) (metro 0))))

(defn get-curr-beat [metro]
  (/ (- (now) (metro 0)) (beats-to-ms metro 1)))

(defn- time-len-to-abs [time-len-arr offset]
  (drop-last (reduce (fn [lst tm] (conj lst (+ (last lst) tm))) [offset] time-len-arr)))

(defn- play-phrase-abs-time [metro beat time-arr play-arr]
  (let [cancel-before (get-curr-beat metro)]
    (map 
      (fn [plays t] 
        (map 
          (fn [play]
            (let [play-t (+ t beat)]
              (if (<= cancel-before play-t) (play (+ t beat))))) 
          plays)
        ) play-arr time-arr)))

(defn stop-pl [& {:keys [:im]} ]
  (if im
    (stop))
  (att/stop-and-reset-pool! inc-sched-pool :strategy :kill))

(defn- rand-desc []
   (let [chars (map char (range 99 123))
         desc (take 4 (repeatedly #(rand-nth chars)))]
                             (reduce str desc)))   

(defn cut-neg-offset [phrase]
  (loop [phrase phrase]
    (if (>= (get phrase :time-offset 0) 0)
      phrase
      (recur (update-in 
               (reduce (fn [phrase sym] 
                         (update-in phrase [sym]
                                    #( -> (drop 1 %)))) phrase [:times :sounds])
               [:time-offset] #( -> (+ % (first (:times phrase)))))))))



(defn play-phrase [phrase metro timing & {:keys [:on] :or {:on :beat}}]
  (letfn [(get-time-left-to-beat [curr-t beats] 
              (mod (- beats (mod curr-t beats)) beats))
          (compensate-offset [curr-t actual-play-time beats]
            (let [time-left (- actual-play-time curr-t)
                  periods-to-add (* -1 (Math/floor (/ time-left beats)))]
              (* periods-to-add beats)))
          (calc-offset [phrase-offset]
                        (if (number? on)
                          (+ phrase-offset on)
                          phrase-offset))]
    (let [beats (cond 
                  (= on :beat) 1
                  (or (= on :bar) (number? on)) (:beats phrase))
          curr-t (get-curr-beat metro)
          phrase-offset (get phrase :time-offset 0)
          offset (calc-offset phrase-offset)
          beat-change (get-time-left-to-beat curr-t beats)
          until-1 (compensate-offset curr-t (+ curr-t offset beat-change) beats)
          delay-add (* timing beats)
          beat (+ curr-t until-1 delay-add beat-change)]
      (play-phrase-abs-time metro beat
                            (time-len-to-abs (:times phrase) offset)
                            (:sounds phrase))
      )))

;; not in use but still thinking if 5o keep current beat yielder to be inc-yielder so haven't removed.
(comment
  (defn- create-range-yielder [sorted-seq]
    (let [cached (atom sorted-seq)]
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

  (defn- create-next-yielder [sq]
    (let [cached (atom sq)]
      (fn []
        (if (empty? @cached)
          nil
          (do 
            (let [res (first @cached)]
              (reset! cached (drop 1 @cached))
              res))))))
  )

(defn- create-inc-yielder [sorted-seq]
  (let [counter (atom -1)
        cached (atom sorted-seq)
        pop-cached (fn [] 
                    (let [nxt (first @cached)]
                      (reset! cached (drop 1 @cached))
                      nxt))
        last-stored (atom (pop-cached))]
    (drop 1 @cached)
    (fn []
      (if @last-stored
        (if (= @last-stored (swap! counter inc))
          (let [res @last-stored] 
            (reset! last-stored (pop-cached))
            res)
          nil)
        -1))))

(defn repeat-phrase [phrase metro & {:keys [:on :times :desc :initial-delay] :or {:on :beat :times (range) :initial-delay 0}}]
  (let [at-desc (if (nil? desc) (str "rpt-p-" (rand-desc)) desc)
        job (atom nil)
        beat-yielder (create-inc-yielder times)]
    (reset! job (att/every (beats-to-ms metro (:beats phrase)) 
               (fn []
                 (let [res (beat-yielder)]
                   (cond 
                     (= res -1) (att/stop @job)
                     (not (= res nil)) (void-println (play-phrase phrase metro 0 :on on))))) 
               inc-sched-pool :desc at-desc :initial-delay (beats-to-ms metro initial-delay)))))

(defn play-song [song-fn metro & {:keys [:on :desc] :or {:on :beat :desc nil}}]
  (let [at-desc (if (nil? desc) (str "song-p-" (rand-desc)) desc)
        song-parts (song-fn :metro metro)
        longest-neg-offset (apply min (remove nil? (map #( -> (get (:p %) :time-offset 0)) song-parts)))
        parts-delays (map #( -> (- (get (:p %) :time-offset 0) longest-neg-offset)) song-parts)]
    (map 
      (fn [_delay part]
        (let [at-desc (str at-desc "/" (if-let [d (get part :n)] d (rand-desc)))] 
          (repeat-phrase (:p part) metro :on on :desc at-desc :times (:b part) :initial-delay _delay))) 
      parts-delays song-parts)))
