(ns funky-1.numbers.basic-beats-example1
  (:use [overtone.live]
        [funky-1.bass-utils]
        [funky-1.bass-guitar]))

(def snare26903 (sample (freesound-path 43377)))
(def kick2086 (sample (freesound-path 43369)))
(def c-hat802 (sample (freesound-path 43373)))
(def crash    (sample (freesound-path 28717)))

(defn phrases [& {:keys [:metro]}]
  (letfn [(sound-pl [sound]
            (fn [timing]
              (at (metro timing) (sound))))]
    {:metronome {
                 :beats 8
                 :times (repeat 8 1)
                 :sounds (map (fn [k] (map sound-pl (remove nil? [k])))
                                (flatten [kick2086 (repeat 3 c-hat802) snare26903 (repeat 3 c-hat802)]))}
     :drums  {:p1 {
                :beats 8
                :times (repeat 32 0.25)
                :sounds (map (fn [k h] (map sound-pl (remove nil?  [k h])))
                         (flatten [kick2086 nil nil kick2086 snare26903 nil snare26903 kick2086 nil nil nil nil snare26903 nil nil kick2086 
                                   (repeat 8 nil) nil nil nil kick2086 snare26903 kick2086 nil nil])
                         [nil c-hat802 c-hat802 nil c-hat802 c-hat802 c-hat802 nil c-hat802 c-hat802 c-hat802 nil nil c-hat802 c-hat802 nil
                         nil c-hat802 c-hat802 nil c-hat802 c-hat802 c-hat802 nil c-hat802 c-hat802 c-hat802 nil nil c-hat802 c-hat802 c-hat802])}
              :p2 {
                :beats 8
                :times (repeat 32 0.25)
                :sounds (map (fn [k h] (map sound-pl (remove nil?  [k h])))
                         (flatten [kick2086 nil nil kick2086 snare26903 nil snare26903 kick2086 nil nil nil nil snare26903 nil nil kick2086 
                                   (repeat 8 nil) nil nil nil kick2086 snare26903 kick2086 nil nil])
                         (flatten [[crash (repeat 3 nil)] (repeat 7 [c-hat802 (repeat 3 nil)])]))}}
     :bassp  {:p1 {
                :beats 8
                :times  [ 1       0.5       0.5       0.25      0.5         1.25      0.5       0.5       0.5       0.5       0.5       0.25        1.25]
                :sounds [ [3 3 1] [3 5 0.4] [3 3 0.4] [3 6 0.4] [3 7 0.25]  [3 3 0.4] [3 3 0.4] [3 5 0.4] [3 6 0.4] [3 7 0.4] [2 3 0.4] [2 5 0.25]  [2 6 0.4]]
              }
              :p2 {
                :beats 8
                :time-offset -1.5
                :times  [0.5        0.5       0.5       0.25        0.5         0.75      0.5         0.75      0.75        0.25        0.25]
                :sounds [[1 1 0.4]  [1 4 0.4] [1 6 0.5] [2 8 0.25]  [2 6 0.25]  [2 4 0.4] [1 6 0.25]  [1 4 0.5] [1 6 0.25]  [1 4 0.25]  [1 6 0.25]]
              }
            }}))

(defn song-1 [& {:keys [:metro]}]
  (let [phrases (phrases :metro metro)
        drums (:drums phrases)
        bassp (:bassp phrases)]
    [{:p (:p2 drums)
    :b (range 0 36)
    :n "drums"}
   {:p (bass-ptrn (:p1 bassp) metro)
    :b (concat (range 2 6) (range 8 10) (range 14 26))
    :n "bass-base"}
   {:p (bass-ptrn (update-in (:p1 bassp) [:sounds] #( -> (transpose-stringed % 3))) metro)
    :b (concat (range 10 14) (range 20 24))
    :n "bass-t3"}
   {:p (bass-ptrn (assoc 
                    (update-in 
                      (:p1 bassp) 
                      [:sounds] #( -> (transpose-stringed % 32)))
                    :time-offset 1) 
                  metro)
    :b (range 24 28)
    :n "bass-t32-o1"}
   {:p (bass-ptrn (update-in (:p1 bassp) [:sounds] #( -> (transpose-stringed % 12))) metro)
    :b (range 6 8)
    :n "bass-t12"}
   {:p (bass-ptrn (:p2 bassp) metro)
    :b (range 30 36)
    :n "bass-p2"}
     ]))

(defn song-repeat [& {:keys [:metro]}]
  (let [phrases (phrases :metro metro)
        drums (:drums phrases)
        bassp (:bassp phrases)]
    [{:p (:p1 drums)
    :b (range)
    :n "drums"}
   {:p (bass-ptrn (:p2 bassp) metro)
    :b (drop 2 (range))
    :n "bass"}
     ]))

