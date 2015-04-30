(ns funky-1.string-extend
  (:use [overtone.synth.stringed]))



(defn pick-string-func
  "pick the instrument's string depending on the fret selected.  A
   fret value less than -1 will cause no event; -1 or greater causes
   the previous note to be silenced; 0 or greater will also cause a
   new note event."
  ([the-strings the-inst string-index fret]
     (let [mkarg #'overtone.synth.stringed/mkarg
           the-note (#'overtone.synth.stringed/fret-to-note (nth the-strings string-index) fret)]
       (fn []
         ;; turn off the previous note
         (if (>= the-note -1)
           (ctl the-inst (mkarg "gate" string-index) 0))
         ;; NOTE: there needs to be some time between these
         ;; FIXME: +50 seems conservative.  Find minimum.
         (if (>= the-note 0)
           ;(Thread/sleep 50)
           (ctl the-inst
             (mkarg "note" string-index) the-note
             (mkarg "gate" string-index) 1))))))
