(ns genman.core-test
  (:require
   [clojure.spec.alpha :as s]
   [clojure.test :refer [is]]
   [clojure.test.check.generators :as gen]
   #?@(:clj
       ([clojure.test.check.clojure-test :refer [defspec]]
        [com.gfredericks.test.chuck.clojure-test :refer [for-all]])
       :cljs
       ([clojure.test.check.clojure-test :refer-macros [defspec]]
        [com.gfredericks.test.chuck.clojure-test :refer-macros [for-all]]))
   [genman.core :as genman :refer [defgenerator def-gen-group]]))

(s/def ::id int?)
(s/def ::name string?)

(defgenerator ::id
  (s/gen (s/int-in 0 10000)))

(genman/with-gen-group :test1
  (defgenerator ::id
    (gen/return 42))

  (defgenerator ::name
    (gen/return "foo")))

(genman/with-gen-group :test2
  (defgenerator ::id
    (s/gen #{0 1 2}))

  (defgenerator ::name
    (gen/fmap #(apply str %)
              (s/gen (s/coll-of char? :count 5)))))

(def-gen-group :test3
  (genman/merge-groups :test2 {::name #(gen/return "bar")}))

(def-gen-group :test4
  (genman/extend-group :test1 {::id #(gen/fmap inc %)}))

(defspec prop-default-gen-group
  (for-all [id (genman/gen ::id)
            name (genman/gen ::name)]
    (is (<= 0 id 9999))
    (is (string? name))))

(defspec prop-test1-gen-group
  (genman/use-gen-group :test1
    (for-all [id (genman/gen ::id)
              name (genman/gen ::name)]
      (is (= id 42))
      (is (= name "foo")))))

(defspec prop-test2-gen-group
  (genman/use-gen-group :test2
    (for-all [id (genman/gen ::id)
              name (genman/gen ::name)]
      (is (contains? #{0 1 2} id))
      (is (= (count name) 5)))))

(defspec prop-test3-gen-group
  (genman/use-gen-group :test3
    (for-all [id (genman/gen ::id)
              name (genman/gen ::name)]
      (is (contains? #{0 1 2} id))
      (is (= name "bar")))))

(defspec prop-test4-gen-group
  (genman/use-gen-group :test4
    (for-all [id (genman/gen ::id)
              name (genman/gen ::name)]
      (is (= id 43))
      (is (= name "foo")))))
