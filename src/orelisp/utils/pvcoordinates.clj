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

(defn map->orekit-3d-vector
  "Converts a map to an instance of Hipparchus 3DVector"
  [vector]
  (Vector3D. (:x vector)
             (:y vector)
             (:z vector)))

(defn map->orekit-pvcoordinates
  "Converts a map to an instance of Orekit PVCoordinates"
  [coordinates]
  (spec-utils/throw-spec coordinates PVCoordinatesSpec "PV Coordinates are not conform to spec")
  (let [position (:position coordinates)
        velocity (or (:velocity coordinates) zero-vec)
        position-vector (map->orekit-3d-vector position)
        velocity-vector (map->orekit-3d-vector velocity)]
    (PVCoordinates. position-vector velocity-vector)))

(defn orekit-3d-vector->map
  [orekit-3d-vector]
  {:x (.getX orekit-3d-vector)
   :y (.getY orekit-3d-vector)
   :z (.getZ orekit-3d-vector)})

(defn orekit-pvcoordinates->map
  [orekit-pvcoordinates]
  (let [position (.getPosition orekit-pvcoordinates)
        velocity (.getVelocity orekit-pvcoordinates)]
    {:position (orekit-3d-vector->map position)
     :velocity (orekit-3d-vector->map velocity)}))

(-> {:position {:x 1 :y 2 :z 3}}
    map->orekit-pvcoordinates
    orekit-pvcoordinates->map)
