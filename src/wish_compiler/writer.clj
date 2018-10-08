(ns ^{:author "Daniel Leong"
      :doc "Writes a sequence of directives to an `output-stream`"}
  wish-compiler.writer
  (:require [clojure.java.io :refer [output-stream writer]]
            [cognitect.transit :as t]
            [wish-compiler.util :refer [args-raise]]))

(defprotocol IWriter
  (close [this])
  (write-directive [this d]))

(deftype EdnWriter [out]
  IWriter
  (close [this]
    (.flush out)
    (.close out))
  (write-directive [this d]
    (.write out (str d))
    (.newLine out)))

; TransitWriter writes a *vector* of directives
(deftype TransitWriter [directives dest]
  IWriter
  (close [this] (with-open [out (output-stream dest)]
                  (t/write (t/writer out :json) @directives)))
  (write-directive [this d]
    (swap! directives conj d)))

(defn wish-writer
  "Create an IWriter that writes to `dest`, which is any
   valid argument to `output-stream`"
  ([dest] (wish-writer nil dest))
  ([{:keys [format]} dest]
   (case (or format :transit)
     :edn (->EdnWriter (writer dest))
     :transit (->TransitWriter (atom []) dest)
     (args-raise "Invalid output format: " format))))
