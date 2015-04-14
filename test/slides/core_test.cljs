(ns slides.core-test
  (:require
   [cljs.test :refer-macros [deftest testing is]]
   [slides.core :refer [translation]]))

(deftest translate-test
  (let [d {:counter 1}]
    (is (= (translation d) " translateX(-960px) "))
    (is (= (translation {:counter 0}) " translateX(0px) "))
    (is (= (translation {:counter 2}) " translateX(-1920px) "))
    (is (= (translation {:counter 0
                         :trans-z 2500 })
           " translateX(0px) translateZ(2500px) "))))

