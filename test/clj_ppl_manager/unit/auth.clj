(ns clj-ppl-manager.unit.auth
  (:require [clj-ppl-manager.auth :as auth]
            [clojure.test :refer :all]))

(deftest hash-password-test
  (testing "returns a hash different from the plaintext password"
    (let [password "correct-horse-battery-staple"
          hashed   (auth/hash-password password)]
      (is (string? hashed))
      (is (not= password hashed))))
  (testing "produces a different hash for the same password each time (salted)"
    (let [password "correct-horse-battery-staple"]
      (is (not= (auth/hash-password password)
                (auth/hash-password password))))))

(deftest correct-password?-test
  (testing "returns true when the password matches the hash"
    (let [password "correct-horse-battery-staple"
          hashed   (auth/hash-password password)]
      (is (true? (auth/correct-password? password hashed)))))
  (testing "returns false when the password does not match the hash"
    (let [hashed (auth/hash-password "correct-horse-battery-staple")]
      (is (false? (auth/correct-password? "wrong-password" hashed))))))
