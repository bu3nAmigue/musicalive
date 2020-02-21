(ns musicalive.sample
  (:use [overtone.live]
        [musicalive.ritmos]
        [musicalive.util]
        ))

(def dame-ritmo (atom {}))
(def dur1 (atom 400))

(plain-live-sequencer (+ 200 (now)) dur1 dame-ritmo)

(reset! dame-ritmo {})

(play-rhythm dame-ritmo (drum-and-bass-1-a))

(stop)

(reset! dur1 200)

(def nombres
  (with-out-str (clojure.repl/dir musicalive.ritmos)))

(stop)
