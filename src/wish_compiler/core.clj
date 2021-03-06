(ns wish-compiler.core
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [docopt.core :refer [docopt]]
            [wish-compiler.processor :refer [process]])
  (:gen-class))

(declare run)

(defn ^{:doc "wish-compiler

Usage:
  wish-compiler [options] <source-dir> <output-file>
  wish-compiler [options] <source-dir> -
  wish-compiler -h | --help
  wish-compiler --version

Options:
  -h --help   Show this message
  --version   Show version
  --edn       Use legacy edn format
  --transit   Use transit format (default)"}
  -main [& args]
  ; separate fn to satisfy docopt in an uberjar
  (run args))

(defn- fix-spaces [s]
  (str/replace s "%20" " "))

(defn parse-args [args]
  ; a bug in docopt drops spaces in quoted arguments...
  (-> (docopt (map #(str/replace % " " "%20") args))
      (update "<source-dir>" fix-spaces)
      (update "<output-file>" fix-spaces)))

(defn run [args]
  (let [>> (parse-args args)]
    (cond
      (or (nil? >>)
          (>> "--help")) (println (:doc (meta #'-main)))
      (>> "--version") (println (str "wish-compiler, version "
                                     ; load version from project.clj
                                     (some-> (io/resource "project.clj")
                                             slurp
                                             read-string
                                             (nth 2))))

      :else (try
              (process {:source-dir (>> "<source-dir>")
                        :output-file (>> "<output-file>")
                        :format (or (when (>> "--edn") :edn)
                                    :transit)})
              (catch Throwable e
                (if-let [info (ex-data e)]
                  ; arg-related error; print nicely
                  (do (println "error:" (.getMessage e))
                      (System/exit 1))

                  ; throw the whole stack trace
                  (throw e))))
      )))
