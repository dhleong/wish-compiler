(ns ^{:author "Daniel Leong"
      :doc "Directive validation"}
  wish-compiler.directives
  (:require [clojure.string :as str]))

; ======= utils ===========================================

(defn- require-key [entity k]
  (let [v (get entity k)]
    (cond
      (not v)
      [:error (str "`" k "` is required")]

      (not (keyword? v))
      [:error (str "`" k "` must be a keyword")])))

(defn- require-string [entity k]
  (let [v (get entity k)]
    (cond
      (not v)
      [:error (str "`" k "` is required")]

      (not (string? v))
      [:error (str "`" k "` must be a string")]

      (str/blank? v)
      [:error (str "`" k "` must not be blank")])))


; ======= validators ======================================
; Validators return sequences of error vectors, which look like:
;   [level message]
; Items in the vector may be nil to indicate that a check passed.

(defn- validate-class [[_ class-def]]
  [(require-key class-def :id)
   (require-string class-def :name)])


; ======= public interface ================================

(defn validate [[kind & args :as directive]]
  (some->> (case kind
             :!declare-class (validate-class directive)
             nil)

           ; remove nil entries, since they are not an issue
           (keep identity)
           seq))
