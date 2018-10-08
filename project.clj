(defproject wish-compiler "0.1.0-SNAPSHOT"
  :description "Data Source compiler for wish"
  :url "https://github.com/dhleong/wish-compiler"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [docopt "0.6.1"]
                 ;; [org.clojure/tools.reader "1.3.0"]
                 [com.cognitect/transit-clj "0.8.313"]]
  :main ^:skip-aot wish-compiler.core
  :target-path "target/%s"
  :bin {:name "wish-compiler"}
  :profiles {:uberjar {:aot :all}
             :dev {:plugins [[lein-binplus "0.6.4"]]}})
