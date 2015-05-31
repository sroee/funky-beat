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

    {:drums  {:p1 {
                :beats 8
                :times (repeat 32 0.25) 
                :sounds (map (fn [k h] (map sound-pl (remove nil?  [k h])))
                         (flatten [fkick nil nil fkick fsnare nil fsnare fkick nil nil nil nil fsnare nil nil fkick 
                                   (repeat 8 nil) nil nil nil fkick fsnare fkick nil nil])
                         [nil fchat fchat nil fchat fchat fchat nil fchat fchat fchat nil nil fchat fchat nil
                         nil fchat fchat nil fchat fchat fchat nil fchat fchat fchat nil nil fchat fchat fchat])}
              :p2 {
                :beats 8
                :times (repeat 32 0.25)
                :sounds (map (fn [k h] (map sound-pl (remove nil?  [k h])))
                         (flatten [fkick nil nil fkick fsnare nil fsnare fkick nil nil nil nil fsnare nil nil fkick 
                                   (repeat 8 nil) nil nil nil fkick fsnare fkick nil nil])
                         (flatten [[fcrash (repeat 3 nil)] (repeat 7 [fchat (repeat 3 nil)])]))}}
     :bassp  {:p1 {
                :beats 8
                :times  (flatten (repeat 2 
                                     [0.25        0.5         0.25        0.25        0.25        0.25        0.75        0.75        0.5         0.25]))
                :sounds             [[2 12 0.25]  [2 12 0.25] [2 10 0.25] [2 12 0.25] [2 10 0.25] [1 12 0.25] [1 10 0.75] [1 11 0.75] [1 12 0.5]  [1 12 0.25]
                                     [2 0 0.25]   [2 0 0.25]  [1 0 0.25]  [2 0 0.25]  [1 3 0.25]  [1 0 0.25]  [2 3 0.75]  [3 4 0.75]  [3 5 0.75]]
              }
            }
     :demo  {
             :p1 {
                  :beats 4
                  :times (repeat 4 1)
                  :sounds (map (fn [k] (map sound-pl (remove nil?  [k]))) [fkick fsnare fkick fsnare])}
             :p2 {
                 :beats 8
                 :times (flatten (repeat 4 [1.5 0.5]))
                 :sounds (map (fn [k] (map sound-pl (remove nil?  [k]))) (repeat 8 fchat))} 
             :crash {
                  :beats 0.5
                  :times [0.5]
                  :sounds (map (fn [k] (map sound-pl (remove nil?  [k]))) [fcrash])}
             :kick {
                  :beats 0.5
                  :times [0.5]
                  :sounds (map (fn [k] (map sound-pl (remove nil?  [k]))) [fkick])}
             :pos-off {
                       :beats 8
                       :time-offset 2
                       :times (repeat 6 1)
                       :sounds (map (fn [k] (map sound-pl (remove nil?  [k]))) [fkick fsnare fkick fkick fkick fsnare])}  }}))

(defn funky-1 [& {:keys [:metro]}]
  (let [phrases (phrases :metro metro)
        drums (:drums phrases)
        bassp (:bassp phrases)]
    [{:p (:p2 drums)
    :b (range 0 4)
    :n "drums"}
   {:p (bass-ptrn (:p1 bassp) metro bg)
    :b (range 0 4)
    :n "bass"}
     ]))
