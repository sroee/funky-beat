(ns funky-1.i-set
  (:use [overtone.live]))

(defn i-set-creator [metro sound-set]
  {:p-arr (fn [note-arr timing] 
            (map (fn [note] (at (metro timing) ((note sound-set)))) note-arr))
   :p-note (fn [note timing]
             (at (metro timing) ((note sound-set))))})
