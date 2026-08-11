(ns clj-ppl-manager.integration.endpoints.roles-test
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

(defn random-role-name
  []
  (str "role-" (random-uuid)))

(defn url
  [path]
  (aux.component/sut->url *sut* path))

(defn post-role!
  [body]
  (client/post (url (url-for :rest.roles/create))
               {:accept           :json
                :content-type     :json
                :body             (json/encode body)
                :throw-exceptions false}))

(defn get-role
  [id]
  (client/get (url (url-for :rest.roles/get :path-params {:id (str id)}))
              {:accept           :json
               :throw-exceptions false}))

(deftest create-role-test
  (testing "creates a role and returns 201 with the persisted role"
    (let [name     (random-role-name)
          response (post-role! {:name name})
          body     (json/decode (:body response) true)]
      (is (= 201 (:status response)))
      (is (= name (:name body)))
      (is (some? (:id body)))))

  (testing "rejects an invalid payload with 422"
    (let [response (post-role! {:name ""})]
      (is (= 422 (:status response))))))

(deftest get-role-test
  (testing "returns a previously created role, including its permissions"
    (let [name     (random-role-name)
          created  (json/decode (:body (post-role! {:name name})) true)
          response (get-role (:id created))
          body     (json/decode (:body response) true)]
      (is (= 200 (:status response)))
      (is (= (:id created) (:id body)))
      (is (= name (:name body)))
      (is (= [] (:permissions body)))))

  (testing "returns 404 for an id with no matching role"
    (let [response (get-role (random-uuid))]
      (is (= 404 (:status response)))))

  (testing "returns 400 for a malformed id"
    (let [response (get-role "not-a-uuid")]
      (is (= 400 (:status response))))))
