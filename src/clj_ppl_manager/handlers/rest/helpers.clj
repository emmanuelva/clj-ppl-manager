(ns clj-ppl-manager.handlers.rest.helpers
  (:require [cheshire.core :as json]))

(defn response
  ([status]
   (response status nil))
  ([status body]
   (merge
     {:status  status
      :headers {"Content-Type" "application/json"}}
     (when body {:body (json/encode body)}))))

(def ok (partial response 200))
(def created (partial response 201))
(def bad-request (partial response 400))
(def unauthorized (partial response 401))
(def not-found (partial response 404))
(def unprocessable-entity (partial response 422))
