(ns musicalive.ritmos-parser
  (:require [clojure.string :refer [trim, join, replace,lower-case]]
            [clojure.data.json :as json]
            [clojure.repl :refer [dir-fn]]
            [clojure.java.io :as io]))


(defn namespace-header []
 (str "(ns musicalive.ritmos"
       "\n (:use [overtone.live])) \n"))

(defn parse-key [key]
  (let [remove-slash #(replace %  #"- " "")
        replace-blank #(replace %  #"\s" "-")]
    (-> key name trim remove-slash replace-blank lower-case)))

(def instruments
  (str "\n (def instrumentos \n"
   "{:BD (freesound 235453)
   :LT (freesound 809)
   :MT (freesound 183171)
   :HT (freesound 173838)
   :CL (freesound 404544)
   :SH (freesound 324346)
   :SN (freesound 26903)
   :RS (freesound 207937)
   :CB (freesound 364919)
   :CY (freesound 207956)
   :OH (freesound 183680)
   :CH (freesound 269720)}) \n"))

(defn parse-pattern
  ([pat] (parse-pattern pat (vec (repeat 16 0))))
  ([pat measure]
   (reduce #(assoc %1 %2 1) measure pat)))


(defn parse-rhythm
  [rhythm]
  (let [title  (parse-key (first rhythm))
        remain (first (rest rhythm))
        updated-data (reduce-kv (fn [m k v]
                                 (if (empty? v) m (assoc m k (parse-pattern  v)))) {} remain)]
    (str "\n(defn " title "\n  [] \n"
          updated-data
         ") \n")))

(defn generate
  []
  (let [all (json/read-str (slurp "./resources/ritmos.json")
                        :key-fn keyword)]
  (with-open [w (io/writer (str "src/musicalive/ritmos.clj") :append true)]
    (.write w (namespace-header))
    (.write w instruments)
    (doall (map #(.write w (parse-rhythm %)) all))
    (.write w "\n")
    )))

;(generate)
