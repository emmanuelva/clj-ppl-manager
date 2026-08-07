(ns clj-ppl-manager.models.person
  (:require [malli.core :as m]))

(def Person
  (m/schema [:map
             [:id :uuid]
             [:name :string]
             [:dob inst?]
             [:day-of-birth :int]
             [:month-of-birth :int]
             [:gender [:enum "male" "female"]]
             [:email [:maybe :string]]
             [:phone [:maybe :string]]]))

(def NewPerson
  (m/schema [:map
             [:name :string]
             [:dob inst?]
             [:day-of-birth :int]
             [:month-of-birth :int]
             [:gender [:enum "male" "female"]]
             [:email [:maybe :string]]
             [:phone [:maybe :string]]]))
