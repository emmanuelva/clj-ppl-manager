(ns clj-ppl-manager.integration.repositories.tags-test
  (:require [clj-ppl-manager.integration.aux.component :as aux.component]
            [clj-ppl-manager.repositories.people :as repositories.people]
            [clj-ppl-manager.repositories.tags :as repositories.tags]
            [clojure.test :refer :all])
  (:import (java.time LocalDate)))

(def ^:dynamic *datasource* nil)

(use-fixtures :once
  (fn [f]
    (aux.component/with-datasource
      (fn [datasource]
        (binding [*datasource* datasource]
          (f))))))

(defn create-person!
  []
  (:id (repositories.people/save-new-person!
         {:name           (str "person-" (random-uuid))
          :dob            (LocalDate/of 1990 5 15)
          :day-of-birth   15
          :month-of-birth 5
          :gender         "male"
          :email          nil
          :phone          nil}
         *datasource*)))

(deftest save-new-tag!-test
  (testing "persists a tag for a person"
    (let [person-id (create-person!)
          saved     (repositories.tags/save-new-tag!
                       {:person-id person-id :tag "vip"} *datasource*)]
      (is (= person-id (:person-id saved)))
      (is (= "vip" (:tag saved)))))

  (testing "rejects a duplicate (person-id, tag) pair (composite primary key)"
    (let [person-id (create-person!)]
      (repositories.tags/save-new-tag! {:person-id person-id :tag "vip"} *datasource*)
      (is (thrown? Exception
                   (repositories.tags/save-new-tag!
                     {:person-id person-id :tag "vip"} *datasource*)))))

  (testing "rejects an unknown person-id (foreign key constraint)"
    (is (thrown? Exception
                 (repositories.tags/save-new-tag!
                   {:person-id (random-uuid) :tag "vip"} *datasource*)))))

(deftest get-tags-by-person-id-test
  (testing "returns all tags for a person"
    (let [person-id (create-person!)]
      (repositories.tags/save-new-tag! {:person-id person-id :tag "vip"} *datasource*)
      (repositories.tags/save-new-tag! {:person-id person-id :tag "founder"} *datasource*)
      (is (= #{"vip" "founder"}
             (set (map :tag (repositories.tags/get-tags-by-person-id person-id *datasource*)))))))

  (testing "returns an empty seq when the person has no tags"
    (let [person-id (create-person!)]
      (is (empty? (repositories.tags/get-tags-by-person-id person-id *datasource*))))))
