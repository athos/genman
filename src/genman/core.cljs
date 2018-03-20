(ns genman.core
  (:require-macros genman.core)
  (:require genman.internal
            [genman.protocols :as proto]))

(defn merge-groups [& gen-groups]
  (proto/->Merge gen-groups))

(defn extend-group [gen-group extension]
  (proto/->Extend gen-group extension))
