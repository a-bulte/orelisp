(ns orelisp.time.absolute-date
  (:require
   [clojure.set :refer [map-invert]]
   [malli.core :as m]
   [orelisp.spec-utils :as spec-utils]
   [orelisp.time.timescales :as timescales])
  (:import
   [org.orekit.time AbsoluteDate]))

(def epochs
  {:arbitrary AbsoluteDate/ARBITRARY_EPOCH
   :beidou AbsoluteDate/BEIDOU_EPOCH
   :ccsds AbsoluteDate/CCSDS_EPOCH
   :fifties AbsoluteDate/FIFTIES_EPOCH
   :future-infinity AbsoluteDate/FUTURE_INFINITY
   :galileo AbsoluteDate/GALILEO_EPOCH
   :glonass AbsoluteDate/GLONASS_EPOCH
   :gps AbsoluteDate/GPS_EPOCH
   :irnss AbsoluteDate/IRNSS_EPOCH
   :j2000 AbsoluteDate/J2000_EPOCH
   :java AbsoluteDate/JAVA_EPOCH
   :julian AbsoluteDate/JULIAN_EPOCH
   :modified-julian AbsoluteDate/MODIFIED_JULIAN_EPOCH
   :past-infinity AbsoluteDate/PAST_INFINITY
   :qzss AbsoluteDate/QZSS_EPOCH})

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

;; Spec

(def YearSpec (m/schema [:int]))

(def MonthSpec
  (m/schema
   [:or
    (into [:enum] (keys month-int-map))
    (into [:enum] (map name (keys month-int-map)))
    [:and [:int] [:>= 1] [:<= 12]]]))

(def DaySpec (m/schema [:and [:int] [:>= 1] [:<= 31]]))

(def HourSpec (m/schema [:and [:int] [:>= 0] [:<= 23]]))

(def MinuteSpec (m/schema [:and [:int] [:>= 0] [:<= 59]]))

(def SecondSpec
  (m/schema
   [:or
    [:and [:double] [:>= 0] [:<= 60]]
    [:and [:int] [:>= 0] [:<= 60]]]))

(def DateMapSpec
  (m/schema
   [:map
    [:year YearSpec]
    [:month MonthSpec]
    [:day DaySpec]
    [:hour {:optional true} HourSpec]
    [:minute {:optional true} MinuteSpec]
    [:second {:optional true} SecondSpec]
    [:timescale {:optional true} timescales/TimescaleSpec]]))

(def DateKeywordSpec (m/schema (into [:enum] (keys epochs))))

(def DateSpec (m/schema [:or DateMapSpec DateKeywordSpec]))

;; End spec

(defn- month->int
  "Converts a month keyword to the corresponding integer"
  [month]
  (spec-utils/throw-spec month MonthSpec "Month is not conform to spec")
  (cond
    (keyword? month) (get month-int-map month)
    (int? month) month
    (string? month) (get month-int-map (keyword month))))

(defmulti ->absolute-date "Converts to Orekit AbsoluteDate" type)

(defmethod ->absolute-date clojure.lang.PersistentArrayMap
  [date]
  (spec-utils/throw-spec date DateMapSpec "Date is not conform to spec")
  (let [{:keys [year month day hour minute second timescale]
         :or {hour 0
              minute 0
              second 0.0
              timescale :utc}} date]
    (AbsoluteDate. year (month->int month) day hour minute (double second) (timescales/get-timescale timescale))))

(defmethod ->absolute-date clojure.lang.Keyword
  [epoch]
  (spec-utils/throw-spec epoch DateKeywordSpec "Date is not conform to spec")
  (get epochs epoch))

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
  (spec-utils/throw-spec date DateSpec "Date is not conform to spec")
  (let [orekit-date (->absolute-date date)
        orekit-shifted-date (.shiftedBy orekit-date (double dt))
        timescale (or (:timescale date) :utc)]
    (absolute-date->map orekit-shifted-date timescale)))
