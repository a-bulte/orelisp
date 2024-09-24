(ns orelisp.time.absolute-date
  (:require
   [orelisp.time.timescales :as timescales])
  (:import
   [org.orekit.time AbsoluteDate]))

(def ^:private epochs
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

(defn- month->int
  "Converts a month keyword to the corresponding integer"
  [month]
  (cond
    (keyword? month) (get month-int-map month)
    (int? month) month
    (string? month) (get month-int-map (keyword month))))

(defmulti ->absolute-date "Converts to Orekit AbsoluteDate" type)

(defmethod ->absolute-date clojure.lang.PersistentArrayMap
  [date]
  (let [{:keys [year month day hour minute second timescale]
         :or {hour 0
              minute 0
              second 0.0
              timescale :utc}} date]
    (AbsoluteDate. year (month->int month) day hour minute (double second) (timescales/get-timescale timescale))))

(defmethod ->absolute-date clojure.lang.Keyword
  [epoch]
  (get epochs epoch))
