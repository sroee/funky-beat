(ns funky-1.core
  (:gen-class)
  (:require [funky-1.numbers.funky-beat1 :as fb]
            [funky-1.numbers.basic-beats-example1 :as s1])
  (:use [overtone.live]))

(defn play-riff [metro beat time-arr play-arr]
  (map 
    (fn [plays t] 
      (map 
        (fn [play]
          (play (+ t beat))) 
        plays)
      ) play-arr time-arr))

(defn play-ptrn [ptrn metro start-time] 
  (play-riff metro start-time
                (:times ptrn)
                (:sounds ptrn)))

(defn play-song [song]
  (let [metro (metronome 90)
               start-time (metro)]
  (map (fn [part] 
         (map (fn [dbl-bar] (play-ptrn (:p part) metro (+ (* 8 dbl-bar) start-time))) (:b part))) (song :metro metro))
    ))

(defn- main [])
