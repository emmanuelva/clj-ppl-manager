(ns clj-ppl-manager.handlers.health
  (:require [clj-ppl-manager.handlers.helpers :as rest.helpers]))

(defn respond-ok
  [_]
  (rest.helpers/ok {:status "OK"}))
