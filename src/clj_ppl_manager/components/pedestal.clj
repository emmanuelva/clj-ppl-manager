(ns clj-ppl-manager.components.pedestal
  (:require [com.stuartsierra.component :as component]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.http-kit :as hk]
            [io.pedestal.connector :as conn]
            [io.pedestal.interceptor :as interceptor]
            [io.pedestal.http.content-negotiation :as content-negotiation]
            [clj-ppl-manager.routes]))

(def url-for (route/url-for-routes (route/expand-routes clj-ppl-manager.routes/combined-routes)))

(defn inject-dependencies [dependencies]
  (interceptor/interceptor
    {:name  ::inject-dependencies
     :enter (fn [context]
              (update context :dependencies merge dependencies))}))

(def content-negotiation-interceptor
  (content-negotiation/negotiate-content ["application/json", "text/html"]))

(defrecord PedestalComponent
  [config
   in-memory-state-component]
  component/Lifecycle

  (start [component]
    (println ";; Starting PedestalComponent")
    (let [server (-> {:host "localhost"
                      :type   :jetty
                      :join?  false
                      :port   (-> config :server :port)
                      :router :map-tree
                      :secure-headers {:content-security-policy-settings {:object-src "none"}}}
                     (conn/with-default-interceptors)
                     (update :interceptors concat
                             [content-negotiation-interceptor
                              (inject-dependencies {:datasource (when-let [datasource-component (:datasource component)]
                                                                   (datasource-component))})])
                     (conn/with-routes clj-ppl-manager.routes/combined-routes)
                     (hk/create-connector nil)
                     (conn/start!))]
      (assoc component :connector server)))

  (stop [component]
    (println ";; Stopping PedestalComponent")
    (when-let [connector (:connector component)]
      (conn/stop! connector))
    (assoc component :connector nil)))

(defn new-pedestal-component
  [config]
  (map->PedestalComponent {:config config}))

