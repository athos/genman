(ns genman.core
  (:require-macros genman.core)
  (:require genman.internal
            [genman.protocols :as proto]))

(defrecord Merge [gen-groups]
  proto/ToGenGroup
  (->gen-group [this]
    (into {} (map proto/->gen-group) gen-groups)))

(defn merge-groups [& gen-groups]
  (->Merge gen-groups))
