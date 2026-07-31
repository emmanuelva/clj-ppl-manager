(ns clj-ppl-manager.core
  (:require [clj-ppl-manager.components.pedestal :as component.pedestal]
            [clj-ppl-manager.components.datasource :as components.datasource]
            [com.stuartsierra.component :as component]))

(defn app-system
  [config]
  (component/system-map
    :data-source (components.datasource/datasource-component config)
    :pedestal-component
    (component/using
      (component.pedestal/new-pedestal-component config)
      [:data-source])))
