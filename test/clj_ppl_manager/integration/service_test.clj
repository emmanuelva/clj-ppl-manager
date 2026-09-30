(ns clj-ppl-manager.integration.service-test
  (:require [clj-ppl-manager.core :as core]
            [clj-ppl-manager.integration.aux.component :as aux.component]
            [clj-ppl-manager.components.http :refer [url-for]]
            [clj-http.client :as client]
            [clojure.test :refer :all]))

(deftest content-negotiation-test
  (aux.component/with-system
    [sut (core/base-test-app-system (aux.component/test-config))]
    (testing "only application/json is accepted"
      (is (= {:body   "{\"status\":\"OK\"}"
              :status 200}
             (-> (aux.component/sut->url sut (url-for :health))
                 (client/get {:accept           :json
                              :throw-exceptions false})
                 (select-keys [:body :status]))))
      (is (= {:body   "Not Acceptable"
              :status 406}
             (-> (aux.component/sut->url sut (url-for :health))
                 (client/get {:accept           :edn
                              :throw-exceptions false})
                 (select-keys [:body :status])))))))
