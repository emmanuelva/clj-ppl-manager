(ns clj-ppl-manager.integration.repositories.permissions-test
  (:require [clj-ppl-manager.integration.aux.component :as aux.component]
            [clj-ppl-manager.repositories.permissions :as repositories.permissions]
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

(defn random-field-name
  []
  (str "field-" (random-uuid)))

(defn create-role!
  []
  (:id (repositories.roles/save-new-role! (random-role-name) *datasource*)))

(deftest save-new-permission!-test
  (testing "persists a permission and returns it with a generated id"
    (let [role-id (create-role!)
          field   (random-field-name)
          saved   (repositories.permissions/save-new-permission!
                     {:role-id role-id :field field :write true}
                     *datasource*)]
      (is (uuid? (:id saved)))
      (is (= role-id (:role-id saved)))
      (is (= field (:field saved)))
      (is (true? (:write saved)))))

  (testing "rejects a duplicate (role-id, field) pair (unique constraint)"
    (let [role-id (create-role!)
          field   (random-field-name)]
      (repositories.permissions/save-new-permission!
        {:role-id role-id :field field :write true} *datasource*)
      (is (thrown? Exception
                   (repositories.permissions/save-new-permission!
                     {:role-id role-id :field field :write false} *datasource*)))))

  (testing "rejects an unknown role-id (foreign key constraint)"
    (is (thrown? Exception
                 (repositories.permissions/save-new-permission!
                   {:role-id (random-uuid) :field (random-field-name) :write true}
                   *datasource*)))))

(deftest get-permission-by-id-test
  (testing "returns the previously saved permission for its id"
    (let [role-id (create-role!)
          saved   (repositories.permissions/save-new-permission!
                     {:role-id role-id :field (random-field-name) :write false}
                     *datasource*)]
      (is (= saved (repositories.permissions/get-permission-by-id (:id saved) *datasource*)))))

  (testing "returns an empty map when no permission matches the id"
    (is (= {} (repositories.permissions/get-permission-by-id (random-uuid) *datasource*)))))
