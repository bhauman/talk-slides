(ns slides.present
  (:require
   [sablono.core :as sab :include-macros true]))

(defmulti slide (fn [a] a))

(defn section [nm state content]
  (sab/html
   [:section
    {:style {:display "block"} :key nm
     :className
     (str
      "dbg-border bg-color "
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
