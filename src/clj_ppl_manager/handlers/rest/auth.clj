(ns clj-ppl-manager.handlers.rest.auth
  (:require [clj-ppl-manager.controllers.auth :as controllers.auth]
            [clj-ppl-manager.handlers.rest.helpers :as rest.helpers]))

(defn login
  [request datasource jwt-secret]
  (let [input (select-keys (:json-params request) [:email :password])
        {:keys [ok token errors]} (controllers.auth/login! input datasource jwt-secret)]
    (cond
      ok (rest.helpers/ok {:token token})
      (map? errors) (rest.helpers/unprocessable-entity {:errors (str errors)})
      :else (rest.helpers/unauthorized {:error "invalid credentials"}))))

(def login-handler
  {:name :rest.auth/login
   :enter
   (fn [{:keys [dependencies request] :as context}]
     (let [{:keys [datasource jwt-secret]} dependencies]
       (->> (login request datasource jwt-secret)
            (assoc context :response))))})
