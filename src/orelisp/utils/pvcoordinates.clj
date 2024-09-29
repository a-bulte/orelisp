(ns orelisp.utils.pvcoordinates
  (:require
   [malli.core :as m]
   [orelisp.spec-utils :as spec-utils])
  (:import
   [org.hipparchus.geometry.euclidean.threed Vector3D]
   [org.orekit.utils PVCoordinates]))

;; Spec

(def Vector3DSpec
  (m/schema
   [:map
    [:x number?]
    [:y number?]
    [:z number?]]))

(def PVCoordinatesSpec
  (m/schema
   [:map
    [:position Vector3DSpec]
    [:velocity {:optional true} Vector3DSpec]
    [:acceleration {:optional true} Vector3DSpec]]))

;; End spec

(def zero-vec {:x 0 :y 0 :z 0})

(defn map->orekit-pvcoordinates
  "Converts a map to an instance of Orekit PVCoordinates"
  [coordinates]
  (spec-utils/throw-spec coordinates PVCoordinatesSpec "PV Coordinates are not conform to spec")
  (let [position (:position coordinates)
        velocity (or (:velocity coordinates) zero-vec)
        acceleration (or (:acceleration coordinates) zero-vec)
        position-vector (Vector3D. (:x position)
                                   (:y position)
                                   (:z position))
        velocity-vector (Vector3D. (:x velocity)
                                   (:y velocity)
                                   (:z velocity))
        acceleration-vector (Vector3D. (:x acceleration)
                                       (:y acceleration)
                                       (:z acceleration))]
    (PVCoordinates. position-vector velocity-vector acceleration-vector)))
