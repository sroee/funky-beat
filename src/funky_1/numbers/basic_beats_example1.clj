(ns funky-1.numbers.basic-beats-example1
  (:use [overtone.live]
        [funky-1.bass-utils]
        [funky-1.i-set]
        [funky-1.song-player]))


(def metro (metronome 90))

(def drum-set { 
               :snare (sample (freesound-path 43377))
               :kick (sample (freesound-path 43369))
               :chat (sample (freesound-path 43373))
               :crash    (sample (freesound-path 28717)) })

(def phrases 
    {:metronome {
                 :beats 8
                 :times (repeat 8 1)
                 :sounds (flatten [:kick (repeat 3 :chat) :snare (repeat 3 :chat)])}
     :drums  {:p1 {
                :beats 8
                :times (repeat 32 0.25)
                :sounds (map (fn [k h] [k h])
                         (flatten [:kick nil nil :kick :snare nil :snare :kick nil nil nil nil :snare nil nil :kick 
                                   (repeat 8 nil) nil nil nil :kick :snare :kick nil nil])
                         [nil :chat :chat nil :chat :chat :chat nil :chat :chat :chat nil nil :chat :chat nil
                         nil :chat :chat nil :chat :chat :chat nil :chat :chat :chat nil nil :chat :chat :chat])}
              :p2 {
                :beats 8
                :times (repeat 32 0.25)
                :sounds (map (fn [k h] [k h])
                         (flatten [:kick nil nil :kick :snare nil :snare :kick nil nil nil nil :snare nil nil :kick 
                                   (repeat 8 nil) nil nil nil :kick :snare :kick nil nil])
                         (flatten [[:crash (repeat 3 nil)] (repeat 7 [:chat (repeat 3 nil)])]))}}
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
            }})

(defn song-1 [& {:keys [:metro]}]
  (let [drums (:drums phrases)
        bassp (:bassp phrases)
        i-drums (i-set-creator metro drum-set)]
    [{:p (:p2 drums)
    :b (range 0 36)
    :n "drums"
    :i (:p-arr i-drums)}
   {:p (:p1 bassp)
    :b (concat (range 2 6) (range 8 10) (range 14 26))
    :n "bass-base"
    :i (i-bass-creator metro)}
   {:p (update-in (:p1 bassp) [:sounds] #( -> (transpose-stringed % 3)))
    :b (concat (range 10 14) (range 20 24))
    :n "bass-t3"
    :i (i-bass-creator metro)}
   {:p (bass-ptrn (assoc 
                    (update-in 
                      (:p1 bassp) 
                      [:sounds] #( -> (transpose-stringed % 32)))
                    :time-offset 1) 
                  metro)
    :b (range 24 28)
    :n "bass-t32-o1"
    :i (i-bass-creator metro)}
   {:p (update-in (:p1 bassp) [:sounds] #( -> (transpose-stringed % 12)))
    :b (range 6 8)
    :n "bass-t12"
    :i(i-bass-creator metro)}
   {:p (:p2 bassp)
    :b (range 30 36)
    :n "bass-p2"
    :i (i-bass-creator metro)}
     ]))

(defn song-repeat [& {:keys [:metro]}]
  (let [drums (:drums phrases)
        bassp (:bassp phrases)]
    [{:p (:p1 drums)
    :b (range)
    :n "drums"
    :i (:p-arr (i-set-creator metro drum-set))}
   {:p (:p2 bassp)
    :b (drop 2 (range))
    :n "bass"
    :i (i-bass-creator metro)}
     ]))


(play-song song-1 metro :on :beat)
