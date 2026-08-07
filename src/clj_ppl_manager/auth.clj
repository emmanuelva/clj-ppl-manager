(ns clj-ppl-manager.auth
  (:require [buddy.hashers :as hashers]))

(defn hash-password
  {:malli/schema [:=> [:cat :string] :string]}
  [password]
  (hashers/derive password {:alg :argon2id}))

(defn correct-password?
  {:malli/schema [:=> [:cat :string :string] :boolean]}
  [password hashed-password]
  (:valid (hashers/verify password hashed-password)))
