(ns raven-clj.core-test
  (:require [midje.sweet :refer :all])
  (:use raven-clj.core)
  (:import [java.sql Timestamp]
           [java.util Date]))

(def example-dsn
  (str "https://"
       "b70a31b3510c4cf793964a185cfe1fd0:b7d80b520139450f903720eb7991bf3d"
       "@example.com/1"))

(facts "on make-sentry-url"
  (fact "secure url"
    (make-sentry-url "https://example.com" "1") => "https://example.com/api/1/store/")
  (fact "insecure url"
    (make-sentry-url "http://example.com" "1") => "http://example.com/api/1/store/"))

(facts "on make-sentry-header"
  (fact "sentry header"
    (let [ts (str (Timestamp. (.getTime (Date.))))]
      (make-sentry-header ts
                          "b70a31b3510c4cf793964a185cfe1fd0"
                          "b7d80b520139450f903720eb7991bf3d")
      => (format "Sentry sentry_version=7, sentry_client=hcarvalhoalves/raven-clj/1.4.0, sentry_timestamp=%s, sentry_key=b70a31b3510c4cf793964a185cfe1fd0, sentry_secret=b7d80b520139450f903720eb7991bf3d" ts))))

(facts "on parse-dsn"
  (fact "dsn parsing"
    (parse-dsn "https://b70a31b3510c4cf793964a185cfe1fd0:b7d80b520139450f903720eb7991bf3d@example.com/1")
    => {:key        "b70a31b3510c4cf793964a185cfe1fd0"
        :secret     "b7d80b520139450f903720eb7991bf3d"
        :uri        "https://example.com"
        :project-id 1})

  (fact "dsn parsing with path"
    (parse-dsn "https://b70a31b3510c4cf793964a185cfe1fd0:b7d80b520139450f903720eb7991bf3d@example.com/sentry/1")
    => {:key        "b70a31b3510c4cf793964a185cfe1fd0"
        :secret     "b7d80b520139450f903720eb7991bf3d"
        :uri        "https://example.com/sentry"
        :project-id 1})

  (fact "dsn parsing with port"
    (parse-dsn "https://b70a31b3510c4cf793964a185cfe1fd0:b7d80b520139450f903720eb7991bf3d@example.com:9000/1")
    => {:key        "b70a31b3510c4cf793964a185cfe1fd0"
        :secret     "b7d80b520139450f903720eb7991bf3d"
        :uri        "https://example.com:9000"
        :project-id 1})

  (fact "dsn parsing with port and path"
    (parse-dsn "https://b70a31b3510c4cf793964a185cfe1fd0:b7d80b520139450f903720eb7991bf3d@example.com:9000/sentry/1")
    => {:key        "b70a31b3510c4cf793964a185cfe1fd0"
        :secret     "b7d80b520139450f903720eb7991bf3d"
        :uri        "https://example.com:9000/sentry"
        :project-id 1}))

(facts "on capture"
  (facts "with a valid dsn"
    (let [event-info (atom nil)]
      (with-redefs [send-packet (fn [ev] (reset! event-info ev))]
        (capture example-dsn {})
        (fact "should set :platform in event-info to clojure"
          (:platform @event-info) => "clojure")))))
