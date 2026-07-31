(ns clj-ppl-manager.api.health
  (:require [clj-ppl-manager.api.helpers :as api.helpers]))

(defn respond-ok
  [_]
  (api.helpers/ok {:status "OK"}))
