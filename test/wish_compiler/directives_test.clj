(ns wish-compiler.directives-test
  (:require [clojure.test :refer :all]
            [wish-compiler.directives :refer :all]))

(deftest validate-class-test
  (testing "Valid"
    (is (nil? (validate [:!declare-class
                         {:id :foo
                          :name "Foo"}]))))

  (testing "Require id"
    (is (= [[:error "`:id` is required"]]
           (validate [:!declare-class
                      {:name "Foo"}])))
    (is (= [[:error "`:id` must be a keyword"]]
           (validate [:!declare-class
                      {:id "Foo"
                       :name "Foo"}]))))

  (testing "Require name"
    (is (= [[:error "`:name` is required"]]
           (validate [:!declare-class
                      {:id :foo}])))

    (is (= [[:error "`:name` must be a string"]]
           (validate [:!declare-class
                      {:id :foo
                       :name :name}])))

    (is (= [[:error "`:name` must not be blank"]]
           (validate [:!declare-class
                      {:id :foo
                       :name ""}])))))

