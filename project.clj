(defproject org.clojars.hcarvalhoalves/raven-clj "1.7.1"
  :description "Fork of Sentry clojure client"
  :url "http://github.com/hcarvalhoalves/raven-clj"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :plugins [[lein-midje "3.1.3"]]

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [clj-http "3.0.0"]
                 [cheshire "5.5.0"]
                 [prone "1.0.1"]]

  :profiles {:dev {:dependencies [[midje "1.8.3"]]}})
