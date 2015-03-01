(ns slides.todos
  (:require
   [om.core :as om :include-macros true]
   [om.dom :as dom :include-macros true]
   [sablono.core :as sab :include-macros true]))

(defn prevent [f]
  (fn [e]
    (.preventDefault e)
    (f e)))

(defn prevent->value [f]
  (prevent (fn [e]
             (f (.-value (.-target e))))))

(defn add-todo [form-todo todos]
  (vec
   (conj todos
         (assoc form-todo
                :id (name (gensym "temp-"))
                :created-at (js/Date.)))))

(defn update-todo [id data todos]
  (mapv
   (fn [td]
     (if (= (:id td) id)
       (merge td data)
       td))
   todos))

(defn delete-todo [id todos]
  (vec (filter #(not= (:id %) id) todos)))

(defn update-todo! [todos id data]
  (om/transact! todos []
                (partial update-todo id data)
                :update-todo))

(defn delete-todo! [todos id]
  (om/transact! todos [] (partial delete-todo id) :delete-todo))

(defn todo [{:keys [todos todo] :as huh} owner]
  (let [{:keys [id content completed]} todo]
    (om/component
     (sab/html
      [:li 
       [:div 
        (if completed
          [:a.orange {:href "#"
               :style { :marginRight "10px" }
               :onClick (prevent #(delete-todo! todos id))}
           "delete"]
          [:a.orange {:href "#"
               :style { :marginRight "10px"}
               :onClick (prevent
                         #(update-todo! todos id {:completed true}))}
           "done"])
        [:span {:style (if completed
                         {:text-decoration "line-through"}
                         {})}
         content]]]))))

(defn todo-list [{:keys [subset-todos todos]} owner]
  (om/component
   (sab/html [:ul {:style {:list-style "none"
                           :padding "0px"}}
              (doall
               (map
                (fn [t] (om/build todo
                                 { :todos todos
                                   :todo  t}) )
                subset-todos))])))

(defn todo-form [data owner]
  (om/component
   (let [todos     (:todos data)
         form-todo (:form-todo data)]
     (sab/html
      [:form {:onSubmit
              (prevent
               #(do
                  (om/transact!
                   todos []
                   (partial add-todo form-todo)
                   :create-todo)
                  (om/update! form-todo {})))}
       [:input {:type "text"
                :placeholder "Todo"
                :value (:content form-todo)
                :onChange (prevent->value
                           #(om/update! form-todo :content %))}]]))))

(defn todos-component [data owner]
  (om/component
   (let [todos-parts     (group-by :completed (:todos data))
         todos           (get todos-parts nil)
         completed-todos (get todos-parts true)]
     (sab/html [:div
                [:h1 "Don't dos"]
                (om/build todo-form data)
                (om/build todo-list {:subset-todos todos
                                     :todos (:todos data)})
                (when (not-empty completed-todos)
                  (sab/html
                   [:div [:h4 "Completed"]
                    (om/build todo-list {:subset-todos completed-todos
                                         :todos (:todos data)})]))]))))
