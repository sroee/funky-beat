(ns funky-1.numbers.funky-beat1
  (:use [overtone.live]
        [funky-1.bass-utils]
        [funky-1.i-set]))

(def drum-set {:snare (sample (freesound-path 43377))
               :kick (sample (freesound-path 43369))
               :chat (sample (freesound-path 43373))
               :crash (sample (freesound-path 28717))})

(def phrases
    {:drums  {:p1 {
                :beats 8
                :times (repeat 32 0.25) 
                :sounds (map (fn [k h] (remove nil?  [k h]))
                         (flatten [:kick nil nil :kick :snare nil :snare :kick nil nil nil nil :snare nil nil :kick 
                                   (repeat 8 nil) nil nil nil :kick :snare :kick nil nil])
                         [nil :chat :chat nil :chat :chat :chat nil :chat :chat :chat nil nil :chat :chat nil
                         nil :chat :chat nil :chat :chat :chat nil :chat :chat :chat nil nil :chat :chat :chat])}
              :p2 {
                :beats 8
                :times (repeat 32 0.25)
                :sounds (map (fn [k h] (remove nil?  [k h]))
                         (flatten [:kick nil nil :kick :snare nil :snare :kick nil nil nil nil :snare nil nil :kick 
                                   (repeat 8 nil) nil nil nil :kick :snare :kick nil nil])
                         (flatten [[:crash (repeat 3 nil)] (repeat 7 [:chat (repeat 3 nil)])]))}}
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
                  :sounds [:kick :snare :kick :snare]}
             :p2 {
                 :beats 8
                 :times (flatten (repeat 4 [1.5 0.5]))
                 :sounds (repeat 8 :chat)} 
             :crash {
                  :beats 0.5
                  :times [0.5]
                  :sounds [:crash]}
             :kick {
                  :beats 0.5
                  :times [0.5]
                  :sounds [:kick]}
             :pos-off {
                       :beats 8
                       :time-offset 2
                       :times (repeat 6 1)
                       :sounds [:kick :snare :kick :kick :kick :snare]}  
             }
     })

(defn funky-1 [& {:keys [:metro]}]
  (let [drums (:drums phrases)
        bassp (:bassp phrases)]
    [{:p (:p2 drums)
    :b (range 0 4)
    :n "drums"
    :i (:p-arr (i-set-creator metro drum-set))}
   {:p (:p1 bassp)
    :b (range 0 4)
    :n "bass"
    :i (i-bass-creator metro)}
     ]))
