(ns genman.protocols
  (:require [clojure.spec.alpha :as s]
            [genman.internal :as internal]))

(defprotocol ToOverridesMap
  (->overrides-map* [this]))

(extend-protocol ToOverridesMap
  #?(:clj clojure.lang.Keyword
     :cljs cljs.core/Keyword)
  (->overrides-map* [key]
    (get @internal/%gen-groups key {}))

  #?(:clj Object
     :cljs default)
  (->overrides-map* [map] map))

(defrecord Merge [gen-groups]
  ToOverridesMap
  (->overrides-map* [this]
    (into {} (map ->overrides-map*) gen-groups)))

(defrecord Extend [gen-group extension]
  ToOverridesMap
  (->overrides-map* [this]
    (let [base (->overrides-map* gen-group)]
      (reduce-kv (fn [m k f]
                   (let [g (if-let [gen-fn (get base k)]
                             (gen-fn)
                             (s/gen k))
                         g' (f g)]
                     (assoc m k (constantly g'))))
                 base
                 extension))))
