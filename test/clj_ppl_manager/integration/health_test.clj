(ns clj-ppl-manager.integration.health-test
  (:require [clj-ppl-manager.core :as core]
            [clj-ppl-manager.integration.aux.component :as aux.component]
            [clj-ppl-manager.components.pedestal :refer [url-for]]
            [clj-http.client :as client]
            [clojure.test :refer :all]))

(deftest health-test
  (aux.component/with-system
    [sut (core/base-test-app-system (aux.component/test-config))]
    (testing "that health endpoint returns a valid response"
      (is (= {:body   "{\"status\":\"OK\"}"
              :status 200}
             (-> (aux.component/sut->url sut (url-for :health))
                 (client/get {:accept :json})
                 (select-keys [:body :status])))))))
