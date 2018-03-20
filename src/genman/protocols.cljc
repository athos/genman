(ns genman.protocols
  (:require [clojure.spec.alpha :as s]
            [genman.internal :as internal]))

(defprotocol ToGenGroup
  (->gen-group [this]))

(extend-protocol ToGenGroup
  #?(:clj clojure.lang.Keyword
     :cljs cljs.core/Keyword)
  (->gen-group [key]
    (get @internal/%gen-groups key {}))

  #?(:clj Object
     :cljs default)
  (->gen-group [map] map))

(defrecord Merge [gen-groups]
  ToGenGroup
  (->gen-group [this]
    (into {} (map ->gen-group) gen-groups)))

(defrecord Extend [gen-group extension]
  ToGenGroup
  (->gen-group [this]
    (let [base (->gen-group gen-group)]
      (reduce-kv (fn [m k f]
                   (let [g (if-let [gen-fn (get base k)]
                             (gen-fn)
                             (s/gen k))
                         g' (f g)]
                     (assoc m k (constantly g'))))
                 base
                 extension))))
