(ns clj-ppl-manager.integration.aux.component
  (:require [clj-ppl-manager.config :as config]
            [clj-ppl-manager.core]
            [clojure.string :as str]
            [clojure.test :refer :all]
            [com.stuartsierra.component :as component])
  (:import (java.net ServerSocket)))

(defmacro with-system
  [[bound-var binding-expr] & body]
  `(let [~bound-var (component/start ~binding-expr)]
     (try
       ~@body
       (finally
         (component/stop ~bound-var)))))

(defn sut->url
  [sut path]
  (str/join ["http://localhost:"
             (-> sut :pedestal-component :config :server :port)
             path]))

(defn get-free-port
  []
  (with-open [socket (ServerSocket. 0)]
    (.getLocalPort socket)))

(defn test-config
  []
  (assoc-in (config/system-config) [:server :port] (get-free-port)))

(defn test-with-container-config
  [database-container]
  (-> (assoc-in (config/system-config) [:server :port] (get-free-port))
      (assoc :db-spec {:jdbcUrl (.getJdbcUrl database-container)
                       :username (.getUsername database-container)
                       :password (.getPassword database-container)})))
