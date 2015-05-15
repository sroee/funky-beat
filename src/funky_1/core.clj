(ns funky-1.core
  (:gen-class)
  (:require [funky-1.numbers.funky-beat1 :as fb]
            [funky-1.numbers.basic-beats-example1 :as s1]
            [funky-1.song-player :as sp])
  (:use [overtone.live]))

(defn play-song [song]
  (sp/play-song song))

(defn- main [])
