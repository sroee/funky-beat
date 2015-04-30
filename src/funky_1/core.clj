(ns funky-1.core
  (:gen-class)
  (:use [overtone.live]
        [funky-1.bass-guitar]
        [funky-1.patterns]))

(defn play-riff [metro beat time-arr play-arr]
  (map 
    (fn [plays t] 
      (map 
        (fn [play]
          (play (+ t beat))) 
        plays)
      ) play-arr time-arr))

(defn transpose-stringed [sounds-arr frets]
  (map (fn [arr] [(first arr) (+ (second arr) frets) (nth arr 2)]) sounds-arr))


(defn play-ptrn [ptrn metro start-time] 
  (play-riff metro start-time
                (:times ptrn)
                (:sounds ptrn)))


(defn bass-ptrn ([ptrn bg]
   (assoc ptrn 
      :sounds (map 
                (fn [note-p] 
                  [(fn [timing] 
                     [(bass-pick bg (first note-p) (second note-p) (- (metro timing) 50))
                      (bass-pick bg (first note-p) -1 
                                    (- (metro (+ timing (nth note-p 2))) 50))])]) 
                (:sounds ptrn))))
                ([ptrn]
   (bass-ptrn ptrn bg))
                 )

(defn play-song []
  (let [start-time (metro)]
    [(map  
       (fn [dbl-bar] 
         (play-ptrn (:p2 drums) metro (+ (* 8 dbl-bar) start-time))) (range 0 64))
     (map
       (fn [dbl-bar]
         (play-ptrn (bass-ptrn (:p1 bassp)) metro (+ (* 8 dbl-bar) start-time)))
      (range 4 64))
     (map 
       (fn [dbl-bar] 
         (play-ptrn (bass-ptrn 
                      {:sounds (transpose-stringed (:sounds (:p1 bassp)) 3) :times (:times (:p1 bassp))} 
                      bg2) 
                    metro (+ (* 8 dbl-bar) start-time))) (concat [12] (range 14 16) (range 20 24) (range 32 40) (range 48 60))) 
     ])
  )

(defn- main []
  (play-song))
