(ns clj-ppl-manager.config
  (:import (io.github.cdimascio.dotenv Dotenv)))

(defonce ^:private env
         (-> (Dotenv/configure)
             (.ignoreIfMissing)
             (.load)))

(defn- env-get
  [k default]
  (or (.get env k) default))

(defn system-config
  []
  {:server  {:port (Integer/parseInt (env-get "PORT" "3001"))}
   :db-spec {:jdbcUrl  (str "jdbc:postgresql://"
                            (env-get "DATABASE_HOST" "localhost") ":"
                            (env-get "DATABASE_PORT" "5432") "/"
                            (env-get "DATABASE_NAME" "ecommerce"))
             :username (env-get "DATABASE_USERNAME" "postgres")
             :password (env-get "DATABASE_PASSWORD" "postgres")}})

