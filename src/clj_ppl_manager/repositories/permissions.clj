(ns clj-ppl-manager.repositories.permissions
  (:require [clj-ppl-manager.repositories.utils :as repositories.utils]
            [honey.sql :as sql]
            [next.jdbc :as jdbc]))

(defn save-new-permission!
  {:malli/schema [:=> [:cat :clj-ppl-manager/new-permission :any] :clj-ppl-manager/permission]}
  [{:keys [role-id field write]} datasource]
  (let [columns [:id :role_id :field :write]
        values [(random-uuid) role-id field write]
        query (-> {:insert-into [:permissions]
                   :columns     columns
                   :values      [values]}
                  (sql/format))]
    (-> (jdbc/execute-one!
          datasource
          query
          {:return-keys true})
        (repositories.utils/normalize-db-response))))

(defn get-permission-by-id
  {:malli/schema [:=> [:cat :uuid :any] :clj-ppl-manager/permission]}
  [id datasource]
  (let [query (-> {:select [:id :role_id :field :write]
                   :from   :permissions
                   :where  [:= :id id]}
                  (sql/format))]
    (-> (jdbc/execute-one!
          datasource
          query
          {:return-keys true})
        (repositories.utils/normalize-db-response))))

(defn get-permissions-by-role-id
  {:malli/schema [:=> [:cat :uuid :any] [:sequential :clj-ppl-manager/permission]]}
  [role-id datasource]
  (let [query (-> {:select [:id :role_id :field :write]
                   :from   :permissions
                   :where  [:= :role_id role-id]}
                  (sql/format))]
    (->> (jdbc/execute! datasource query)
         (mapv repositories.utils/normalize-db-response))))
