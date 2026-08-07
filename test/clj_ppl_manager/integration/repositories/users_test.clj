(ns clj-ppl-manager.integration.repositories.users-test
  (:require [clj-ppl-manager.integration.aux.component :as aux.component]
            [clj-ppl-manager.repositories.roles :as repositories.roles]
            [clj-ppl-manager.repositories.users :as repositories.users]
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

(defn random-email
  []
  (str "user-" (random-uuid) "@example.com"))

(defn create-role!
  []
  (:id (repositories.roles/save-new-role! (random-role-name) *datasource*)))

(deftest save-new-user!-test
  (testing "persists a user and returns it with a generated id"
    (let [role-id (create-role!)
          email   (random-email)
          saved   (repositories.users/save-new-user!
                     {:role-id role-id :email email :password "hashed-password"}
                     *datasource*)]
      (is (uuid? (:id saved)))
      (is (= role-id (:role-id saved)))
      (is (= email (:email saved)))
      (is (= "hashed-password" (:password saved)))))

  (testing "rejects a duplicate email (unique constraint)"
    (let [role-id (create-role!)
          email   (random-email)]
      (repositories.users/save-new-user!
        {:role-id role-id :email email :password "a"} *datasource*)
      (is (thrown? Exception
                   (repositories.users/save-new-user!
                     {:role-id role-id :email email :password "b"} *datasource*)))))

  (testing "rejects an unknown role-id (foreign key constraint)"
    (is (thrown? Exception
                 (repositories.users/save-new-user!
                   {:role-id (random-uuid) :email (random-email) :password "a"}
                   *datasource*)))))

(deftest get-user-by-id-test
  (testing "returns the previously saved user for its id"
    (let [role-id (create-role!)
          saved   (repositories.users/save-new-user!
                     {:role-id role-id :email (random-email) :password "a"}
                     *datasource*)]
      (is (= saved (repositories.users/get-user-by-id (:id saved) *datasource*)))))

  (testing "returns an empty map when no user matches the id"
    (is (= {} (repositories.users/get-user-by-id (random-uuid) *datasource*)))))
