(ns clj-ppl-manager.integration.migrations-test
  (:require [clj-ppl-manager.core :as core]
            [clj-ppl-manager.integration.aux.component :as aux.component]
            [clj-ppl-manager.integration.aux.containers :as aux.containers]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]
            [clojure.test :refer :all]))

(deftest migrations-test
  (let [database-container (aux.containers/create-database-container)]
    (try
      (.start database-container)
      (aux.component/with-system
        [sut (core/app-system (aux.component/test-with-container-config database-container))]
        (clojure.pprint/pprint sut)
        (let [{:keys [datasource]} sut
              [schema-version :as schema-versions]
              (jdbc/execute!
                (datasource)
                ["select * from schema_version"]
                {:builder-fn rs/as-unqualified-lower-maps})]
          (is (< 0 (count schema-versions)))
          (is (= {:description "create roles table"
                  :script "V1__create_roles_table.sql"
                  :success true}
                 (select-keys schema-version [:description :script :success])))))
      (finally
        (.stop database-container)))))
