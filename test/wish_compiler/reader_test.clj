(ns wish-compiler.reader-test
  (:require [clojure.test :refer :all]
            [wish-compiler.reader :refer :all]
            [clojure.java.io :refer [input-stream]]))

(defn string-input [s]
  (input-stream (.getBytes s)))

(deftest read-directives-test
  (testing "Captures invalid edn"
    (is (= {:issues [[[1 3] :error "Unmatched delimiter: }"]]}
           (read-directives
             (string-input "[]

                             [}]"))))
    (is (= {:issues [[[1 3] :error "EOF while reading, starting at line 3"]]}
           (read-directives
             (string-input "[]

                             [")))))

  (testing "Returns issues with line numbers"
    (is (= {:issues [[[1 1] :error "Fake Error"]]}
           (read-directives
             {:validate (fn [_]
                          [[:error "Fake Error"]])}
             (string-input "[]"))))
    (is (= {:issues [[[1 4] :error "Fake Error"]]}
           (read-directives
             {:validate (fn [_]
                          [[:error "Fake Error"]])}
             (string-input "
                            [:test
                             :2
                             ]")))))

  (testing "Returns valid directives"
    (is (= {:directives [[:!declare-class
                          {:id :my-class
                           :name "My Class"}]
                         [:!declare-class
                          {:id :my-class2
                           :name "My Class2"}]]}
           (read-directives
             (string-input
               "[:!declare-class
                 {:id :my-class
                  :name \"My Class\"}]
                [:!declare-class
                 {:id :my-class2
                  :name \"My Class2\"}]"))))))

