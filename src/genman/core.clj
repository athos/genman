(ns genman.core
  (:require [clojure.spec.alpha :as s]))

(def %gen-groups (atom {}))
(def ^:dynamic *gen-group* :default)

(def GENERATORS_SYM (gensym 'generators))

(defmacro defgenerator
  ([spec-name-or-path generator]
   `(defgenerator *gen-group* ~spec-name-or-path ~generator))
  ([gen-group spec-name-or-path generator]
   `(do (swap! %gen-groups assoc-in [~gen-group '~spec-name-or-path]
               (fn [] ~generator))
        '~spec-name-or-path)))

(defmacro with-gen-group [gen-group & body]
  `(binding [*gen-group* ~gen-group]
     ~@body))

(defmacro use-gen-group [gen-group & body]
  `(let [~GENERATORS_SYM (get @%gen-groups ~gen-group {})]
     ~@body))

(defmacro gen
  ([spec] `(gen ~spec {}))
  ([spec overrides]
   `(let [overrides# (merge ~(if (get &env GENERATORS_SYM)
                               GENERATORS_SYM
                               `(get @%gen-groups *gen-group* {}))
                            ~overrides)]
      (s/gen ~spec overrides#))))
