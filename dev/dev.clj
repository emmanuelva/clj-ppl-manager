(ns dev
  (:require [com.stuartsierra.component.repl :as component-repl]
            [clj-ppl-manager.core :as  core]))

(component-repl/set-init
  (fn [_old-system]
    (core/app-system
      {:server  {:port 3001}
       :db-spec {:jdbcUrl  "jdbc:postgresql://localhost:5432/pplmanager"
                 :username "postgres"
                 :password "postgres"}})))
