(ns orelisp.time.timescales
  (:require
   [malli.core :as m]
   [orelisp.spec-utils :as spec-utils])
  (:import
   [org.orekit.data DataContext]))

(def ^:private lazy-timescales (.getTimeScales (DataContext/getDefault)))

(def timescales
  {:utc #(.getUTC lazy-timescales)
   :tt  #(.getTT lazy-timescales)
   :tai #(.getTAI lazy-timescales)
   :gps #(.getGPS lazy-timescales)
   :bdt #(.getBDT lazy-timescales)
   :glonass #(.getGLONASS lazy-timescales)
   :galileo #(.getGST lazy-timescales)
   :irnss #(.getIRNSS lazy-timescales)
   :qzss #(.getQZSS lazy-timescales)
   :tcb #(.getTCB lazy-timescales)
   :tcg #(.getTCG lazy-timescales)
   :tdb #(.getTDB lazy-timescales)})

;; Spec

(def TimescaleSpec
  (m/schema
   (into [:enum] (keys timescales))))

;; End spec

(defn get-timescale
  [timescale]
  (spec-utils/throw-spec timescale TimescaleSpec "Timescale does not conform to spec")
  ((get timescales timescale)))
