(ns clj-ppl-manager.components.http
  (:require [clj-ppl-manager.routes]
            [clojure.string :as str]
            [com.stuartsierra.component :as component]
            [muuntaja.core :as muuntaja]
            [org.httpkit.server :as http-kit]
            [reitit.coercion.malli :as reitit.malli]
            [reitit.core :as r]
            [reitit.ring :as ring]
            [reitit.ring.coercion :as ring.coercion]
            [reitit.ring.middleware.muuntaja :as muuntaja.middleware]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]))

(def ^:private acceptable-media-types #{"application/json" "text/html"})

(defn- acceptable?
  [accept-header]
  (or (str/blank? accept-header)
      (some (fn [media-type] (or (= media-type "*/*") (contains? acceptable-media-types media-type)))
            (->> (str/split accept-header #",")
                 (map #(-> % (str/split #";") first str/trim))))))

(defn wrap-accept-negotiation
  [handler]
  (fn [request]
    (if (acceptable? (get-in request [:headers "accept"]))
      (handler request)
      {:status 406 :body "Not Acceptable"})))

(defn wrap-secure-headers
  [handler]
  (fn [request]
    (update (handler request) :headers merge
            {"X-Content-Type-Options"   "nosniff"
             "X-Frame-Options"          "DENY"
             "X-XSS-Protection"         "1; mode=block"
             "Content-Security-Policy"  "object-src 'none'"})))

(defn- inject-dependencies
  [handler dependencies]
  (fn [request]
    (handler (merge request dependencies))))

(def ^:private swagger-route
  ["/swagger.json"
   {:get {:no-doc  true
          :swagger {:info {:title "clj-ppl-manager API"}}
          :handler (swagger/create-swagger-handler)}}])

(defn- app
  [dependencies]
  (-> (ring/ring-handler
        (ring/router
          (into [swagger-route] clj-ppl-manager.routes/combined-routes)
          {:data {:coercion   reitit.malli/coercion
                  :muuntaja   muuntaja/instance
                  :middleware [swagger/swagger-feature
                               muuntaja.middleware/format-negotiate-middleware
                               muuntaja.middleware/format-response-middleware
                               muuntaja.middleware/format-request-middleware
                               ring.coercion/coerce-exceptions-middleware
                               ring.coercion/coerce-response-middleware
                               #(inject-dependencies % dependencies)]}})
        (ring/routes
          (swagger-ui/create-swagger-ui-handler {:path "/swagger-ui"})
          (ring/create-default-handler)))
      wrap-accept-negotiation
      wrap-secure-headers))

(def ^:private router
  (ring/router clj-ppl-manager.routes/combined-routes))

(defn url-for
  [route-name & {:keys [path-params]}]
  (:path (r/match-by-name router route-name path-params)))

(defrecord HttpComponent
  [config datasource]
  component/Lifecycle

  (start [component]
    (println ";; Starting HttpComponent")
    (let [dependencies {:datasource  (when-let [datasource-component (:datasource component)]
                                        (datasource-component))
                        :jwt-secret  (-> config :jwt :secret)}
          server       (http-kit/run-server (app dependencies)
                                             {:port                 (-> config :server :port)
                                              :legacy-return-value? false})]
      (assoc component :server server)))

  (stop [component]
    (println ";; Stopping HttpComponent")
    (when-let [server (:server component)]
      (http-kit/server-stop! server))
    (assoc component :server nil)))

(defn new-http-component
  [config]
  (map->HttpComponent {:config config}))
