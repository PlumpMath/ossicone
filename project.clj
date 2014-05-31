(defproject nuejure "0.1.0-SNAPSHOT"
  :description "FIXME: write description"

  :url "http://example.com/FIXME"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.6.0"]]
  
  :source-paths ["src"]

  :profiles {:dev {:dependencies [[org.clojure/clojurescript "0.0-2227"]]

                   :plugins [[com.keminglabs/cljx "0.4.0"]
                             [lein-cljsbuild "1.0.3"]
                             [com.cemerick/austin "0.1.4"]]

                   :hooks [cljx.hooks leiningen.cljsbuild]

                   :cljx {:builds [{:source-paths ["src"]
                                    :output-path "target/classes"
                                    :rules :clj}
                                   {:source-paths ["src"]
                                    :output-path "target/classes"
                                    :rules :cljs}]}

                   :cljsbuild {:builds [{:source-paths ["target/classes"]
                                         :compiler {:output-to "target/main.js"
                                                    :optimizations :advanced}}]}
                   :main nuejure}})
