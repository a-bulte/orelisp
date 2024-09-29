(ns orelisp.frames.frames
  (:require
   [malli.core :as m]
   [orelisp.spec-utils :as spec-utils])
  (:import
   [org.orekit.data DataContext]))

(def ^:private lazy-frames (.getFrames (DataContext/getDefault)))

(def frames
  {:eme2000 #(.getEME2000 lazy-frames)
   :icrf #(.getICRF lazy-frames)
   :gcrf #(.getGCRF lazy-frames)
   :teme #(.getTEME lazy-frames)})

;; Spec

(def FrameSpec
  (m/schema
   (into [:enum] (keys frames))))

;; End spec

(defn get-frame
  "Takes a keyword corresponding to a reference frame and returns the corresponding Orekit object"
  [frame]
  (spec-utils/throw-spec frame FrameSpec "Frame is not conform to spec")
  ((get frames frame)))
