(ns clj-ppl-manager.handlers.roles
  (:require [clj-ppl-manager.controllers.roles :as controllers.roles]
            [clj-ppl-manager.handlers.helpers :as rest.helpers])
  (:import (java.util UUID)))

(defn create-role
  [request datasource]
  (let [input      (select-keys (:body-params request) [:name])
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

(defn create-role-handler
  [{:keys [datasource] :as request}]
  (create-role request datasource))

(defn get-role-handler
  [{:keys [datasource] :as request}]
  (get-role request datasource))
