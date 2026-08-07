(ns clj-ppl-manager.repositories.utils
  (:require [clojure.string]))

(defn underscore-to-dash-keys [m]
  (into {}
        (for [[k v] m]
          (let [new-k (-> k name (clojure.string/replace "_" "-") keyword)]
            [new-k v]))))

(defn remove-namespace-from-keys [m]
  (update-keys m (comp keyword name)))

(defn normalize-db-response
  [response]
  (-> response
      remove-namespace-from-keys
      underscore-to-dash-keys))