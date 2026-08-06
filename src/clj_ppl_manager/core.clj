(ns clj-ppl-manager.core
  (:require [clj-ppl-manager.components.pedestal :as component.pedestal]
            [clj-ppl-manager.components.datasource :as components.datasource]
            [com.stuartsierra.component :as component]))

(defn app-system
  [config]
  (component/system-map
    :datasource (components.datasource/datasource-component config)
    :pedestal-component
    (component/using
      (component.pedestal/new-pedestal-component config)
      [:datasource])))

(defn base-test-app-system
  [config]
  (component/system-map
    :pedestal-component
    (component/using
      (component.pedestal/new-pedestal-component config)
      [])))
