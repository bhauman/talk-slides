(ns slides.core
    (:require
     [om.core :as om :include-macros true]
     [om.dom :as dom :include-macros true]
     [ankha.core :as ankha]
     [sablono.core :as sab :include-macros true]
     [slides.present :refer [on only section]]
     [figwheel.client :as fw]
     [cljs.core.async :as async :refer [<! timeout]])
    (:require-macros
     [slides.present :refer [defslide]]
     [cljs.core.async.macros :refer [go]]))

(enable-console-print!)

(declare get-slide dispatch! set-slide)

(defmulti slide (fn [a] a))

(defmethod slide :default [s]
  (sab/html [:div "empty"]))

(defn highlight [data owner]
  (reify
    om/IDidMount
    (did-mount [this]
      (let [dom-node (om/get-node owner "code-ref")]
        (.highlightBlock js/hljs dom-node)))
    om/IDidUpdate
    (did-update [this _ _]
      (let [dom-node (om/get-node owner "code-ref")]
        (.highlightBlock js/hljs dom-node)))
    om/IRenderState
    (render-state [this state]
      (sab/html
       [:pre.left
        [:code {:className "clojure"
                :ref "code-ref"}
         data]]))))

(defn highlighter [code]
  (om/build highlight code))

;; define your app data so that it doesn't get over-written on reload

(def slide-list [:intro
                 :context
                 :figwheel-birth
                 :state-of-the-art
                 :staccato
                 :what-experience
                 :experiment
                 :bad-idea?
                 :the-trade-off
                 :reloadable-code
                 :shiftin-responsibility
                 :how-hard-write
                 :clojure-script-reloadable
                 :javascript-not-so-much
                 :enter-reactjs
                 :clojurescript-heart-reactjs
                 #_:reloadable-javascript
                 :dome
                 :bret-victor-1
                 :bret-victor-2
                 :this-awesome
                 :timely-feedback
                 #_:qualities
                 :questions])

#_(set-slide :intro)

(defonce app-state
  (atom { :counter 0
          :ins-counter 0
          :animate true}))

;; slide definitions

(def cubes [{:x 0
             :y 0
             :z 0}
            {:x 1
             :y 0
             :z 0}
            {:x 2
             :y 0
             :z 0}            
            ])

(defslide intro [state]
  [:div.center.top-15
   [:h1 "Developing ClojureScript with "
    [:span.orange "Figwheel"]]
   [:div.green "Bruce Hauman"]
   [:br]
   [:div.blue "@bhauman"]
   [:div.blue "rigsomelight.com"]
   [:div.blue "github.com/bhauman"]])

;; automated constant

(defslide context [state]
  [:div.center.top-15
   [:h2.green "Context"]
   [:div.line "ClojureScript"]
   [:div.line "SPAs"]])

(defslide figwheel-birth [state]
  [:div.center.top-15
   [:h2.green "Birth of Figwheel"]
   [:div "dissatisfied with the current feedback loop " [:br]
    "of SPA development"]])

(defslide state-of-the-art [state]
  [:div.left.left-10.top-5
   [:h2.blue "Current state of the art"]
   (on 1 state [:div.line "Write code"])
   (on 2 state [:div.line "Reload browser"])
   (on 3 state [:div.line "Lose application state"])
   (on 4 state [:div.line "Manipulate application back into state needed"])
   (on 5 state [:div.line "Test/verify behavior"])
   (on 6 state [:div.line "Rinse and repeat, until you lose your mind"])])

(defslide staccato [state]
  [:div.center.top-20
   [:blockquote.blue "staccato " [:span.orange "antithesis" ] " of " [:span.green "FLOW"]]])

(defslide experiment [state]
  [:div.center.top-15
   [:div.blue "Experiment"]
   [:blockquote.orange "simple live file reloads"]
   [:div.line
    "why not reload the code into the"]
   [:div.line
    "running application on save?"]])

(defslide bad-idea? [state]
  [:div.left.left-10
   [:h2 "Bad Idea? ... I don't know. Try it."]
   (on 1 state
       [:div.line.orange "Worried about"])
   (on 1 state
       [:div.line "High potential for instability"])
   (on 2 state
       [:div.line "burden of paying attention to load time side-effects"])
   (on 3 state
       [:div.line.green "Experience" ])   
   (on 3 state
       [:div.line "instantaneous feedback almost magical"])
   (on 4 state
       [:div.line "darn good ROI for writing reloadable code"])
   (on 5 state
       [:div.line.blue "Ultimately" ])
   (on 5 state
       [:div.line "\"Are you serious? we should be doing this all the time!\""])])

