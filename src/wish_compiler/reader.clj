(ns ^{:author "Daniel Leong"
      :doc "Reads directives from a `reader`"}
  wish-compiler.reader
  (:require [clojure.edn :as edn]
            [clojure.java.io :refer [reader]]
            [wish-compiler.directives :as directives])
  (:import (clojure.lang LineNumberingPushbackReader)))

(defn- read-or-catch [in]
  (let [line-start (.getLineNumber in)]
    (try
      (edn/read {:eof nil} in)
      (catch clojure.lang.EdnReader$ReaderException e
        {:issue [[line-start (.getLineNumber in)]
                 :error
                 (.getMessage (.getCause e))]}))))

(defn read-directives
  "Read directives from the given `input`, which is any valid argument
   to `reader`. Returns a map of {:issues [] :directives []}, where :issues
   is a sequence of issue vectors of the shape:
     [[line-start line-end] level message]
   and :directives is a collection of validated directives"
  ([input] (read-directives nil input))
  ([{:keys [validate]
     :or {validate directives/validate}} input]
  (with-open [in (LineNumberingPushbackReader. (reader input))]
    (loop [issues []
           directives []]
      (let [line-start (.getLineNumber in)
            d (read-or-catch in)
            line-end (.getLineNumber in)
            new-issues (if (map? d)
                         ; we caught an exception
                         [(:issue d)]

                         (some->> d
                                  (validate)
                                  (map (partial into [[line-start line-end]]))))]
        (cond
          ; done, with issues
          (and (not d)
               (seq issues))
          {:issues (seq issues)
           :directives directives}

          ; done; no issues!
          (not d) {:directives directives}

          ; abort early on errors
          (some #(= :error (second %)) new-issues)
          {:issues (concat issues new-issues)}

          ; keep loading directives
          :else
          (recur (concat issues new-issues)
                 (conj directives d))))))))
