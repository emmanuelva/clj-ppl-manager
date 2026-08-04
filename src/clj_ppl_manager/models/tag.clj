(ns clj-ppl-manager.models.tag
  (:require [malli.core :as m]))

(def Tag
  (m/schema [:map
             [:person-id :uuid]
             [:tag :string]]))
