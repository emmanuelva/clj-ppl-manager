(ns clj-ppl-manager.components.datasource
  (:require [clojure.tools.logging :as log]
            [next.jdbc.connection :as connection]
            [clojure.string :refer (split)]
            [clojure.java.io :as java.io])
  (:import (com.zaxxer.hikari HikariDataSource)
           (org.flywaydb.core Flyway)))

(defn get-migrations-path []
  (-> (java.io/resource "database/migrations")
      str
      (split #":")
      second))

(defn datasource-component
  [config]
  (connection/component
    HikariDataSource
    (assoc
      (:db-spec config)
      :init-fn (fn [datasource]
                 (log/info "Running database init")
                 (.migrate
                   (-> (Flyway/configure)
                       (.dataSource datasource)
                       (.table "schema_version")
                       (.locations (into-array
                                     String
                                     [(str "filesystem:" (get-migrations-path))]))
                       (.load)))))))

