(ns funky-1.numbers.four-am
  (:use [overtone.live]
        [funky-1.bass-utils]
        [funky-1.i-set]
        [funky-1.song-player])
  (:require [funky-1.bass-guitar :as bg]
            [overtone.at-at :as att]
            [funky-1.phrase-utils :as pu]))

(def metro (metronome 100))

(def drum-set {:kick (sample (freesound-path 43377))
               :snare (sample (freesound-path 43369))
               :chat (sample (freesound-path 43373))
               :ohat (sample (freesound-path 43374))
               :crash (sample (freesound-path 28717))})

(definst simple-sin [freq 110 duration 1]
  (* (env-gen (dadsr 0 0 duration duration 0.01 1 :step 0) 1 1 0 1 FREE) (sin-osc freq)))

(demo 10 
      (sin-osc (env-gen (envelope [0 (-> :C4 note midi->hz) (-> :D4 note midi->hz) (-> :E4 note midi->hz) (-> :C4 note midi->hz)] [1 2 1 3] [10 :linear :sine -10]) 1 1 1 1 FREE)))

(demo 10
(let [freq 110 duration 1]
  (* (env-gen (dadsr 0 0 duration duration 0.01 duration :step 0) 1 1 0 1 FREE) (lf-tri freq))))

(definst wah-sin [freq 110 duration 1]
  (b-low-pass 
    (square freq)
    (* 
      (env-gen (dadsr 0 (/ duration 10) (/ duration 10) (* duration 2) :welch duration) 1 1 0 1 FREE) 
      (/ freq 2))))

(definst wah-sin [freq 440 duration 1]
  (* (env-gen (dadsr 0 0 duration duration 0.01 1 :step 0) 1 1 0 1 FREE)
    (b-band-pass (/ (+
                   (map #(sin-osc (* freq %)) (range 1 7))) 
                   1) (+ (/ freq 7)
                         (* 
                           (env-gen (dadsr 0 0.1 0.1 duration :welsh 0) 1 1 0 1 FREE)
                           (* freq 1.3))) 
                            0.2)))
(wah-sin 880)
(definst stretch01 [in 0 from 0 to 1]
  (+ from (* in (- to from))))

((definst shiiiit [freq 110]
   (let [ms (mouse-x 0 1)
         freq-ms (+ 15 (* ms (- 7500 20))) 
         vol-ms (+ 1 (* (pow ms 2) (- 0.05 1)))]
     (* vol-ms
     (b-band-pass (square freq) freq-ms 0.3)))) 
 (-> :B3 note midi->hz))

;; so far the control trigger is not working. need to find a way to trigger a fast drop to 0 return to 1.
;(definst switch-note-trigger [gate 1]
;  (sin-osc (* 220 (- 1 (env-gen (envelope [1 0] [0.1]) :gate gate :action 0)))))
;(def snt (switch-note-trigger))
;(ctl snt :gate 1)

;(definst test-shit [freq 220 gate 1] 
;  (sin-osc (env-gen (adsr 0.4 0.2 0.8 0.5 (/ freq 0.8) :linear) :gate (sin-osc:kr 2) :action 0)))

;(def ts (test-shit 220 1))
;[(ctl ts :gate 0)
;(ctl ts :freq 220)]

(defn i-simsin-creator [metro]
  (fn [note-param timing]
      (at (metro timing) (simple-sin (midi->hz (note (first note-param))) (second note-param)))
    ))
(defn i-wahsin-creator [metro]
  (fn [note-param timing]
      (at (metro timing) (wah-sin (midi->hz (note (first note-param))) (second note-param)))
    ))
(def bass-base {
                :beats 16
                :sounds [[:A1 1] [:C#2 1] [:E2 1] [:D2 1]]
                :times (repeat 4 4)})

(def funky-top (pu/arr-to-phrase [[:A3 0.25] 0.25 [:A3 0.25] 0.25 [:A4 0.4] 0.5 [:A3 0.25] 0.5 [:G3 0.25] 0.5 [:A3 0.25] 0.75 [:G3 0.75] 1.25]))

(def drums-sl-rl {
                  :beats 2
                  :sounds (repeat 8 :snare)
                  :times (repeat 8 0.25)})

(def drums-hh-16-st {
                 :beats 16
                 :sounds (repeat 16 :chat)
                 :times (repeat 16 1)})
(def drums-hh-16-rev {
                 :time-offset 0.5
                 :beats 16
                 :sounds (repeat 16 :chat)
                 :times (repeat 16 1)})
(def drums-kick-snare {
                 :beats 16
                 :sounds (flatten (repeat 4 [:kick :snare :kick :snare]))
                 :times (flatten (repeat 4 [1 1.5 0.5 1]))})
(def drums-hihat (pu/arr-to-phrase [:chat 0.25 :chat 0.25 :chat 0.25 :chat 0.25 :ohat 0.5 :chat 0.5 :chat 0.5 :chat 0.75 :chat 0.75] :time-offset -0.5)) 
(def yoni-drum (pu/arr-to-phrase [:chat 0.125 :chat 0.125 :ohat 0.5 nil 3.25] :time-offset -0.25))
(def drums-hh-16-craz {
                 :time-offset 0.125
                 :beats 16
                 :sounds (repeat 8 :chat)
                 :times (repeat 8 2)})
(def pre-snare (assoc (pu/concat-phrases [(pu/arr-to-phrase [:snare 1]) (pu/empty 7) ]) :time-offset -1))
(def crash (pu/arr-to-phrase [:crash 1]))

[(play-phrase (:p-note (i-set-creator metro drum-set)) yoni-drum metro 0 :on :beat)
(play-phrase (:p-note (i-set-creator metro drum-set)) pre-snare metro 0 :on :beat)]
[(play-phrase (:p-note (i-set-creator metro drum-set)) drums-sl-rl metro 0 :on :beat)
(play-phrase (i-wahsin-creator metro) funky-top metro 0 :on :bar :desc "top")]

(play-phrase (:p-note (i-set-creator metro drum-set)) drums-sl-rl metro 0 :on :beat)

(repeat-phrase (i-simsin-creator metro) bass-base metro :on :bar :desc "bass") 
[(att/stop 3 inc-sched-pool)
(repeat-phrase (i-wahsin-creator metro) bass-base metro :on :bar :desc "bass")]
[(play-phrase (:p-note (i-set-creator metro drum-set)) drums-sl-rl metro 0 :on :beat)
(play-phrase (:p-note (i-set-creator metro drum-set)) crash metro 0 :on :beat)]
[(play-phrase (:p-note (i-set-creator metro drum-set)) pre-snare metro 0 :on :bar)
(repeat-phrase (:p-note (i-set-creator metro drum-set)) drums-hh-16-st metro :on :bar :desc "16th hh straight")
(repeat-phrase (:p-note (i-set-creator metro drum-set)) drums-kick-snare metro :on :bar :desc "snare-kick")]
[(att/stop 4 inc-sched-pool)
(repeat-phrase (:p-note (i-set-creator metro drum-set)) drums-hihat metro :on :bar :desc "hh ptrn")]
[(att/stop 3 inc-sched-pool)
(repeat-phrase (:p-note (i-set-creator metro drum-set)) drums-hh-16-rev metro :on :bar :desc "16 hh flipped")]
(repeat-phrase (:p-note (i-set-creator metro drum-set)) drums-hh-16-craz metro :on :bar :desc "16 hh craz")

(att/show-schedule inc-sched-pool)
(att/stop 2 inc-sched-pool)
(stop-pl)
(stop)
(comment (fx-reverb 0))
(clear-fx 0)
