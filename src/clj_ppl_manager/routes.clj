(ns clj-ppl-manager.routes
  (:require [clj-ppl-manager.handlers.health :as rest.health]
            [clj-ppl-manager.handlers.routes :as rest.routes]))

(def combined-routes
  (into
    [["/health" {:name :health :get {:handler rest.health/respond-ok}}]]
    rest.routes/routes))
