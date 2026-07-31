(ns clj-ppl-manager.core
  (:require [clj-ppl-manager.components.pedestal :as pedestal-component]
            [com.stuartsierra.component :as component]))

(defn app-system
  [config]
  (component/system-map
    ;; :data-source (datasource-component/datasource-component config)
    :pedestal-component
    (component/using
      (pedestal-component/new-pedestal-component config)
      [])))
