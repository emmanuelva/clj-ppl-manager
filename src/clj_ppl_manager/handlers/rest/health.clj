(ns clj-ppl-manager.handlers.rest.health
  (:require [clj-ppl-manager.handlers.rest.helpers :as rest.helpers]))

(defn respond-ok
  [_]
  (rest.helpers/ok {:status "OK"}))
