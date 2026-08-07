(ns clj-ppl-manager.integration.repositories.roles-test
  (:require [clj-ppl-manager.integration.aux.component :as aux.component]
            [clj-ppl-manager.repositories.roles :as repositories.roles]
            [clojure.test :refer :all]))

(def ^:dynamic *datasource* nil)

(use-fixtures :once
  (fn [f]
    (aux.component/with-datasource
      (fn [datasource]
        (binding [*datasource* datasource]
          (f))))))

(defn random-role-name
  []
  (str "role-" (random-uuid)))

(deftest save-new-role!-test
  (testing "persists a role and returns it with a generated id"
    (let [name  (random-role-name)
          saved (repositories.roles/save-new-role! name *datasource*)]
      (is (uuid? (:id saved)))
      (is (= name (:name saved)))))

  (testing "rejects a duplicate name (unique constraint on roles.name)"
    (let [name (random-role-name)]
      (repositories.roles/save-new-role! name *datasource*)
      (is (thrown? Exception (repositories.roles/save-new-role! name *datasource*))))))

(deftest get-role-by-id-test
  (testing "returns the previously saved role for its id"
    (let [name         (random-role-name)
          saved        (repositories.roles/save-new-role! name *datasource*)
          fetched-role (repositories.roles/get-role-by-id (:id saved) *datasource*)]
      (is (= saved fetched-role))))

  (testing "returns an empty map when no role matches the id"
    (is (= {} (repositories.roles/get-role-by-id (random-uuid) *datasource*)))))
