(ns clj-ppl-manager.repositories.people
  (:require [clj-ppl-manager.repositories.utils :as repositories.utils]
            [honey.sql :as sql]
            [next.jdbc :as jdbc]))

(defn save-new-person!
  "Attempt to save a new person"
  {:malli/schema [:=> [:cat :clj-ppl-manager/new-person :any] :clj-ppl-manager/person]}
  [{:keys [name dob day-of-birth month-of-birth gender email phone]} datasource]
  (let [columns [:id :name :dob :day_of_birth :month_of_birth :gender :email :phone]
        values [(random-uuid) name dob day-of-birth month-of-birth [:cast gender :gender] email phone]
        query (-> {:insert-into [:people]
                   :columns     columns
                   :values      [values]}
                  (sql/format))]
    (-> (jdbc/execute-one!
          datasource
          query
          {:return-keys true})
        (repositories.utils/normalize-db-response))))

(defn get-person-by-id
  "Attempt to get a person by id"
  {:malli/schema [:=> [:cat :uuid :any] :clj-ppl-manager/person]}
  [id datasource]
  (let [query (-> {:select [:id :name :dob :day_of_birth :month_of_birth :gender :email :phone]
                   :from   :people
                   :where  [:= :id id]}
                  (sql/format))]
    (-> (jdbc/execute-one!
          datasource
          query
          {:return-keys true})
        (repositories.utils/normalize-db-response))))
