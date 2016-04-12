(ns raven-clj.core-test
  (:require [midje.sweet :refer :all]
            [raven-clj.core :as c])
  (:import [java.sql Timestamp]
           [java.util Date]))

(def example-dsn
  (str "https://"
       "b70a31b3510c4cf793964a185cfe1fd0:b7d80b520139450f903720eb7991bf3d"
       "@example.com/1"))

(facts "on make-sentry-url"
  (fact "secure url"
    (c/make-sentry-url "https://example.com" "1") => "https://example.com/api/1/store/")
  (fact "insecure url"
    (c/make-sentry-url "http://example.com" "1") => "http://example.com/api/1/store/"))

(facts "on make-sentry-header"
  (fact "sentry header"
    (let [ts (str (Timestamp. (.getTime (Date.))))]
      (c/make-sentry-header ts
                            "b70a31b3510c4cf793964a185cfe1fd0"
                            "b7d80b520139450f903720eb7991bf3d")
      => (format "Sentry sentry_version=7, sentry_client=raven-clj/1.5.0, sentry_timestamp=%s, sentry_key=b70a31b3510c4cf793964a185cfe1fd0, sentry_secret=b7d80b520139450f903720eb7991bf3d" ts))))

(facts "on parse-dsn"
  (fact "dsn parsing"
    (c/parse-dsn "https://b70a31b3510c4cf793964a185cfe1fd0:b7d80b520139450f903720eb7991bf3d@example.com/1")
    => {:key        "b70a31b3510c4cf793964a185cfe1fd0"
        :secret     "b7d80b520139450f903720eb7991bf3d"
        :uri        "https://example.com"
        :project-id 1})

  (fact "dsn parsing with path"
    (c/parse-dsn "https://b70a31b3510c4cf793964a185cfe1fd0:b7d80b520139450f903720eb7991bf3d@example.com/sentry/1")
    => {:key        "b70a31b3510c4cf793964a185cfe1fd0"
        :secret     "b7d80b520139450f903720eb7991bf3d"
        :uri        "https://example.com/sentry"
        :project-id 1})

  (fact "dsn parsing with port"
    (c/parse-dsn "https://b70a31b3510c4cf793964a185cfe1fd0:b7d80b520139450f903720eb7991bf3d@example.com:9000/1")
    => {:key        "b70a31b3510c4cf793964a185cfe1fd0"
        :secret     "b7d80b520139450f903720eb7991bf3d"
        :uri        "https://example.com:9000"
        :project-id 1})

  (fact "dsn parsing with port and path"
    (c/parse-dsn "https://b70a31b3510c4cf793964a185cfe1fd0:b7d80b520139450f903720eb7991bf3d@example.com:9000/sentry/1")
    => {:key        "b70a31b3510c4cf793964a185cfe1fd0"
        :secret     "b7d80b520139450f903720eb7991bf3d"
        :uri        "https://example.com:9000/sentry"
        :project-id 1}))

(facts "on capture"
  (facts "with a valid dsn"
    (fact "should set :platform in event-info to clojure"
      (c/capture example-dsn {}) => irrelevant
      (provided
        (c/get-timestamp) => "2042-01-01 00:00:00.000"
        (c/get-hostname) => "example.com"
        (c/generate-uuid) => "00000000000000000000000000000000"
        (c/send-packet {:key         "b70a31b3510c4cf793964a185cfe1fd0"
                        :secret      "b7d80b520139450f903720eb7991bf3d"
                        :uri         "https://example.com"
                        :project-id  1
                        :platform    "clojure"
                        :server_name "example.com"
                        :timestamp   "2042-01-01 00:00:00.000"
                        :event_id    "00000000000000000000000000000000"}) => irrelevant))))
