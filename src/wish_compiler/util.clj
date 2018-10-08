(ns ^{:author "Daniel Leong"
      :doc "util"}
  wish-compiler.util)

(defn args-raise
  "Throw an exception describing a problem with the provided args
   that can be handled cleanly by -main"
  [& message-parts]
  (throw (ex-info (apply str message-parts) {})))

