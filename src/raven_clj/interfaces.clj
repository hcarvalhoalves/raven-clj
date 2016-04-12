(ns raven-clj.interfaces
  (:require [prone.stacks :as stacks]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.repl :as repl]))

(defn in-app [package app-namespaces]
  (boolean (some #(.startsWith package %) app-namespaces)))

(defn file->source [file-path line-number]
  (some-> (io/resource file-path)
          slurp
          (str/split #"\n")
          (#(drop (- line-number 6) %))
          (#(take 11 %))))

(defn frame->sentry [app-namespaces frame]
  (let [source (file->source (:class-path-url frame) (:line-number frame))]
    {:filename     (:file-name frame)
     :lineno       (:line-number frame)
     :function     (str (:package frame) "/" (:method-name frame))
     :in_app       (in-app (:package frame) app-namespaces)
     :context_line (some-> source (nth 5 nil))
     :pre_context  (some->> source (take 5))
     :post_context (some->> source (drop 6))}))

(defn stacktrace [event-map ^Exception e & [app-namespaces]]
  (let [stacks (stacks/normalize-exception (repl/root-cause e))
        frames (map (partial frame->sentry app-namespaces)
                    (reverse (:frames stacks)))]
    (-> {:message   (.getMessage e)
         :exception [{:value      (:message stacks)
                      :type       (:type stacks)
                      :stacktrace {:frames frames}}]}
        (merge event-map))))
