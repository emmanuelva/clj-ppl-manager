(ns clj-ppl-manager.handlers.auth
  (:require [clj-ppl-manager.controllers.auth :as controllers.auth]
            [clj-ppl-manager.handlers.helpers :as rest.helpers]))

(defn login
  [request datasource jwt-secret]
  (let [input (select-keys (:body-params request) [:email :password])
        {:keys [ok token errors]} (controllers.auth/login! input datasource jwt-secret)]
    (cond
      ok (rest.helpers/ok {:token token})
      (map? errors) (rest.helpers/unprocessable-entity {:errors (str errors)})
      :else (rest.helpers/unauthorized {:error "invalid credentials"}))))

(defn login-handler
  [{:keys [datasource jwt-secret] :as request}]
  (login request datasource jwt-secret))
