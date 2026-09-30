(ns clj-ppl-manager.core
  (:require [clj-ppl-manager.components.http :as components.http]
            [clj-ppl-manager.components.datasource :as components.datasource]
            [com.stuartsierra.component :as component]))

(defn app-system
  [config]
  (component/system-map
    :datasource (components.datasource/datasource-component config)
    :http-component
    (component/using
      (components.http/new-http-component config)
      [:datasource])))

(defn base-test-app-system
  [config]
  (component/system-map
    :http-component
    (component/using
      (components.http/new-http-component config)
      [])))
