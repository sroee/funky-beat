(ns funky-1.numbers.funky-beat1
  (:use [overtone.live]
        [funky-1.bass-utils]
        [funky-1.i-set]
        [funky-1.song-player])
  (:require [funky-1.bass-guitar :as bg]))


(def metro (metronome 90))

(def drum-set {:snare (sample (freesound-path 43377))
               :kick (sample (freesound-path 43369))
               :chat (sample (freesound-path 43373))
               :crash (sample (freesound-path 28717))})

(definst mybass1 [freq 440 release 0.5]
  (let [nu-release (/ release 2)
        nu-freq (* freq 2)]
  (* 
    (+ 
      (* (env-gen (dadsr 0 0.001 0.0001 (- nu-release 0.1) release) 1 1 0 1 FREE) (* 2 (sin-osc nu-freq))) 
      (* (env-gen (dadsr 0.01 0.001 (- nu-release 0.1) (- nu-release 0.1) release) 1 1 0 1 FREE) (sin-osc nu-freq)) 
      (* (env-gen (dadsr 0.01 0.1 (- nu-release 0.1) (* nu-release 1.5) release) 1 1 0 1 FREE) (+ (sin-osc (/ nu-freq 2)) (sin-osc (/ nu-freq 2.01)) (sin-osc (/ nu-freq 1.99)
  )))
      ))))

(defn i-bass1-creator [metro]
  (fn [note-param timing]
    (let [note-val (bg/bass-note (first note-param) (second note-param))
          release (last note-param)]
      (at (metro timing) (mybass1 (midi->hz note-val) release))
    )))

;; some things I'd like to improve in model in order to make it easier to do complex things:
;; 1. no need to remove nils, the player better be able to just play nothing when getting them.
;; 2. maybe model can be written as a single array of data, splitted into times and sounds separately.
;; maybe can add symbol that does sounds and times together.
;; maybe like: :kick:x2 kick double beat, :kick or :kick:1 kick single beat, :chat:8 chat on 1/8 beat, :snare:x2. snare on 2 beats dotted (meaning 3 beats).
;; etc.
;; 3. bass don't need to hold separate timing for times and sounds. can be only times, and release can be calculated accordingly.
;; maybe can decide on release before the timing by 1ms or something. 
;; besides, can be special cases handeld by adding the 3rd parameter, or by putting nil.
;; so the bass can be something like:
;; [2 12] [2 12] [2 10] nil [2 12] (for example)
;; or:
;; :2|12:16 :2|12:16 (starting to accept the idea that putting time together is not that fun..
(def phrases
    {:drums  {:bd2 {
                :beats 8
                :times (repeat 32 0.25) 
                :sounds (flatten [:kick nil nil :kick :snare nil nil :kick nil nil nil nil :snare nil :snare :kick 
                                   (repeat 8 nil) nil nil nil :kick :snare :kick nil nil])}
              :bd1 {
                :beats 8
                :times (repeat 32 0.25)
                :sounds (flatten [:kick nil nil :kick :snare nil :snare :kick nil nil nil nil :snare nil nil :kick 
                                   (repeat 8 nil) nil nil nil :kick :snare :kick nil nil])}
              :cm {
                :beats 8
                :times (repeat 32 0.25)
                :sounds (flatten [[:crash (repeat 3 nil)] (repeat 7 [:chat (repeat 3 nil)])])}}

     :bassp  {:p1 {
                :beats 8
                :times  (flatten (repeat 2 
                                     [0.25        0.5         0.25        0.25        0.25        0.25        0.75        0.75        0.5         0.25]))
                :sounds             [[2 12 0.25]  [2 12 0.25] [2 10 0.25] [2 12 0.25] [2 10 0.25] [1 12 0.25] [1 10 0.75] [1 11 0.75] [1 12 0.5]  [1 12 0.25]
                                     [2 0 0.25]   [2 0 0.25]  [1 0 0.25]  [2 0 0.25]  [1 3 0.25]  [1 0 0.25]  [2 3 0.75]  [3 4 0.75]  [3 5 0.75]]
              }
              :p2 {
                   :beats 8
                   :times   [0.75         1         1        0.25        0.25        0.75        0.75        0.25        0.25        0.25        0.25        0.75        0.75        0.75]
                   :sounds  [[1 10 0.75]  [1 10 1]  [1 10 0.5] [0 10 0.25] [1 10 0.25] [1 10 0.25] [1 8 0.75]  [0 8 0.25]  [1 8 0.25]  [1 6 0.25]  [0 8 0.25]  [0 6 0.75]  [0 7 0.75]  [0 8 0.75]]
            }}
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
    [{:p (:bd1 drums)
    :b (flatten [(range 0 4) (range 8 12)])
    :n "drums bd1"
    :i (:p-note (i-set-creator metro drum-set))}
   {:p (:cm drums)
    :b (range 0 12)
    :n "drums cm"
    :i (:p-note (i-set-creator metro drum-set))}
   {:p (:bd2 drums)
    :b (range 4 8)
    :n "drums bd2"
    :i (:p-note (i-set-creator metro drum-set))}
   {:p (:p1 bassp)
    :b (flatten [(range 0 4) (range 8 12)])
    :n "bass p1"
    :i (i-bass1-creator metro)}
   {:p (:p2 bassp)
    :b (range 4 8)
    :n "bass p2"
    :i (i-bass1-creator metro)}
     ]))

(play-song funky-1 metro :on :beat)
(play-phrase (:p-note (i-set-creator metro drum-set)) (-> phrases :drums :bd1) metro 0 :on :beat)
(play-phrase (i-bass1-creator metro) (-> phrases :bassp :p1) metro 0 :on :beat)



