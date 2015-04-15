(ns slides.present
  (:require
   [sablono.core :as sab :include-macros true]))

(defmulti slide (fn [a] a))

(defmethod slide :default [s]
  (sab/html [:div "empty"]))

(defn section [nm state content]
  (sab/html
   [:section
    {:style {:-webkit-transform (str "translateX(" (* (:spos state) 960)  "px)")}
     :key nm
     :className
     (str
      "dbg-borde bg-color "
      (if (:last-slide state)
        (str "last"
             (when (:animate state)
               " last-animate"))
        (str "current"
             (when (:animate state)
               " current-animate"))))}
    content]))

(defn on [num state content]
  (if (>= (:ins-counter state) num)
    content
    [:span]))

(defn only [num state content]
  (if (= (:ins-counter state) num)
    content
    [:span]))
