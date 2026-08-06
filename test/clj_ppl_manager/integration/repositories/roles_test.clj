(ns clj-ppl-manager.integration.repositories.roles-test
  (:require [clj-ppl-manager.components.datasource :as components.datasource]
            [clj-ppl-manager.integration.aux.component :as aux.component]
            [clj-ppl-manager.integration.aux.containers :as aux.containers]
            [clj-ppl-manager.repositories.roles :as repositories.roles]
            [com.stuartsierra.component :as component]
            [clojure.test :refer :all]))

(def ^:dynamic *datasource* nil)

(defn with-database
  [f]
  (let [database-container (aux.containers/create-database-container)]
    (try
      (.start database-container)
      (let [started (component/start
                       (components.datasource/datasource-component
                         (aux.component/test-with-container-config database-container)))]
        (try
          (binding [*datasource* (started)]
            (f))
          (finally
            (component/stop started))))
      (finally
        (.stop database-container)))))

(use-fixtures :once with-database)

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
