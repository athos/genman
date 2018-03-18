(defproject genman "0.1.0-SNAPSHOT"
  :description "Generator management utility for clojure.spec"
  :url "https://github.com/athos/genman"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0" :scope "provided"]
                 [org.clojure/clojurescript "1.9.946" :scope "provided"]]

  :profiles {:dev {:dependencies [[com.gfredericks/test.chuck "0.2.8"]]
                   :plugins [[lein-cljsbuild "1.1.7"]
                             [lein-doo "0.1.9"]]}}

  :cljsbuild {:builds
              {:test
               {:source-paths ["src" "test"]
                :compiler {:main genman.test-runner
                           :output-to "target/test/genman.js"
                           :output-dir "target/test"
                           :target :nodejs
                           :optimizations :none}}
               :test-min
               {:source-paths ["src" "test"]
                :compiler {:main genman.test-runner
                           :output-to "target/test-advanced/genman.js"
                           :output-dir "target/test-advanced"
                           :target :nodejs
                           :optimizations :advanced}}}}

  :aliases {"test-all" ["do" ["test"] ["test-cljs"]]
            "test-cljs" ["do"
                         ["test-cljs-none" "once"]
                         ["test-cljs-min" "once"]]
            "test-cljs-none" ["doo" "node" "test"]
            "test-cljs-min" ["doo" "node" "test-min"]})
