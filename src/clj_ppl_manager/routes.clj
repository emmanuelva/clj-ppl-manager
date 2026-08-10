(ns clj-ppl-manager.routes
  (:require [clj-ppl-manager.handlers.rest.health :as rest.health]
            [clj-ppl-manager.handlers.rest.routes :as rest.routes]
            [clojure.set :as set]))

(def combined-routes
  (set/union
    #{["/health" :get rest.health/respond-ok :route-name :health]}
    rest.routes/routes))
