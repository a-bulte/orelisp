(ns orelisp.spec-utils
  (:require
   [malli.core :as m]
   [malli.error :as me]))

(defmacro try-spec
  [expr]
  (let [e (gensym "e")]
    `(try
       ~expr
       (catch Exception ~e
         (me/humanize (ex-data ~e))))))

(defmacro throw-spec
  [input-value spec error-message]
  `(when-let [spec-error# (m/explain ~spec ~input-value)]
     (throw (ex-info ~error-message spec-error#))))
