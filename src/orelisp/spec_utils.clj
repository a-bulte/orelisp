(ns orelisp.spec-utils
  (:require
   [malli.core :as m]
   [malli.error :as me]))

(defmacro spec-try
  [expr]
  (let [e (gensym "e")]
    `(try
       ~expr
       (catch Exception ~e
         (me/humanize (ex-data ~e))))))

(defmacro spec-throw
  [input-value spec error-message]
  `(when-let [spec-error# (m/explain ~spec ~input-value)]
     (throw (ex-info ~error-message spec-error#))))
