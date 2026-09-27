(ns clj-ppl-manager.repositories.users
  (:require [clj-ppl-manager.repositories.utils :as repositories.utils]
            [honey.sql :as sql]
            [next.jdbc :as jdbc]))

(defn save-new-user!
  "Attempt to save a new user"
  {:malli/schema [:=> [:cat :clj-ppl-manager/new-user :any] :clj-ppl-manager/user]}
  [{:keys [role-id email password]} datasource]
  (let [columns [:id :role_id :email :password]
        values [(random-uuid) role-id email password]
        query (-> {:insert-into [:users]
                   :columns     columns
                   :values      [values]}
                  (sql/format))]
    (-> (jdbc/execute-one!
          datasource
          query
          {:return-keys true})
        (repositories.utils/normalize-db-response))))

(defn get-user-by-id
  "Attempt to get a user by id"
  {:malli/schema [:=> [:cat :uuid :any] :clj-ppl-manager/user]}
  [id datasource]
  (let [query (-> {:select [:id :role_id :email :password]
                   :from   :users
                   :where  [:= :id id]}
                  (sql/format))]
    (-> (jdbc/execute-one!
          datasource
          query
          {:return-keys true})
        (repositories.utils/normalize-db-response))))

(defn get-user-by-email
  "Attempt to get a user by email"
  {:malli/schema [:=> [:cat :string :any] :clj-ppl-manager/user]}
  [email datasource]
  (let [query (-> {:select [:id :role_id :email :password]
                   :from   :users
                   :where  [:= :email email]}
                  (sql/format))]
    (-> (jdbc/execute-one!
          datasource
          query
          {:return-keys true})
        (repositories.utils/normalize-db-response))))
