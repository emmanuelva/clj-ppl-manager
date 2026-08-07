(ns clj-ppl-manager.models.permission
  (:require [malli.core :as m]))

(def Permission
  (m/schema [:map
             [:id :uuid]
             [:role-id :uuid]
             [:field :string]
             [:write :boolean]]))

(def NewPermission
  (m/schema [:map
             [:role-id :uuid]
             [:field :string]
             [:write :boolean]]))
