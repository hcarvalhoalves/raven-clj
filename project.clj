(defproject hcarvalhoalves/raven-clj "1.4.0"
  :description "Fork of Sentry clojure client"
  :url "http://github.com/hcarvalhoalves/raven-clj"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :plugins [[lein-midje "3.1.3"]
            [lein-cloverage "1.0.6"]
            [lein-vanity "0.2.0"]
            [lein-ancient "0.6.7"]]

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [clj-http "2.0.0"]
                 [cheshire "5.5.0"]]

  :profiles {:dev {:dependencies [[ns-tracker "0.3.0"]
                                  [org.clojure/tools.namespace "0.2.10"]
                                  [midje "1.8.3"]]}})
