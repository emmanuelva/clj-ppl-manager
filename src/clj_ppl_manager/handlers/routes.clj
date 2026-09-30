(ns clj-ppl-manager.handlers.routes
  (:require [clj-ppl-manager.controllers.auth :as controllers.auth]
            [clj-ppl-manager.controllers.roles :as controllers.roles]
            [clj-ppl-manager.handlers.auth :as rest.auth]
            [clj-ppl-manager.handlers.permissions :as rest.permissions]
            [clj-ppl-manager.handlers.roles :as rest.roles]
            [clj-ppl-manager.models.permission :as models.permission]
            [clj-ppl-manager.models.role :as models.role]))

(def routes
  [["/login"
    {:name :rest.auth/login
     :post {:summary    "Log in with email and password"
            :parameters {:body controllers.auth/Credentials}
            :handler    rest.auth/login-handler}}]
   ["/roles"
    {:name :rest.roles/create
     :post {:summary    "Create a role"
            :parameters {:body controllers.roles/NewRole}
            :responses  {201 {:body models.role/Role}}
            :handler    rest.roles/create-role-handler}}]
   ["/roles/:id"
    {:name :rest.roles/get
     :get  {:summary    "Get a role by id, including its permissions"
            :parameters {:path [:map [:id :uuid]]}
            :responses  {200 {:body [:map
                                      [:id :uuid]
                                      [:name :string]
                                      [:permissions [:vector models.permission/Permission]]]}}
            :handler    rest.roles/get-role-handler}}]
   ["/permissions"
    {:name :rest.permissions/create
     :post {:summary    "Create a permission"
            :parameters {:body models.permission/NewPermission}
            :responses  {201 {:body models.permission/Permission}}
            :handler    rest.permissions/create-permission-handler}}]
   ["/permissions/:id"
    {:name :rest.permissions/get
     :get  {:summary    "Get a permission by id"
            :parameters {:path [:map [:id :uuid]]}
            :responses  {200 {:body models.permission/Permission}}
            :handler    rest.permissions/get-permission-handler}}]])
