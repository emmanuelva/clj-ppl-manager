(ns clj-ppl-manager.integration.endpoints.auth-test
  (:require [cheshire.core :as json]
            [clj-http.client :as client]
            [clj-ppl-manager.auth :as auth]
            [clj-ppl-manager.components.pedestal :refer [url-for]]
            [clj-ppl-manager.integration.aux.component :as aux.component]
            [clj-ppl-manager.repositories.roles :as repositories.roles]
            [clj-ppl-manager.repositories.users :as repositories.users]
            [clojure.test :refer :all]))

(def ^:dynamic *sut* nil)
(def ^:dynamic *datasource* nil)

(use-fixtures :once
  (fn [f]
    (aux.component/with-app-system
      (fn [sut]
        (binding [*sut*        sut
                  *datasource* ((:datasource sut))]
          (f))))))

(defn url
  [path]
  (aux.component/sut->url *sut* path))

(defn post-login!
  [body]
  (client/post (url (url-for :rest.auth/login))
               {:accept           :json
                :content-type     :json
                :body             (json/encode body)
                :throw-exceptions false}))

(defn create-user!
  [password]
  (let [role  (repositories.roles/save-new-role! (str "role-" (random-uuid)) *datasource*)
        email (str "user-" (random-uuid) "@example.com")]
    (repositories.users/save-new-user! {:role-id  (:id role)
                                         :email    email
                                         :password (auth/hash-password password)}
                                        *datasource*)))

(deftest login-test
  (testing "returns a JWT token for valid credentials"
    (let [password "correct-horse-battery-staple"
          user     (create-user! password)
          response (post-login! {:email (:email user) :password password})
          body     (json/decode (:body response) true)]
      (is (= 200 (:status response)))
      (is (string? (:token body)))))

  (testing "returns 401 for a wrong password"
    (let [password "correct-horse-battery-staple"
          user     (create-user! password)
          response (post-login! {:email (:email user) :password "wrong-password"})]
      (is (= 401 (:status response)))))

  (testing "returns 401 for an unknown email"
    (let [response (post-login! {:email (str (random-uuid) "@example.com") :password "whatever"})]
      (is (= 401 (:status response)))))

  (testing "returns 422 for a missing password"
    (let [response (post-login! {:email "someone@example.com"})]
      (is (= 422 (:status response))))))
