(ns funky-1.bass-utils
  (:use [overtone.live]
        [funky-1.bass-guitar]))

(def bg (bass-guitar))

(defn transpose-stringed [sounds-arr frets]
  (map (fn [arr] [(first arr) (+ (second arr) frets) (nth arr 2)]) sounds-arr))

(defn bass-ptrn ([ptrn metro bg]
   (assoc ptrn 
      :sounds (map 
                (fn [note-p] 
                  [(fn [timing] 
                     [(bass-pick bg (first note-p) (second note-p) (- (metro timing) 50))
                      (bass-pick bg (first note-p) -1 
                                    (- (metro (+ timing (nth note-p 2))) 50))])]) 
                (:sounds ptrn))))
                ([ptrn metro]
   (bass-ptrn ptrn metro bg)))
