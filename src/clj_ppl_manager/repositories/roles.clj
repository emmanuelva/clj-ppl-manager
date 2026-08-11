(ns clj-ppl-manager.repositories.roles
  (:require [clj-ppl-manager.repositories.utils :as repositories.utils]
            [honey.sql :as sql]
            [next.jdbc :as jdbc]))

(defn save-new-role!
  {:malli/schema [:=> [:cat :string :any] :clj-ppl-manager/role]}
  [name datasource]
  (let [columns [:id :name]
        values [(random-uuid) name]
        query (-> {:insert-into [:roles]
                   :columns     columns
                   :values      [values]}
                  (sql/format))]
    (-> (jdbc/execute-one!
          datasource
          query
          {:return-keys true})
        (repositories.utils/normalize-db-response))))

(defn get-role-by-id
  {:malli/schema [:=> [:cat :uuid :any] :clj-ppl-manager/role]}
  [id datasource]
  (let [query (-> {:select [:id :name]
                   :from   :roles
                   :where  [:= :id id]}
                  (sql/format))]
    (-> (jdbc/execute-one!
          datasource
          query
          {:return-keys true})
        (repositories.utils/normalize-db-response))))
