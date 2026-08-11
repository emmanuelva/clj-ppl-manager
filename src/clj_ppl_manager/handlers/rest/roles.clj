(ns clj-ppl-manager.handlers.rest.roles
  (:require [clj-ppl-manager.controllers.roles :as controllers.roles]
            [clj-ppl-manager.handlers.rest.helpers :as rest.helpers])
  (:import (java.util UUID)))

(defn create-role
  [request datasource]
  (let [input      (select-keys (:json-params request) [:name])
        {:keys [ok role errors]} (controllers.roles/create-role! input datasource)]
    (if ok
      (rest.helpers/created role)
      (rest.helpers/unprocessable-entity {:errors (str errors)}))))

(defn get-role
  [request datasource]
  (let [id-param   (-> request :path-params :id)]
    (try
      (let [id (UUID/fromString id-param)
            {:keys [ok role]} (controllers.roles/find-role id datasource)]
        (if ok
          (rest.helpers/ok role)
          (rest.helpers/not-found {:error "role not found"})))
      (catch IllegalArgumentException e
        (println (ex-message e))
        (rest.helpers/bad-request {:error "invalid id"})))))

(def create-role-handler
  {:name :rest.roles/create
   :enter
   (fn [{:keys [dependencies request] :as context}]
     (let [{:keys [datasource]} dependencies]
       (->> (create-role request datasource)
            (assoc context :response))))})

(def get-role-handler
  {:name :rest.roles/get
   :enter
   (fn [{:keys [dependencies request] :as context}]
     (let [{:keys [datasource]} dependencies]
       (->> (get-role request datasource)
            (assoc context :response))))})
