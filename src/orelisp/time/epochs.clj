(ns orelisp.time.epochs
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
