(ns orelisp.data.data
  (:import
   [java.io File]
   [org.orekit.data DataContext DirectoryCrawler]))

(defn initialize-orekit-data!
  "Add provider manager with requested data path.
   Uses default data context"
  [orekit-data-path]
  (let [orekit-data (new File orekit-data-path)
        manager (.getDataProvidersManager (DataContext/getDefault))]
    (.addProvider manager (new DirectoryCrawler orekit-data))))