(defslide the-trade-off [state]
  [:div.center.top-10
   [:div.blue "The trade-off"]
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
   (on 1 state
       [:div.line
        [:span.green "behavior "] "of application has "
        [:span.green "changed"]])
   (on 2 state
       [:div.line "application "
        [:span.orange "state"]
        " is "
        [:span.orange "unchanged"]])])

(defslide shiftin-responsibility [state]
  [:div.center.top-15
   [:h2 [:span.blue "It's all about the reloadable code"]]
   [:div.line "If you want automated continuous hot code reloading"]
   [:div.line "in an imperative context"]
   [:div.line.orange "write reloadable code!"]])

(defslide how-hard-write [state]
  [:div.center.top-20
   [:blockquote.orange "How hard is it to write reloadable code?"]])

(defslide clojure-script-reloadable [state]
  [:div.center.top-15
   [:h2 [:span.blue "ClojureScript and its idioms very reloadable"]]
   [:div.line "reloadability is a core value in Clojure"]])

(defslide javascript-not-so-much [state]
  [:div.left.left-10.top-10
   [:h2 [:span.blue "JavaScript APIs not so much"]]
   (on 1 state
       [:div.line "APIs that rely on mutable state (DOM)"])
   (on 2 state
       [:div (highlighter "(.addEventListener ... )")])
   (on 3 state
       [:div.line "requires strategy to write reloadable code"])
   (on 4 state
       [:div.line "difficult in the large: large projects and teams"])])

(defslide enter-reactjs [state]
  [:div.left.left-10.top-10
   [:h2 [:span.blue "Enter React.js"]]
   (on 1 state [:div.line "Not everything is awesome, but React.js is"])
   (on 2 state [:div.line "proper React.js code is inherently reloadable"])
   (on 3 state [:div.line "solves the problem of interacting with the DOM"])
   (on 4 state [:div.line "a component model that allows interacting with libraries and APIs in a way that is reloadable"])])

(defslide clojurescript-heart-reactjs [state]
  [:div.center.top-10
   [:h2 [:span.blue "ClojureScript" [:span.orange " ❤ "] "React.js"]]
   [:blockquote "The threshold"]
   [:div.line "this combination tremendously simplifies writing reloadable code"]
   [:div.line "the magic of instantaneous hot code loading today!"]
   [:div.line "even in the imperative env of JavaScript!"]])

(defslide reloadable-javascript [state]
  [:div.center.top-10
   [:h2 [:span.blue "live hot code reloading in JS?"]]
   [:div.line "now with React, this can be done"]
   [:div.line "beware of communities high investment in encapsulation patterns"]])

(defslide dome [state]
  [:div.center.top-5 
   [:img {:src "img/dome.jpgg" ;; imgs/dome.jpg
          :width 720
          :height 537}] 
   (only 1 state
         [:div {:style {:fontSize "8em;"
                        :position "absolute"
                        :top "140px"   ;; 190
                        :left "248px"  ;; 288
                        :textShadow "0px 0px 20px #000"}}
          "(" [:span {:style
                      {:display "inline-block"
                       :width "200px"}}] ")"])
   (only 2 state
         [:div
          {:style {:position "absolute"
                   :top "253px"  ;; 213
                   :left "446px" ;; 366
                   :opacity 0.5}}
          [:img  {:src "imgs/Clojure-Logo.png"
                  :width "210px"}]])])

;; stage 1

;; S: I'm now using figwheel reloading

#_(prn @app-state)

#_(dispatch! :advance)

#_(dispatch! :back)

;; what experience do we want? What would be a dreamy experience?

;; what trade-offs are we going to have to make in order to get that
;; experience? What is the cost?

;; My answer to these questions is Figwheel.

;; change to terminal; stop cljsbuild and start figwheel.
;; reload app to get figwheel connected.

;; navigate to where we are.

;; maybe instead of the examples below.

