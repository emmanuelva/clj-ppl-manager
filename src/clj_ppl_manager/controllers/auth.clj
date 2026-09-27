(ns clj-ppl-manager.controllers.auth
  (:require [clj-ppl-manager.auth :as auth]
            [clj-ppl-manager.repositories.users :as repositories.users]
            [malli.core :as m]))

(def Credentials
  (m/schema [:map
             [:email [:string {:min 1}]]
             [:password [:string {:min 1}]]]))

(defn login!
  [input datasource jwt-secret]
  (if (m/validate Credentials input)
    (let [{:keys [email password]} input
          user (repositories.users/get-user-by-email email datasource)]
      (if (and (seq user) (auth/correct-password? password (:password user)))
        {:ok true :token (auth/generate-token user jwt-secret)}
        {:ok false :errors :invalid-credentials}))
    {:ok false :errors (m/explain Credentials input)}))
