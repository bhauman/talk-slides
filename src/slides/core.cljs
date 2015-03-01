(ns slides.core
    (:require
     [om.core :as om :include-macros true]
     [om.dom :as dom :include-macros true]
     [ankha.core :as ankha]
     [sablono.core :as sab :include-macros true]
     [slides.todos :refer [todos-component]])
    (:require-macros
     [slides.core :refer [defslide]]))

(enable-console-print!)

(declare get-slide)

;; define your app data so that it doesn't get over-written on reload

(def slide-list [:intro
                 :dome
                 :awesome
                 :not-awesome
                 :bret-victor-1
                 :bret-victor-2
                 :magic
                 :not-magic
                 :what-happens
                 :nothing-fancy
                 :bad-idea
                 :trade-off
                 :the-trade-off
                 :reloadable-code
                 :todos])

(defonce app-state
  (atom { :counter 0
          :ins-counter 0
          :animate true
          :todos-state { :todos []
                         :form-todo {} }}))

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

;; slide definitions

(defslide intro [state]
  [:div.center.top-15
   [:h1 "Developing ClojureScript with "
    [:span.orange "Figwheel"]]
   [:div.green "Bruce Hauman"]
   [:br]
   [:div.blue "@bhauman"]
   [:div.blue "rigsomelight.com"]
   [:div.blue "github.com/bhauman"]])

(defslide dome [state]
  [:div.center.top-5 
   [:img {:src "imgs/dome.jpg"}]
   (only 1 state
         [:div {:style {:fontSize "8em;"
                        :position "absolute"
                        :top "190px"
                        :left "288px"
                        :textShadow "0px 0px 20px #000"}}
          "(" [:span {:style
                      {:display "inline-block"
                       :width "200px"}}] ")"])
   (only 2 state
         [:div
          {:style {:position "absolute"
                   :top "213px"
                   :left "366px"
                   :opacity 0.5}}
          [:img  {:src "imgs/Clojure-Logo.png"
                  :width "210px"}]])])

(defslide awesome [state]
  [:div.center.top-5
   [:img {:src "img/rich.jpegg"
          :style { :borderRadius "50%"
                   :boxShadow "0px 0px 10px #000"}
          :width "300"
          :height "300"}]
   [:blockquote "Not everything is awesome."]
   [:div.blue "- Burt Reynolds"]])

(defslide not-awesome [state]
  [:div.center.top-20
   [:blockquote "This experience is "
    [:span.orange "NOT"] " awesome."]
   [:div.blue "- wtf"]])

#_(defslide not-awesome [state]
  [:div.center.top-20
   [:blockquote "State isn't persisting."]])

(defslide bret-victor-1 [state]
  [:div.center.top-20
   [:blockquote
    "A tool addresses human needs by "
    [:span #_.orange "amplifying"]
    " human capabilities."]
   [:div.blue "- Burt Reynolds"]])

(defslide bret-victor-2 [state]
  [:div.center.top-20
   [:blockquote  "a tool converts what we"
    [:span #_.orange " can do"]
    [:br]
    "into what we"
    [:span #_.green " want to do"]]
   [:div.blue "- Burt Reynolds"]])

(defslide magic [state]
  [:div.center.top-20
   [:blockquote "What "
    [:span.orange "Magic"] " is this?"]])

(defslide not-magic [state]
  [:div.center.top-20
   [:blockquote "Not Magic "]
   [:div.blue "Just simple file reloading"]])

(defslide what-happens [state]
  [:div.center.top-15
   [:h2 "When you edit and save a file "
    [:span.orange "Figwheel"] ":"]
   [:div.left-20.left
    (on 1 state
        [:div [:span.green "compiles"]
         " the file"])
    (on 2 state
        [:div [:span.green "sends"]
         " a message to the client"])
    (on 3 state
        [:div "client then "
         [:span.blue "reloads"]
         " the compiled CLJS"])]])

(defslide nothing-fancy [state]
  [:div.center.top-10
   [:img {:src "imgs/super-fancy-boom1.gif"}]
   [:blockquote.orange "Nothing fancy is going on here."]])

(defslide bad-idea [state]
  [:div.center.top-20
   [:blockquote "Is this a Bad Idea?"]
   (on 1 state
       [:div "Nah."])])

(defslide trade-off [state]
  [:div.center.top-20
   [:blockquote "It's a trade off."]])

(defslide the-trade-off [state]
  [:div.center.top-10
   [:blockquote "You get: "
    [:span.green "instantaneous feedback"]]
   [:div.blue "in exchange for"]
   [:blockquote
    "writing " [:span.orange "reloadable code"]]])

(defslide reloadable-code [state]
  [:div.center.top-10
   [:h2 [:span.green "Reloadable code"]
    " is code that"]
   [:div "on reload"]
   [:br]
   [:div #_.left-20.left
    (on 1 state
        [:div "only causes " [:span.orange "expected side effects"] ])
    (on 2 state
        [:div
         [:span.orange "incorporates code changes"]
         " into the running app's behavior"])]])




(defslide todos [state]
  (om/build todos-component (:todos-state state)))

(defn get-slide [data]
  (slide (get slide-list
              (mod (:counter data) (count slide-list)))
         data))

;; this can be a react component

;; handle keyboard events
(defmulti dispatch identity)

(defmethod dispatch :advance [_ data]
  (om/transact! data []
                (fn [s]
                  (let [x (:counter s)]
                    (assoc s
                           :last-count x
                           :ins-counter 0
                           :counter (inc x))))))

(defmethod dispatch :internal-advance [_ data]
  (om/transact! data []
                (fn [s]
                  (let [x (:ins-counter s)]
                    (assoc s :ins-counter (inc x))))))

(defmethod dispatch :internal-back [_ data]
  (om/transact! data []
                (fn [s]
                  (let [x (:ins-counter s)]
                    (assoc s :ins-counter
                           (if (zero? x) 0 (dec x)))))))

(defmethod dispatch :back [_ data]
  (om/transact! data []
                (fn [s]
                  (let [x (:counter s)]
                    (assoc s
                           :last-count x
                           :counter
                           (if (zero? x) 0 (dec x)))))))

(def dispatch-map
  {32 :advance
   39 :advance
   40 :internal-advance
   38 :internal-back   
   37 :back})

(defn key-handler [e]
  (when-let [action (get dispatch-map (.-keyCode e))]
    ;; we can scope the data that goes to the dispatch
    (dispatch action
              ;;; extract path here?
              (om/root-cursor app-state))))

(defonce keypresser
  (do
    (.addEventListener
     (.-body js/document)
     "keyup"
     (fn [e] (key-handler e)))
    true))

(defn inspect-data [data]
  (sab/html
   [:div.ankha {:style {:marginTop "50px"
                        :position "fixed"
                        :bottom "0px"
                        :left "0px"
                        :fontSize "0.4em"}}
    (om/build ankha/inspector data)]))

(defn last-slide [data]
  (assoc data
         :counter (:last-count data)
         :last-slide true))

(defn slider [data]
  (sab/html
   [:div.slides
    (if (:last-count data)
     (get-slide (last-slide data))
     [:span]) 
    (get-slide data)]))

#_(swap! app-state update-in [:animate] not)

(defonce app
  (om/root
   (fn [data owner]
     (reify om/IRender
       (render [_]
         (sab/html
          [:div.bg-color {:onKeyPress key-handler }
           (slider data)
           #_(inspect-data data)]))))
   app-state
   {:target (. js/document (getElementById "app"))}))