(defslide bret-victor-1 [state]
  ;; live code transformations!!
  ;; instantaneous MACRO and reloading
  #_(prn state)
  [:div.center.top-20
   [:blockquote
    "A mustache addresses human needs by "
    [:span #_.orange "amplifying"]
    " human capabilities."]
   [:div.blue "- Burt Reynolds"]])

;; heads up display
;; green circle

#_(this is cool)

;; doesn't load code with warnings (save a couple of times)

#_(defn)

;; SHOW SECOND Browser

#_(set-slide :bret-victor-2)

;; CSS reloading
;; make zoom in Safari work slides.css
;; font-size in style.css

;; add rotation to animation effect
;; change timing in animation effect

(defslide bret-victor-2 [state]
  [:div.center.top-20
   [:blockquote  "a beard converts what we"
    [:span #_.orange " can do"]
    [:br]
    "into what we"
    [:span #_.green " want to do"]]
   [:div.blue "- Zach Galifianakis"]
   #_[:div.left-20.left
      (om/build ankha/inspector state)]])

;; CLOSE Safari

;; arbitrary coding

#_(defn add [a b] (+ a b))

#_(prn (add 3 4))


(defslide this-awesome [state]
  [:div.center.top-20
   [:blockquote  "Most awesome thing ever??"]
   (on 1 state
       [:div.line "maybe not, but a very useful tool"])
   (on 1 state
       [:div.line "especially for UI work"])])

(defslide timely-feedback [state]
  [:div.center.top-20
   [:h2.orange "Timely feedback is important."]
   (on 1 state
       [:div.line "we should think more about automated continuous reloadability"])
   (on 2 state
       [:div.line "reloadability is a design value"])])

(defslide qualities [state]
  [:div.left.left-10.top-10
   [:h2 "Qualities of hot reloading on file save"]
   (on 1 state
       [:div.line "natural interface (watch the kids)"])
   (on 2 state
       [:div.line "every editor can do it"])
   (on 3 state
       [:div.line "no complex editor specific REPL setup required"])
   (on 4 state
       [:div.line "keeps everything current"])
   (on 5 state
       [:div.line "different than piecemeal REPL reloading"])])

(defslide questions [state]
  [:div.center.top-20
   [:h2.orange "Questions?"]])



(defslide hot-code1 [state]
  [:div.center.top-15
   (on 1 state
       [:h2
        "Automated continuous"])
   [:h2.blue "Hot code reloading"]])

(defslide hot-code [state]
  [:div.center.top-5
   [:h2.blue "Hot code reloading"]
   [:div.line "loading code into a running application"]
   [:div.line "such that"]
   [:div.top-5
    (on 1 state
        [:div.line
         [:span.green "behavior "] "of application has "
         [:span.green "changed"]]
        )
    (on 2 state
        [:div.line "application "
         [:span.orange "state"]
         " is "
         [:span.orange "unchanged"]]
        )
    (on 3 state
        [:div.line
         [:span.blue "avoiding" ]
         " restarts"])]])

(defslide imperative-context [state]
  [:div.center.top-5
   [:h2.orange "Hot code reloading"]
   [:div  	"\u2229"]
   [:h2.blue "imperative context"]
   [:div  	"="]
   [:h2.line "¯\\_(ツ)_/¯"]])

(defslide feedback [state]
  [:div.center.top-15
   [:h2.green "Increasing Feedback"]
   (on 1 state
       [:div
        [:div.line
         "Increase the amount of information"]
        [:div.line
         "we receive while coding"]])])

(defslide fun [state]
  [:div.center.top-15
   [:h2.green "More fun!"]
   (on 1 state
       [:div.line "Failing fast is fun"])
   (on 2 state
       [:div.line.orange "Video games agree" ])
   (on 3 state
       [:div.line "Acheive a state of "
        [:span.blue "FLOW"]])])

#_(defslide dome [state]
  [:div.center.top-5 
   [:img {:src "img/dome.jpgg" ;; imgs/dome.jpg
          :width 720
          :height 537}] 
   (only 1 state
         [:div {:style {:fontSize "8em;"
                        :position "absolute"
                        :top "140px"   ;; 190
                        :left "248px"  ;; 288
                        :textShadow "0px 0px 20px #000"}}
          "(" [:span {:style
                      {:display "inline-block"
                       :width "200px"}}] ")"])
   (only 2 state
         [:div
          {:style {:position "absolute"
                   :top "253px"  ;; 213
                   :left "446px" ;; 366
                   :opacity 0.5}}
          [:img  {:src "imgs/Clojure-Logo.png"
                  :width "210px"}]])])


(defslide awesome [state]
  [:div.center.top-5
   [:img {:src "imgs/rich.jpeg"
          :style { :borderRadius "50%"
                   :boxShadow "0px 0px 10px #000"}
          :width "300"
          :height "300"}]
   [:blockquote "Not everything is awesome."]
   [:div.blue "- Rich Hickey"]])

(defslide not-awesome [state]
  [:div.center.top-20
   [:blockquote "This experience is "
    [:span.orange "NOT"] " awesome."]
   [:div.blue "- wtf"]])

(defslide what-experience [state]
  [:div.center.top-20
   [:blockquote "What experience do we want?"]])



(defslide what-worth [state]
  [:div.center.top-20
   [:blockquote  "What is this worth to you?"]
   (on 1 state
       [:div.line "Would you switch languages?"])
   (on 2 state
       [:div.line "Would you switch frameworks?"])
   (on 3 state
       [:div.line "Would you change your coding patterns?"])])

(defslide impending-trade-off [state]
  [:div.center.top-20
   [:blockquote  "A trade-off is coming"]])

;; how do I accomplish this reloading?

(defslide reloading-two-ways [state]
  [:div.left.left-10.top-5
   [:h1
    "Two ways to do hot reloading:"]
   (on 1 state
       [:div
        [:h3.orange "Complex " (on 2 state
                                   "/ Impossible")]
        [:div.line "Accomodate arbitrary imperative code"]])
   (on 3 state
       [:div
        [:h3.green "Simple"]
         [:div.line "Write reloadable code"]])])

(defslide the-trade-off [state]
  [:div.center.top-10
   [:div.blue "The trade-off"]
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
   (on 1 state
       [:div.line
        [:span.green "behavior "] "of application has "
        [:span.green "changed"]])
   (on 2 state
       [:div.line "application "
        [:span.orange "state"]
        " is "
        [:span.orange "unchanged"]])])

;; moving the responsibility to the developers shoulders
;; and I'm saying that in an imperative context thats the
;; only place it can reside.

(defslide problems-imperative [state]
  [:div.left.left-10.top-5
   [:h3 "Problems of writing reloadable code " [:br]
    " in an imperative environment."]
   (on 1 state
       [:div.line "Networks of mutable objects"])
   (on 2 state
       [:div.line "Module pattern"])
   (on 3 state
       [:div.line "Top level side effects"])
   (on 4 state
       [:div.line "Stateful mutable browser APIs (DOM, etc)"])
   (on 5 state
       [:div.line "Design of dominent frameworks"])])

(defslide cost-benefit [state]
  [:div.center.top-10
   [:h3.line "Potentially very High Cost"]])

(defslide taming-the-beast [state]
  [:div.center.top-10
   [:blockquote "Taming the beast"]
   [:h3.line "with mostly functional programming"]])

(defslide clojurescript [state]
  [:div.center.top-10
   [:blockquote "ClojureScript idioms are reloadable"]
   (on 1 state
       [:div.line "Pure functions"])
   (on 2 state
       [:div.line "Methods that refer to stable mutable state references"])
   (on 3 state
       [:div.line "Methods that take mutable state as a parameter"])
   (on 4 state
       [:div.line "Most idiomatic ClojureScript is reloadable"])])

(defslide api-problem [state]
  [:div.center.top-10
   [:blockquote "Browser APIs are still a problem."]])

(defslide enter-react [state]
  [:div.center.top-10
   [:blockquote "React"]
   (on 1 state
       "React is a reloadbale interface to the DOM.")])

(defslide react [state]
  [:div.center.top-10
   [:h3 "A threshold has been crossed."]
   [:div.line "ClojureScript and React"]
   [:div.line  "together"]
   [:div.line " have made writing reloadable code"]
   [:div.line " significantly easier."]])

(defslide sample-program [state]
  [:div
   (highlighter
    "(ns example.core
  (:require 
    [sablono.core :as sab :include-macros true]
    [om.core :as om :include-macros true]))

(defonce app-state (atom {:count 0}))

(defn counter [state]
  (sab/html 
    [:div 
      [:h1 \"Count \" (:count state)]
      [:a {:onClick #(transact! state [:count] inc)} \"inc\"]]))

(om/root 
  (fn [data owner]
     (reify om/IRender
       (render [_] (counter data))))
  app-state
  {:target (. js/document (getElementById \"app\"))})")])

(defslide better-code [state]
  [:div.center.top-10
   [:blockquote "Reloadable code is often better code"]
   (on 1 state
       [:div.live "movement towards the declarative"])
   (on 2 state
       [:div.line "minimization of unreloadable code sections"])])

(defslide summary [state]
  [:div.center.top-10
   [:h3 "You can have automated continuous hot code reloading."]
   [:div.line "you have to write reloadable code"]
   [:div.line  "ClojureScript and React have made the cost of writing reloadable code negligable."]])


(defslide the-how [state]
  [:div.center.top-10
   [:blockquote "Figwheel how?"]])

(defslide wont-work [state]
  [:div.center.top-10
   [:blockquote "Not reloadable:"]
   (highlighter
    "(ns example.core)

...

(defn keyhandler [e] ...)

(.addEventlistener js/document keyhandler)")])

(defslide expected-side-effects [state]
  [:div.center.top-10
   [:blockquote "Expected side effects"]
   (highlighter
    "(ns example.core)

...

(defn keyhandler [e] ...)

(defonce listener 
  (do (.addEventlistener js/document keyhandler)
      true)) ")])

(defslide incorporated-behavior [state]
  [:div.center.top-10
   [:blockquote "Incorporate new behavior"]
   (highlighter
    "(ns example.core)

...

(defn keyhandler [e] ...)

(defonce listener 
  (do (.addEventlistener js/document #(keyhandler %))
      true))")])

(defslide even-better [state]
  [:div.center.top-10
   [:blockquote "Even better"]
   (highlighter
    "(ns example.core)

...

(defn keyhandler [e] ...)

(defn ^:export main [] 
  (.addEventlistener js/document #(keyhandler %)))")])

(defslide traditional [state]
  [:div.center.top-10
   [:blockquote "\"Traditional\" JavaScript patterns and APIS"]
   [:div "make it fairly complex to write reloadable code"]])

(defslide enter-clojurescript-react [state]
  [:div.center.top-10
   [:blockquote "Enter ClojureScript and React"]
   ;; images for clojure and react
   ])

(defslide bonuses [state]
  [:div.top-5.left-20.left
   [:h2 "ClojureScript"]
   (on 1 state
       [:div "functional programming"])
   (on 2 state
       [:div "immutable data"])
   (on 3 state
       [:div "minimization of mutable objects"])
   
   (on 4 state
       [:h2 "React"])
   (on 5 state
       [:div
        "declarative interface to mutable client apis"])])

(defslide threshold [state]
  [:div.top-5.center
   [:blockquote
    "We have crossed a threshold."]
   [:div
    "Where writing reloadable code has gotten much simpler."]])

(defn get-slide [data]
  (slide (get slide-list
              (mod (:counter data) (count slide-list)))
         data))

(defn anim-transition []
  (go
    (swap! app-state assoc :trans-z -10000)
    (<! (timeout 1000))
    (swap! app-state assoc :trans-z 0
                           ;;:rot-x   (+ 360 (:rot-x @app-state))
           )))

#_(prn @app-state)
;; this can be a react component

(defn set-slide [id]
    (swap! app-state
           assoc
           :ins-counter 0
           :counter (ffirst (filter #(= id (second %)) (map-indexed vector slide-list))))
    (anim-transition))

;; handle keyboard events
(defmulti dispatch identity)

(defmethod dispatch :advance [_ data]
  (om/transact! data []
                (fn [s]
                  (let [x (:counter s)]
                    (assoc s
                           :ins-counter 0
                           :counter (inc x)))))
  ;; effect
  #_(anim-transition))

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
                           :counter
                           (if (zero? x) 0 (dec x)))))))

(defn dispatch! [action]
  (dispatch action (om/root-cursor app-state)))

(def dispatch-map
  {32 :advance
   39 :advance
   40 :internal-advance
   38 :internal-back   
   37 :back})

(defn key-handler [e]
  (when-let [action (get dispatch-map (.-keyCode e))]
    (dispatch! action)))

(defn inspect-data [data]
  (sab/html
   [:div.ankha {:style {:marginTop "50px"
                        :position "fixed"
                        :bottom "0px"
                        :left "0px"
                        :fontSize "0.4em"}}
    (om/build ankha/inspector data)]))

(defn translation [data]
  (str " translateX(" (* -960 (:counter data)) "px) "
       (when (:trans-z data)
         (str "translateZ(" (:trans-z data) "px) "))
       (when (:rot-x data)
           (str "rotateZ(" (:rot-x data) "deg)"))))

(defn slider [data]
  (sab/html
   [:div.slides
    [:div.slide-world {:style {:-webkit-transform-origin (str (* 960 (:counter data))  "px 300px")
                               :-webkit-transform (translation data)
                               }}
     (map-indexed #(slide %2 (assoc data :spos %1)) slide-list)]]))

#_(swap! app-state update-in [:animate] not)

(defn ^:export main []

  (.addEventListener
   (.-body js/document)
   "keyup"
   (fn [e] (key-handler e)))

  (om/root
   (fn [data owner]
     (reify om/IRender
       (render [_]
         (sab/html
          [:div.bg-color 
           (slider data)
           #_(inspect-data data)]))))
   app-state
   {:target (. js/document (getElementById "app"))}))


(fw/start {
           :websocket-url "ws://localhost:3449/figwheel-ws"
           :autoload false
           :on-jsload (fn []
                        (swap! app-state
                               update-in
                               [:__fig-counter] inc))})
