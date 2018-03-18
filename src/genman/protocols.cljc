(ns genman.protocols
  (:require [genman.internal :as internal]))

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
