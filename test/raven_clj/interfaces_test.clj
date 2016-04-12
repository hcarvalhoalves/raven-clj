(ns raven-clj.interfaces-test
  (:require [midje.sweet :refer :all]
            [raven-clj.interfaces :as i]))

(def e (ex-info "A catastrophical mistake" {:reason "not enough IQ"}))

(facts "on stacktrace"
  (let [event (i/stacktrace {:enviroment "almost-prod"} e ["raven-clj"])
        frame (-> event :exception first :stacktrace :frames last)]

    (fact "contains exception value"
      (-> event :message) => "A catastrophical mistake"
      (-> event :exception first :value) => "A catastrophical mistake")

    (tabular "contains stacktrace frame with source detail"
        (fact (-> frame ?key) => ?val)
        ?key ?val
        :function "clojure.core/ex-info"
        :lineno number?
        :context_line string?
        :in_app false?
        :pre_context coll?
        :post_context coll?)

    (fact "contains extra data"
      (-> event :enviroment) => "almost-prod")))
