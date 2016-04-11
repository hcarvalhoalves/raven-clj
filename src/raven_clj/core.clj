(ns raven-clj.core
  (:require [cheshire.core :as json]
            [clj-http.client :as http]
            [clojure.string :as string])
  (:import [java.util Date UUID]
           [java.sql Timestamp]
           [java.net InetAddress]))

(def raven-clj-version "hcarvalhoalves/raven-clj/1.4.0")

(defn generate-uuid []
  (string/replace (UUID/randomUUID) #"-" ""))

(defn make-sentry-url [uri project-id]
  (format "%s/api/%s/store/"
          uri project-id))

(defn make-sentry-header [timestamp key secret]
  (format "Sentry sentry_version=7, sentry_client=%s, sentry_timestamp=%s, sentry_key=%s, sentry_secret=%s"
          raven-clj-version timestamp key secret))

(defn send-packet [{:keys [timestamp uri project-id key secret] :as packet-info}]
  (let [url (make-sentry-url uri project-id)
        header (make-sentry-header timestamp key secret)
        data (dissoc packet-info :uri :project-id :key :secret)]
    (http/post url
               {:insecure? true
                :throw-exceptions false
                :headers {"X-Sentry-Auth" header
                          "User-Agent" raven-clj-version
                          "Content-Type" "application/json"}
                :body (json/generate-string data)})))

(defn parse-dsn [dsn]
  (let [[proto-auth url] (string/split dsn #"@")
        [protocol auth] (string/split proto-auth #"://")
        [key secret] (string/split auth #":")]
    {:key key
     :secret secret
     :uri (format "%s://%s" protocol
                  (string/join
                    "/" (butlast (string/split url #"/"))))
     :project-id (Integer/parseInt (last (string/split url #"/")))}))

(defn capture [dsn event-info]
  "Send a message to a Sentry server.
  event-info is a map that should contain a :message key and optional
  keys found at http://sentry.readthedocs.org/en/latest/developer/client/index.html#building-the-json-packet"
  (send-packet
   (merge (parse-dsn dsn)
          {:platform "clojure"
           :server_name (.getHostName (InetAddress/getLocalHost))
           :timestamp (str (Timestamp. (.getTime (Date.))))}
          event-info
          {:event_id (generate-uuid)})))

