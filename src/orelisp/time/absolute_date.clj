(ns orelisp.time.absolute-date
  (:require
   [clojure.set :refer [map-invert]]
   [orelisp.spec-utils :as spec-utils]
   [orelisp.time.epochs :as epochs]
   [orelisp.time.specs :as time-specs]
   [orelisp.time.timescales :as timescales])
  (:import
   [org.orekit.time AbsoluteDate]))

(def ^:private month-int-map
  {:january   1
   :february  2
   :march     3
   :april     4
   :may       5
   :june      6
   :july      7
   :august    8
   :september 9
   :october   10
   :november  11
   :december  12})

(defn- month->int
  "Converts a month keyword to the corresponding integer"
  [month]
  (spec-utils/throw-spec month time-specs/Month "Month is not conform to spec")
  (cond
    (keyword? month) (get month-int-map month)
    (int? month) month
    (string? month) (get month-int-map (keyword month))))

(defmulti ->absolute-date "Converts to Orekit AbsoluteDate" type)

(defmethod ->absolute-date clojure.lang.PersistentArrayMap
  [date]
  (spec-utils/throw-spec date time-specs/DateSpec "Date is not conform to spec")
  (let [{:keys [year month day hour minute second timescale]
         :or {hour 0
              minute 0
              second 0.0
              timescale :utc}} date]
    (AbsoluteDate. year (month->int month) day hour minute (double second) (timescales/get-timescale timescale))))

(defmethod ->absolute-date clojure.lang.Keyword
  [epoch]
  (get epochs/epochs epoch))

(defn absolute-date->map
  [orekit-date timescale]
  (let [orekit-timescale (timescales/get-timescale timescale)
        datetime-components (.getComponents orekit-date orekit-timescale)
        date (.getDate datetime-components)
        time (.getTime datetime-components)]
    {:year (.getYear date)
     :month (get (map-invert month-int-map) (.getMonth date))
     :day (.getDay date)
     :hour (.getHour time)
     :minute (.getMinute time)
     :second (.getSecond time)
     :timescale timescale}))

(defn shift
  [date dt]
  (spec-utils/throw-spec date time-specs/DateSpec "Date is not conform to spec")
  (let [orekit-date (->absolute-date date)
        orekit-shifted-date (.shiftedBy orekit-date (double dt))
        timescale (or (:timescale date) :utc)]
    (absolute-date->map orekit-shifted-date timescale)))
