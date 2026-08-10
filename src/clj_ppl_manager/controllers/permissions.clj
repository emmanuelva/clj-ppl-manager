(ns clj-ppl-manager.controllers.permissions
  (:require [clj-ppl-manager.models.permission :as models.permission]
            [clj-ppl-manager.repositories.permissions :as repositories.permissions]
            [clj-ppl-manager.repositories.roles :as repositories.roles]
            [malli.core :as m]))

(defn create-permission!
  [input datasource]
  (cond
    (not (m/validate models.permission/NewPermission input))
    {:ok false :errors (m/explain models.permission/NewPermission input)}

    (empty? (repositories.roles/get-role-by-id (:role-id input) datasource))
    {:ok false :errors {:role-id "role does not exist"}}

    :else
    {:ok true :permission (repositories.permissions/save-new-permission! input datasource)}))

(defn find-permission
  [id datasource]
  (let [permission (repositories.permissions/get-permission-by-id id datasource)]
    (if (empty? permission)
      {:ok false :errors :not-found}
      {:ok true :permission permission})))
