(ns clj-ppl-manager.handlers.permissions
  (:require [clj-ppl-manager.controllers.permissions :as controllers.permissions]
            [clj-ppl-manager.handlers.helpers :as rest.helpers])
  (:import (java.util UUID)))

(defn create-permission
  [request datasource]
  (let [input      (-> (:body-params request)
                        (select-keys [:role-id :field :write])
                        (update :role-id (fn [v] (if (uuid? v) v (some-> v parse-uuid)))))
        {:keys [ok permission errors]} (controllers.permissions/create-permission! input datasource)]
    (if ok
      (rest.helpers/created permission)
      (rest.helpers/unprocessable-entity {:errors (str errors)}))))

(defn get-permission
  [request datasource]
  (let [id-param   (-> request :path-params :id)]
    (try
      (let [id (UUID/fromString id-param)
            {:keys [ok permission]} (controllers.permissions/find-permission id datasource)]
        (if ok
          (rest.helpers/ok permission)
          (rest.helpers/not-found {:error "permission not found"})))
      (catch IllegalArgumentException e
        (println (ex-message e))
        (rest.helpers/bad-request {:error "invalid id"})))))

(defn create-permission-handler
  [{:keys [datasource] :as request}]
  (create-permission request datasource))

(defn get-permission-handler
  [{:keys [datasource] :as request}]
  (get-permission request datasource))
