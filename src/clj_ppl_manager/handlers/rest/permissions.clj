(ns clj-ppl-manager.handlers.rest.permissions
  (:require [clj-ppl-manager.controllers.permissions :as controllers.permissions]
            [clj-ppl-manager.handlers.rest.helpers :as rest.helpers])
  (:import (java.util UUID)))

(defn create-permission
  [request datasource]
  (let [input      (-> (:json-params request)
                        (select-keys [:role-id :field :write])
                        (update :role-id #(some-> % parse-uuid)))
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

(def create-permission-handler
  {:name :rest.permissions/create
   :enter
   (fn [{:keys [dependencies request] :as context}]
     (let [{:keys [datasource]} dependencies]
       (->> (create-permission request datasource)
            (assoc context :response))))})

(def get-permission-handler
  {:name :rest.permissions/get
   :enter
   (fn [{:keys [dependencies request] :as context}]
     (let [{:keys [datasource]} dependencies]
       (->> (get-permission request datasource)
            (assoc context :response))))})
