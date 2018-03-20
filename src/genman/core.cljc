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

(defmacro def-gen-group [name gen-group]
  `(let [group-name# '~name]
     (swap! internal/%gen-groups assoc group-name#
            (->overrides-map ~gen-group))
     group-name#))

(defmacro with-gen-group [gen-group & body]
  `(binding [internal/*gen-group* ~gen-group]
     ~@body))

(defmacro use-gen-group [gen-group & body]
  `(let [~GENERATORS_SYM (->overrides-map ~gen-group)]
     ~@body))

(defmacro gen
  ([spec] `(gen ~spec {}))
  ([spec overrides]
   `(let [overrides# (merge ~(if (get #?(:clj (or (:locals &env) &env)
                                         :cljs (:locals &env))
                                      GENERATORS_SYM)
                               GENERATORS_SYM
                               `(->overrides-map internal/*gen-group*))
                            ~overrides)]
      (s/gen ~spec overrides#))))

#?(:clj
   (defn ->overrides-map [gen-group]
     (proto/->overrides-map* gen-group)))

#?(:clj
   (defn merge-groups [& gen-groups]
     (proto/->Merge gen-groups)))

#?(:clj
   (defn extend-group [gen-group extension]
     (proto/->Extend gen-group extension)))
