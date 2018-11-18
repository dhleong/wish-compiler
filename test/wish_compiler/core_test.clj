(ns wish-compiler.core-test
  (:require [clojure.test :refer :all]
            [wish-compiler.core :refer :all]))

(deftest parse-args-test
  (testing "Works normally"
    (is (= {"<source-dir>" "."
            "<output-file>" "./foo-bar.json"}

           (select-keys
             (parse-args ["." "./foo-bar.json"])
             ["<source-dir>" "<output-file>"]))))

  (testing "Handle space in output name"
    (is (= {"<source-dir>" "."
            "<output-file>" "./foo bar.json"}

           (select-keys
             (parse-args ["." "./foo bar.json"])
             ["<source-dir>" "<output-file>"])))))

