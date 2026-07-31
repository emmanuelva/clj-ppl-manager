(ns clj-ppl-manager.routes
  (:require [clj-ppl-manager.api.health :as api.health]
            [io.pedestal.http.route :as route]))

(def combined-routes
  #{["/health" :get api.health/respond-ok :route-name :health]})
