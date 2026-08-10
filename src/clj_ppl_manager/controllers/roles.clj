(ns clj-ppl-manager.controllers.roles
  (:require [clj-ppl-manager.repositories.roles :as repositories.roles]
            [malli.core :as m]))

(def NewRole
  (m/schema [:map [:name [:string {:min 1}]]]))

(defn create-role!
  [input datasource]
  (if (m/validate NewRole input)
    {:ok true :role (repositories.roles/save-new-role! (:name input) datasource)}
    {:ok false :errors (m/explain NewRole input)}))

(defn find-role
  [id datasource]
  (let [role (repositories.roles/get-role-by-id id datasource)]
    (if (seq role)
      {:ok true :role role}
      {:ok false :errors :not-found})))
