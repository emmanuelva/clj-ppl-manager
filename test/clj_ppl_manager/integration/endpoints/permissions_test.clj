(ns clj-ppl-manager.integration.endpoints.permissions-test
  (:require [cheshire.core :as json]
            [clj-http.client :as client]
            [clj-ppl-manager.components.pedestal :refer [url-for]]
            [clj-ppl-manager.integration.aux.component :as aux.component]
            [clojure.test :refer :all]))

(def ^:dynamic *sut* nil)

(use-fixtures :once
  (fn [f]
    (aux.component/with-app-system
      (fn [sut]
        (binding [*sut* sut]
          (f))))))

(defn url
  [path]
  (aux.component/sut->url *sut* path))

(defn post!
  [path body]
  (client/post (url path)
               {:accept           :json
                :content-type     :json
                :body             (json/encode body)
                :throw-exceptions false}))

(defn create-role!
  []
  (-> (post! (url-for :rest.roles/create) {:name (str "role-" (random-uuid))})
      :body
      (json/decode true)))

(defn create-permission!
  [body]
  (post! (url-for :rest.permissions/create) body))

(defn get-permission
  [id]
  (client/get (url (url-for :rest.permissions/get :path-params {:id (str id)}))
              {:accept           :json
               :throw-exceptions false}))

(deftest create-permission-test
  (testing "creates a permission for an existing role and returns 201"
    (let [role     (create-role!)
          response (create-permission! {:role-id (:id role) :field "salary" :write true})
          body     (json/decode (:body response) true)]
      (is (= 201 (:status response)))
      (is (= (:id role) (:role-id body)))
      (is (= "salary" (:field body)))
      (is (true? (:write body)))
      (is (some? (:id body)))))

  (testing "rejects a permission for a role that does not exist with 422"
    (let [response (create-permission! {:role-id (str (random-uuid)) :field "salary" :write true})]
      (is (= 422 (:status response)))))

  (testing "rejects an invalid payload with 422"
    (let [role     (create-role!)
          response (create-permission! {:role-id (:id role) :field "salary" :write "not-a-bool"})]
      (is (= 422 (:status response))))))

(deftest get-permission-test
  (testing "returns a previously created permission"
    (let [role     (create-role!)
          created  (-> (create-permission! {:role-id (:id role) :field "salary" :write false})
                        :body
                        (json/decode true))
          response (get-permission (:id created))
          body     (json/decode (:body response) true)]
      (is (= 200 (:status response)))
      (is (= (:id created) (:id body)))
      (is (= (:id role) (:role-id body)))
      (is (= "salary" (:field body)))
      (is (false? (:write body)))))

  (testing "returns 404 for an id with no matching permission"
    (let [response (get-permission (random-uuid))]
      (is (= 404 (:status response)))))

  (testing "returns 400 for a malformed id"
    (let [response (get-permission "not-a-uuid")]
      (is (= 400 (:status response))))))
