# Genman
[![Clojars Project](https://img.shields.io/clojars/v/genman.svg)](https://clojars.org/genman)
[![CircleCI](https://circleci.com/gh/athos/genman.svg?style=shield)](https://circleci.com/gh/athos/genman)

Generator management utility for clojure.spec

## Features

- Generator definitions isolated from spec definitions
    - No more need to mess up your spec definition with a gigantic `s/with-gen` code!
- Provides switching mechanism between multiple generator implementations for a single spec

## Installation

Add the following to your `:dependencies`:

[![Clojars Project](https://clojars.org/genman/latest-version.svg)](https://clojars.org/genman)

## Usage

- [`defgenerator` / `gen`](#defgenerator--gen)
- [`with-gen-group` / `use-gen-group`](#with-gen-group--use-gen-group)
- [`merge-groups` / `extend-group`](#merge-groups--extend-group)

### `defgenerator` / `gen`

First, define a generator which you want to use via Genman as follows:

```clj
(require '[clojure.spec.alpha :as s]
         '[clojure.test.check.generators :as gen]
         '[genman.core :as genman :refer [defgenerator]])

(s/def ::id int?)
(s/def ::name string?)

(defgenerator ::id
  (s/gen #{0 1 2}))
```

Once a generator is defined using `defgenerator`, you can use it with `genman/gen` instead of `s/gen`:

```clj
(gen/generate (genman/gen ::id))
;; => 0

(gen/generate (genman/gen ::id))
;; => 2

(gen/generate (genman/gen ::id))
;; => 1
```

### `with-gen-group` / `use-gen-group`

In addition, Genman provieds switching mechanism between multiple generator implementations for a single spec. To use this facility, a generator should be defined in a **generator group**. In the example below, two more implementations for each `::id` and `::name` are being defined in the generator groups named `:dev` and `:test`:

```clj
(genman/with-gen-group :dev
  (defgenerator ::id
    (gen/return 42))

  (defgenerator ::name
    (gen/return "foo")))

(genman/with-gen-group :test
  (defgenerator ::id
    (gen/return 101))

  (defgenerator ::name
    (gen/return "bar")))
```

And then, specify the generator group with `with-gen-group` to choose those specific implementations:

```clj
(genman/with-gen-group :dev
  (gen/generate (genman/gen (s/tuple ::id ::name))))
;; => [42 "foo"]

(genman/with-gen-group :test
  (gen/generate (genman/gen (s/tuple ::id ::name))))
;; => [101 "bar"]
```

If no generator group is specified, `genman/gen` and `defgenerator` will behave as if the `:default` generator group were specified.

Note that since `with-gen-group` is built on top of dynamic var binding, once a generator got out of that scope, it could lose the effect of specifying the generator group:

```clj
(def g
  (genman/with-gen-group :dev
    (fn []
      (genman/gen (s/tuple ::id ::name)))))

(gen/generate (g))
;; => [0 ""]
(gen/generate (g))
;; => [2 "O2ltmsM"]
```

To avoid this behavior, use `use-gen-group` instead:

```clj
(def g
  (genman/use-gen-group :dev
    (fn []
      (genman/gen (s/tuple ::id ::name)))))

(gen/generate (g))
;; => [42 "foo"]
(gen/generate (g))
;; => [42 "foo"]
```

As a rule of thumb, `with-gen-group` works well for test fixtures, and `use-gen-group` suits for use in each (property-based) test case.

### `merge-groups` / `extend-group`

Also, there are some ways to create a new generator group based on existing ones (which we call an *adhoc* generator group).

`merge-groups` merges more than one generator groups (in the left-to-right manner as with `clojure.core/merge`):

```clj
(genman/with-gen-group :test1
  (defgenerator ::id
    (gen/return 42))
    
  (defgenerator ::name
    (gen/return "foo")))

(genman/with-gen-group :test2
  (defgenerator ::name
    (gen/return "bar")))
    
(genman/with-gen-group (genman/merge-groups :test1 :test2)
  (gen/generate (genman/gen (s/tuple ::id ::name))))
;; => [42 "bar"]
```

Or, you can simply pass a map, from spec name keys to fns returning a generator, to override an existing generator group:

```clj
(genman/with-gen-group (genman/merge-groups :test1 {::name #(gen/return "baz")})
  (gen/generate (genman/gen (s/tuple ::id ::name))))
;; => [42 "baz"]
```

If you would like to wrap the existing gererator implementation instead, `extend-group` would be useful:

```clj
(genman/with-gen-group (genman/extend-group :test1 {::id (fn [g] (gen/fmap #(* % 100) g))})
  (gen/generate (genman/gen ::id)))
;; => 4200
```

## License

Copyright © 2018 Shogo Ohta

Distributed under the Eclipse Public License version 1.0.
