(ns funky-1.numbers.funky-beat1
  (:use [overtone.live]
        [funky-1.bass-utils]
        [funky-1.bass-guitar]))

(def fsnare (sample (freesound-path 43377)))
(def fkick (sample (freesound-path 43369)))
(def fchat (sample (freesound-path 43373)))
(def fcrash    (sample (freesound-path 28717)))

(defn phrases [& {:keys [:metro]}]
  (letfn [(sound-pl [sound]
            (fn [timing]
              (at (metro timing) (sound))))]

    {;:drums  {:p1 {
     ;           :times (map (fn [t] (/ t 4)) (range 0 32))
     ;           :sounds (map (fn [k h] (map sound-pl (remove nil?  [k h])))
     ;                    (flatten [kick2086 nil nil kick2086 snare26903 nil snare26903 kick2086 nil nil nil nil snare26903 nil nil kick2086 
     ;                              (repeat 8 nil) nil nil nil kick2086 snare26903 kick2086 nil nil])
     ;                    [nil c-hat802 c-hat802 nil c-hat802 c-hat802 c-hat802 nil c-hat802 c-hat802 c-hat802 nil nil c-hat802 c-hat802 nil
     ;                    nil c-hat802 c-hat802 nil c-hat802 c-hat802 c-hat802 nil c-hat802 c-hat802 c-hat802 nil nil c-hat802 c-hat802 c-hat802])}
     ;         :p2 {
     ;           :times (map (fn [t] (/ t 4)) (range 0 32))
     ;           :sounds (map (fn [k h] (map sound-pl (remove nil?  [k h])))
     ;                    (flatten [kick2086 nil nil kick2086 snare26903 nil snare26903 kick2086 nil nil nil nil snare26903 nil nil kick2086 
     ;                              (repeat 8 nil) nil nil nil kick2086 snare26903 kick2086 nil nil])
     ;                    (flatten [[crash (repeat 3 nil)] (repeat 7 [c-hat802 (repeat 3 nil)])]))}}
     :bassp  {:p1 {
                :times  (flatten (repeat 2 
                                     [0.25        0.5         0.25        0.25        0.25        0.25        0.75        0.75        0.5         0.25]))
                :sounds             [[2 12 0.25]  [2 12 0.25] [2 10 0.25] [2 12 0.25] [2 10 0.25] [1 12 0.25] [1 10 0.75] [1 11 0.75] [1 12 0.5]  [1 12 0.25]
                                     [2 0 0.25]   [2 0 0.25]  [1 0 0.25]  [2 0 0.25]  [1 3 0.25]  [1 0 0.25]  [2 3 0.75]  [3 4 0.75]  [3 5 0.75]]
              }
            }}))

(defn funky-1 [& {:keys [:metro]}]
  (let [phrases (phrases :metro metro)
      ;  drums (:drums phrases)
        bassp (:bassp phrases)]
    (println bassp)
    [;{:p (:p2 drums)
    ;:b (range 0 32)}
   {:p (bass-ptrn (:p1 bassp) metro bg)
    :b (range 0 4)}]))
