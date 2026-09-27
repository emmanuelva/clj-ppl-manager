(ns clj-ppl-manager.handlers.rest.routes
  (:require [clj-ppl-manager.handlers.rest.auth :as rest.auth]
            [clj-ppl-manager.handlers.rest.permissions :as rest.permissions]
            [clj-ppl-manager.handlers.rest.roles :as rest.roles]))

(def routes
  #{["/login" :post rest.auth/login-handler :route-name :rest.auth/login]
    ["/roles" :post rest.roles/create-role-handler :route-name :rest.roles/create]
    ["/roles/:id" :get rest.roles/get-role-handler :route-name :rest.roles/get]
    ["/permissions" :post rest.permissions/create-permission-handler :route-name :rest.permissions/create]
    ["/permissions/:id" :get rest.permissions/get-permission-handler :route-name :rest.permissions/get]})
