(ns clj-ppl-manager.integration.repositories.people-test
  (:require [clj-ppl-manager.integration.aux.component :as aux.component]
            [clj-ppl-manager.repositories.people :as repositories.people]
            [clojure.test :refer :all])
  (:import (java.time LocalDate)))

(def ^:dynamic *datasource* nil)

(use-fixtures :once
  (fn [f]
    (aux.component/with-datasource
      (fn [datasource]
        (binding [*datasource* datasource]
          (f))))))

(defn random-person
  []
  {:name           (str "person-" (random-uuid))
   :dob            (LocalDate/of 1990 5 15)
   :day-of-birth   15
   :month-of-birth 5
   :gender         "female"
   :email          (str (random-uuid) "@example.com")
   :phone          "555-0100"})

(deftest save-new-person!-test
  (testing "persists a person and returns it with a generated id"
    (let [person (random-person)
          saved  (repositories.people/save-new-person! person *datasource*)]
      (is (uuid? (:id saved)))
      (is (= (:name person) (:name saved)))
      (is (= (:day-of-birth person) (:day-of-birth saved)))
      (is (= (:month-of-birth person) (:month-of-birth saved)))
      (is (= (:gender person) (:gender saved)))
      (is (= (:email person) (:email saved)))
      (is (= (:phone person) (:phone saved)))))

  (testing "allows a nil email and phone (nullable columns)"
    (let [person (assoc (random-person) :email nil :phone nil)
          saved  (repositories.people/save-new-person! person *datasource*)]
      (is (nil? (:email saved)))
      (is (nil? (:phone saved))))))

(deftest get-person-by-id-test
  (testing "returns the previously saved person for its id"
    (let [saved (repositories.people/save-new-person! (random-person) *datasource*)]
      (is (= saved (repositories.people/get-person-by-id (:id saved) *datasource*)))))

  (testing "returns an empty map when no person matches the id"
    (is (= {} (repositories.people/get-person-by-id (random-uuid) *datasource*)))))
