(ns genman.test-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            genman.core-test))

(enable-console-print!)

(doo-tests 'genman.core-test)
