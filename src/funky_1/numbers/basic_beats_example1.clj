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
    :b (range 0 36)}
   {:p (bass-ptrn (:p1 bassp) metro bg)
    :b (concat (range 2 6) (range 8 10) (range 14 26))}
   {:p (bass-ptrn (update-in (:p1 bassp) [:sounds] #( -> (transpose-stringed % 3))) metro bg2)
    :b (concat (range 10 14) (range 20 24))}
   {:p (bass-ptrn (assoc 
                    (update-in 
                      (:p1 bassp) 
                      [:sounds] #( -> (transpose-stringed % 32)))
                    :time-offset 1) 
                  metro bg3)
    :b (range 24 28)}
   {:p (bass-ptrn (update-in (:p1 bassp) [:sounds] #( -> (transpose-stringed % 12))) metro bg3)
    :b (range 6 8)}
   {:p (bass-ptrn (:p2 bassp) metro bg)
    :b (range 30 36)}
     ]))

(defn song-repeat [& {:keys [:metro]}]
  (let [phrases (phrases :metro metro)
        drums (:drums phrases)
        bassp (:bassp phrases)]
    [{:p (:p1 drums)
    :b (range)}
   {:p (bass-ptrn (:p2 bassp) metro bg)
    :b (drop 2 (range))}
     ]))

