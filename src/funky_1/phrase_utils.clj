(ns funky-1.phrase-utils
  (:use [overtone.live]))

(defn empty [beats]
  {
   :beats beats
   :times [beats]
   :sounds [nil]})

(defn concat-phrases [phrases]
  "Concatenate phrases. time offsets of any phrase other than the first will be igonred, and phrases will be appended right after the previous phrase. phrases are expected to be covering their beats."
  (let [first-phrase (first phrases)]
    (assoc (reduce 
             (fn [mem phrase]
              {
               :beats (+ (:beats phrase) (:beats mem))
               :times (concat (:times mem) (:times phrase))
               :sounds (concat (:sounds mem) (:sounds phrase))
               }
              ) {:beats 0 :times [] :sounds [] } phrases) :time-offset (get first-phrase :time-offset 0))))

(defn arr-to-phrase [sound-time-arr & {:as params}]
  "Takes array in format [sound-obj1 timimg sound-obj2 timing2 ...] and create a phrase from it."
  (merge 
    (reduce (fn [mem note] 
              (let [sound (first note)
                    timing (last note)]
                (println " sound: " sound " timing: " timing " note: " note " mem: " mem)
                {
                 :beats (+ (:beats mem) timing)
                 :times (conj (:times mem) timing)
                 :sounds (conj (:sounds mem) sound)
                 } 
                )
            ) {:beats 0 :times [] :sounds []} (partition 2 sound-time-arr))
    params))

(comment 
  (concat-phrases [(assoc (empty 8) :time-offset 1.5) {:beats 2 :times [1 1] :sounds [:a :b]} {:beats 3 :times [2 1] :sounds [:c :d]}])
  (arr-to-phrase [:snare 0.5 :kick 1 :snare 0.5 :kick 2] :time-offset -1.5 :beats 3)
  )
