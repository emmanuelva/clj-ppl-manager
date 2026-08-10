(ns clj-ppl-manager.handlers.rest.routes
  (:require [clj-ppl-manager.handlers.rest.roles :as rest.roles]))

(def routes
  #{["/roles" :post rest.roles/create-role-handler :route-name :rest.roles/create]
    ["/roles/:id" :get rest.roles/get-role-handler :route-name :rest.roles/get]})
