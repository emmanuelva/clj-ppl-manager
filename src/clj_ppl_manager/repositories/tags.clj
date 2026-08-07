(ns clj-ppl-manager.repositories.tags
  (:require [clj-ppl-manager.repositories.utils :as repositories.utils]
            [honey.sql :as sql]
            [next.jdbc :as jdbc]))

(defn save-new-tag!
  "Attempt to save a new tag"
  {:malli/schema [:=> [:cat :clj-ppl-manager/tag :any] :clj-ppl-manager/tag]}
  [{:keys [person-id tag]} datasource]
  (let [columns [:person_id :tag]
        values [person-id tag]
        query (-> {:insert-into [:tags]
                   :columns     columns
                   :values      [values]}
                  (sql/format))]
    (-> (jdbc/execute-one!
          datasource
          query
          {:return-keys true})
        (repositories.utils/normalize-db-response))))

(defn get-tags-by-person-id
  "Attempt to get all tags for a person"
  {:malli/schema [:=> [:cat :uuid :any] [:sequential :clj-ppl-manager/tag]]}
  [person-id datasource]
  (let [query (-> {:select [:person_id :tag]
                   :from   :tags
                   :where  [:= :person_id person-id]}
                  (sql/format))]
    (->> (jdbc/execute!
           datasource
           query)
         (map repositories.utils/normalize-db-response))))
