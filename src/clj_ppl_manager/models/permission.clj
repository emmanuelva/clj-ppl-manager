(ns clj-ppl-manager.models.permission
  (:require [malli.core :as m]))

(def Permission
  (m/schema [:map
             [:id :uuid]
             [:role-id :uuid]
             [:field :string]
             [:write :boolean]]))
