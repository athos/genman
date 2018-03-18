(ns genman.core
  (:require [clojure.spec.alpha :as s]
            [genman.internal :as internal]
            [genman.protocols :as proto]))

(def ^:private GENERATORS_SYM (gensym 'generators))

(defmacro defgenerator
  ([spec-name-or-path generator]
   `(defgenerator internal/*gen-group* ~spec-name-or-path ~generator))
  ([gen-group spec-name-or-path generator]
   `(let [gen-group# ~gen-group
          spec-name# '~spec-name-or-path]
      (assert (keyword? gen-group#) "gen-group must be keyword")
      (swap! internal/%gen-groups assoc-in [gen-group# spec-name#]
             (fn [] ~generator))
      spec-name#)))

(defmacro with-gen-group [gen-group & body]
  `(binding [internal/*gen-group* ~gen-group]
     ~@body))

(defmacro use-gen-group [gen-group & body]
  `(let [~GENERATORS_SYM (proto/->gen-group ~gen-group)]
     ~@body))

(defmacro gen
  ([spec] `(gen ~spec {}))
  ([spec overrides]
   `(let [overrides# (merge ~(if (get #?(:clj &env
                                         :cljs (:locals &env))
                                      GENERATORS_SYM)
                               GENERATORS_SYM
                               `(proto/->gen-group internal/*gen-group*))
                            ~overrides)]
      (s/gen ~spec overrides#))))

#?(:clj
   (defrecord Merge [gen-groups]
     proto/ToGenGroup
     (->gen-group [this]
       (into {} (map proto/->gen-group) gen-groups))))

#?(:clj
   (defn merge-groups [& gen-groups]
     (->Merge gen-groups)))
