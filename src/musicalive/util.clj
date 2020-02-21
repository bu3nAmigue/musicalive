(ns musicalive.util
  (:use [overtone.live]
        [musicalive.ritmos :refer [instrumentos]]))

(defn flatten1
  "Takes a map and returns a seq of all the key val pairs:
      (flatten1 {:a 1 :b 2 :c 3}) ;=> (:b 2 :c 3 :a 1)"
  [m]
  (reduce (fn [r [arg val]] (cons arg (cons val r))) [] m))

(defn normalise-beat-info
  [beat]
  (cond
   (= 1 beat)         {}
   (map? beat)        beat
   (sequential? beat) beat
   :else              {}))

(defn schedule-pattern
  [curr-t pat-dur sound pattern]
  {:pre [(sequential? pattern)]}
  (let [beat-sep-t (/ pat-dur (count pattern))]
    (doseq [[beat-info idx] (partition 2 (interleave pattern (range)))]
      (let [beat-t    (+ curr-t (* idx beat-sep-t))
            beat-info (normalise-beat-info beat-info)]
        (if (sequential? beat-info)
          (schedule-pattern beat-t beat-sep-t sound beat-info)
          (at beat-t (apply sound (flatten1 beat-info))))))))

(defn plain-live-sequencer
  ([curr-t sep-t live-patterns] (plain-live-sequencer curr-t sep-t live-patterns 0))
  ([curr-t sep-t live-patterns beat]
     (doseq [[sound pattern] @live-patterns
             :when (= 1 (nth pattern (mod beat (count pattern))))]
       (at curr-t (sound)))
     (let [new-t (+ curr-t @sep-t)]
       (apply-by new-t #'plain-live-sequencer [new-t sep-t live-patterns (inc beat)]))))

(defn live-sequencer
  [curr-t pat-dur live-patterns]   ; pat-dur is fixed number
  (doseq [[sound pattern] @live-patterns]
    (schedule-pattern curr-t pat-dur sound pattern))
  (let [new-t (+ curr-t pat-dur)]
    (apply-by new-t #'live-sequencer [new-t pat-dur live-patterns])))

(defn play-rhythm [live-patterns ritmo]
   (map  #(swap! live-patterns assoc ((first %) instrumentos) (-> % rest first)) ritmo))
