(ns clj-ppl-manager.auth
  (:require [buddy.hashers :as hashers]
            [buddy.sign.jwt :as jwt]))

(defn hash-password
  {:malli/schema [:=> [:cat :string] :string]}
  [password]
  (hashers/derive password {:alg :argon2id}))

(defn correct-password?
  {:malli/schema [:=> [:cat :string :string] :boolean]}
  [password hashed-password]
  (:valid (hashers/verify password hashed-password)))

(defn generate-token
  {:malli/schema [:=> [:cat :map :string] :string]}
  [{:keys [id role-id email]} secret]
  (jwt/sign {:sub      (str id)
             :role-id  (str role-id)
             :email    email
             :exp      (-> (java.time.Instant/now) (.plusSeconds 3600) .getEpochSecond)}
            secret
            {:alg :hs512}))
