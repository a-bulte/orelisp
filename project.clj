(defproject orelisp "0.1.0-SNAPSHOT"
  :description "Orekit wrapper"
  :dependencies [[org.clojure/clojure "1.12.0"]
                 [org.orekit/orekit "12.1.2"]]
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
