(ns orelisp.frames.frames
  (:import
   [org.orekit.data DataContext]))

(def ^:private lazy-frames (.getFrames (DataContext/getDefault)))

(def frames
  {:eme2000 #(.getEME2000 lazy-frames)
   :icrf #(.getICRF lazy-frames)
   :gcrf #(.getGCRF lazy-frames)
   :teme #(.getTEME lazy-frames)})

(defn get-frame
  "Takes a keyword corresponding to a reference frame and returns the corresponding Orekit object"
  [frame]
  ((get frames frame)))

(spec-utils/spec-try (get-frame :eme200))
