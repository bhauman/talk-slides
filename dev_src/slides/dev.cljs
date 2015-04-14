(ns slides.dev
    (:require
     [slides.core]
     [figwheel.client :as fw]))


(fw/start {
           :websocket-url "ws://localhost:3449/figwheel-ws"
           ;; :autoload false
           :build-id "dev"
           :on-jsload (fn []
                        (swap! slides.core/app-state
                               update-in
                               [:__fig-counter] inc))})
