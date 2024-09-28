(ns orelisp.time.specs
  (:require
   [malli.core :as m]
   [orelisp.time.epochs :as epochs]
   [orelisp.time.timescales :as timescales]))

(def months
  [:january :february :march :april :may :june :july :august :september :october :november :december])

(def Year (m/schema [:int]))

(def Month
  (m/schema
   [:or
    (into [:enum] months)
    (into [:enum] (map name months))
    [:and [:int] [:>= 1] [:<= 12]]]))

(def Day (m/schema [:and [:int] [:>= 1] [:<= 31]]))

(def Hour (m/schema [:and [:int] [:>= 0] [:<= 23]]))

(def Minute (m/schema [:and [:int] [:>= 0] [:<= 59]]))

(def Second
  (m/schema
   [:or
    [:and [:double] [:>= 0] [:<= 60]]
    [:and [:int] [:>= 0] [:<= 60]]]))

(def Timescale (m/schema (into [:enum] (keys timescales/timescales))))

(def DateMap
  (m/schema
   [:map
    [:year Year]
    [:month Month]
    [:day Day]
    [:hour {:optional true} Hour]
    [:minute {:optional true} Minute]
    [:second {:optional true} Second]
    [:timescale {:optional true} Timescale]]))

(def DateEpoch (m/schema (into [:enum] (keys epochs/epochs))))

(def DateSpec (m/schema [:or DateMap DateEpoch]))
