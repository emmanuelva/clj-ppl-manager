(ns clj-ppl-manager.models.user
  (:require [malli.core :as m]))

(def User
  (m/schema [:map
             [:id :uuid]
             [:role-id :uuid]
             [:email :string]
             [:password :string]]))
