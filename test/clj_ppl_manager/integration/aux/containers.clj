(ns clj-ppl-manager.integration.aux.containers
  (:import (org.testcontainers.containers PostgreSQLContainer)))

(defn create-database-container
  []
  (PostgreSQLContainer. "postgres:17"))
