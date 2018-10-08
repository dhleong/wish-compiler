(ns ^{:author "Daniel Leong"
      :doc "Processor"}
  wish-compiler.processor
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [wish-compiler.reader :refer [read-directives]]
            [wish-compiler.writer :as w :refer [wish-writer]]
            [wish-compiler.util :refer [args-raise]]))

(defn- default-log [& args]
  (binding [*out* *err*]
    (apply println args)))

(defn processable-files [dir dest-file]
  (->> dir
       (file-seq)
       (remove #(.isDirectory %))
       (remove #(str/includes? (.getPath %) ".git"))
       (filter #(str/ends-with? (.getPath %) ".edn"))
       (remove #(= dest-file %))
       (sort-by #(.getPath %))))

(defn dump-issues [log issues]
  ; TODO
  (log issues)
  (let [error (->> issues
                   (keep (fn [[lines kind msg]]
                           (when (= :error kind)
                             msg))))]
    (when (seq error)
      (args-raise (first error)))))

(defn process
  [{:keys [source-dir
           output-file
           format
           log]
    :or {log default-log}}]
  (let [source-dir-file (io/file source-dir)
        _ (when-not (.isDirectory source-dir-file)
            (args-raise "Source dir `" source-dir "` does not exist"))

        output-file (when (and output-file
                               (not= "-" output-file))
                      (io/file output-file))
        output-dest (or (when output-file
                          ; ensure the parent dir exists
                          (io/make-parents output-file)

                          ; return the path
                          output-file)
                        System/out)

        inputs (let [files (processable-files source-dir-file output-file)]
                 (when-not (seq files)
                   (args-raise "No processable files in " source-dir))

                 files)

        output (wish-writer {:format format}
                            output-dest)]

    (try
      (doseq [f inputs]
        (log "Processing: " (.getPath f))
        (let [{:keys [issues directives]} (read-directives f)]
          (when issues
            (dump-issues log issues))

          (doseq [d directives]
            (w/write-directive output d))))
      (finally
        (w/close output)))))
