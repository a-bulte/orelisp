(ns orelisp.orbits.orbit
  (:require
   [malli.core :as m]
   [orelisp.frames.frames :as frames]
   [orelisp.spec-utils :as spec-utils]
   [orelisp.time.absolute-date :as dates]
   [orelisp.utils.pvcoordinates :as pvcoordinates])
  (:import
   [org.orekit.orbits CartesianOrbit KeplerianOrbit PositionAngleType]))

(def orbital-elements-names
  {:orbit/semi-major-axis #{:a :sma :semi-major-axis}
   :orbit/eccentricity #{:e :ecc :eccentricity}
   :orbit/inclination #{:i :inc :inclination}
   :orbit/perigee-argument #{:pa :aop :perigee-argument :argument-of-periapsis}
   :orbit/right-ascension-of-the-ascending-node #{:raan :right-ascension-of-the-ascending-node}
   :orbit/position-angle #{:nu :anomaly :position-angle}})

(defn- translate-orbital-element
  "Translates common names for orbital elements to standardized one.
  e.g. :a -> :orbit/semi-major-axis
       :raan -> :orbit/right-ascension-of-the ascending-node"
  [input]
  (or (some (fn [[k v]]
              (when (contains? v input)
                k))
            orbital-elements-names)
      input))

(def position-angle-types-map
  {:position-angle/eccentric PositionAngleType/ECCENTRIC
   :position-angle/mean PositionAngleType/MEAN
   :position-angle/true PositionAngleType/TRUE})

;; Spec

(def KeplerianOrbitalParametersSpec
  (m/schema
   [:map
    [:orbit/semi-major-axis [:and [:double] [:>= 0]]]
    [:orbit/eccentricity [:and [:double] [:>= 0]]]
    [:orbit/inclination [:double]]
    [:orbit/perigee-argument [:double]]
    [:orbit/right-ascension-of-the-ascending-node [:double]]
    [:orbit/position-angle [:double]]
    [:position-angle/type (into [:enum] (keys position-angle-types-map))]]))

(def CartesianOrbitalParametersSpec
  pvcoordinates/PVCoordinatesSpec)

(def ^:private orbit-type-spec-map
  {:keplerian KeplerianOrbitalParametersSpec
   :cartesian CartesianOrbitalParametersSpec})

(defn- OrbitSpec
  [orbit-type]
  (m/schema
   [:map
    [:orbit/type [:enum orbit-type]]
    [:frame frames/FrameSpec]
    [:date dates/DateSpec]
    [:body [:map [:mu [:and [:double] [:>= 0]]]]]
    [:orbit/parameters (get orbit-type-spec-map orbit-type)]]))

;; End spec

(defmulti ->orekit-orbit "Converts to orekit orbit" :orbit/type)

(defmethod ->orekit-orbit :keplerian
  [orbit]
  (let [translated-orbit (update orbit :orbit/parameters #(update-keys % translate-orbital-element))]
    (spec-utils/throw-spec translated-orbit (OrbitSpec :keplerian) "Orbit is not conform to spec")
    (let [{:orbit/keys [semi-major-axis
                        eccentricity
                        inclination
                        perigee-argument
                        right-ascension-of-the-ascending-node
                        position-angle]} (:orbit/parameters translated-orbit)
          orekit-position-angle-type (get position-angle-types-map (get-in translated-orbit [:orbit/parameters :position-angle/type]))
          {:keys [frame date]} translated-orbit
          orekit-date (dates/->absolute-date date)]
      (KeplerianOrbit. semi-major-axis
                       eccentricity
                       inclination
                       perigee-argument
                       right-ascension-of-the-ascending-node
                       position-angle
                       orekit-position-angle-type
                       (frames/get-frame frame)
                       orekit-date
                       (get-in translated-orbit [:body :mu])))))

(defmethod ->orekit-orbit :cartesian
  [orbit]
  (spec-utils/throw-spec orbit (OrbitSpec :cartesian) "Orbit is not conform to spec")
  (let [pvcoordinates (pvcoordinates/map->orekit-pvcoordinates (:orbit/parameters orbit))
        {:keys [frame date]} orbit
        orekit-date (dates/->absolute-date date)]
    (CartesianOrbit. pvcoordinates (frames/get-frame frame) orekit-date (get-in orbit [:body :mu]))))
