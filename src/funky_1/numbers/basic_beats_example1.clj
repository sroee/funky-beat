(ns funky-1.numbers.basic-beats-example1
  (:use [overtone.live]
        [funky-1.bass-utils]
        [funky-1.bass-guitar]))

(def snare26903 (sample (freesound-path 43377)))
(def kick2086 (sample (freesound-path 43369)))
(def c-hat802 (sample (freesound-path 43373)))
(def crash    (sample (freesound-path 28717)))
(def bg2 (bass-guitar))
(def bg3 (bass-guitar))
(def bg4 (bass-guitar))

(defn phrases [& {:keys [:metro]}]
  (letfn [(sound-pl [sound]
            (fn [timing]
              (at (metro timing) (sound))))]
    {:drums  {:p1 {
                :times (map (fn [t] (/ t 4)) (range 0 32))
                :sounds (map (fn [k h] (map sound-pl (remove nil?  [k h])))
                         (flatten [kick2086 nil nil kick2086 snare26903 nil snare26903 kick2086 nil nil nil nil snare26903 nil nil kick2086 
                                   (repeat 8 nil) nil nil nil kick2086 snare26903 kick2086 nil nil])
                         [nil c-hat802 c-hat802 nil c-hat802 c-hat802 c-hat802 nil c-hat802 c-hat802 c-hat802 nil nil c-hat802 c-hat802 nil
                         nil c-hat802 c-hat802 nil c-hat802 c-hat802 c-hat802 nil c-hat802 c-hat802 c-hat802 nil nil c-hat802 c-hat802 c-hat802])}
              :p2 {
                :times (map (fn [t] (/ t 4)) (range 0 32))
                :sounds (map (fn [k h] (map sound-pl (remove nil?  [k h])))
                         (flatten [kick2086 nil nil kick2086 snare26903 nil snare26903 kick2086 nil nil nil nil snare26903 nil nil kick2086 
                                   (repeat 8 nil) nil nil nil kick2086 snare26903 kick2086 nil nil])
                         (flatten [[crash (repeat 3 nil)] (repeat 7 [c-hat802 (repeat 3 nil)])]))}}
     :bassp  {:p1 {
                :times  [0     1     1.5   2     2.25  2.75  4     4.5   5     5.5   6     6.5   6.75]
                :sounds [[3 3 1] [3 5 0.4] [3 3 0.4] [3 6 0.4] [3 7 0.25] [3 3 0.4] [3 3 0.4] [3 5 0.4] [3 6 0.4] [3 7 0.4] [2 3 0.4] [2 5 0.25] [2 6 0.4]]
              }
              :p2 {
                :times  [-1.5 -1 -0.5 0  0.25  0.75  1.5 2 2.75  3.5 3.75]
                :sounds [[1 1 0.4] [1 4 0.4] [1 6 0.5] [2 8 0.25] [2 6 0.25] [2 4 0.4] [1 6 0.25] [1 4 0.5] [1 6 0.25] [1 4 0.25] [1 6 0.25]]
              }
            }}))

(defn song-1 [& {:keys [:metro]}]
  (let [phrases (phrases :metro metro)
        drums (:drums phrases)
        bassp (:bassp phrases)]
    [{:p (:p2 drums)
    :b (range 0 32)}
   {:p (bass-ptrn (:p1 bassp) metro bg)
    :b (concat (range 2 6) (range 8 10) (range 14 26))}
   {:p (bass-ptrn {:sounds (transpose-stringed (:sounds (:p1 bassp)) 3) :times (:times (:p1 bassp))} metro bg2)
    :b (concat (range 10 14) (range 20 24))}
   {:p (bass-ptrn {:sounds (transpose-stringed (:sounds (:p1 bassp)) 32) :times (:times (:p1 bassp))} metro bg3)
    :b (map #( -> (+ (/ 1 8) %)) (range 24 28))}
   {:p (bass-ptrn {:sounds (transpose-stringed (:sounds (:p1 bassp)) 12) :times (:times (:p1 bassp))} metro bg3)
    :b (range 6 8)}]))
