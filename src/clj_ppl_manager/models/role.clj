(ns clj-ppl-manager.models.role
  (:require [malli.core :as m]))

(def Role
  (m/schema [:map
             [:id :uuid]
             [:name :string]]))
