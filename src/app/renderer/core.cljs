(ns app.renderer.core
  (:require [reagent.dom :as r]
            [re-frame.core :as rf]
            [app.renderer.events]))

(def debug? ^boolean goog.DEBUG)

(defn dev-setup
  []
  (when debug?
    (println "Development mode")))

(defn send-message
  [item]
  (println (str "About to send " item))
  (.api.send js/window (:channel item) (clj->js {:title   (:name item)
                                                 :message (:message item)
                                                 :detail  (:detail item)})))

(defn get-value
  [element]
  (-> element .-target .-value))

(defn icons
  [icon]
  [:div.text-gray-800
   (condp = icon
     :library
     [:svg {:class "w-6 h-6", :fill "none", :stroke "currentColor", :viewbox "0 0 24 24", :xmlns "http://www.w3.org/2000/svg"}
      [:path {:stroke-linecap "round", :stroke-linejoin "round", :stroke-width "2", :d "M8 14v3m4-3v3m4-3v3M3 21h18M3 10h18M3 7l9-4 9 4M4 10h16v11H4V10z"}]]

     :home
     [:svg {:class "w-6 h-6", :fill "none", :stroke "currentColor", :viewbox "0 0 24 24", :xmlns "http://www.w3.org/2000/svg"}
      [:path {:stroke-linecap "round", :stroke-linejoin "round", :stroke-width "2", :d "M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"}]]

     [:svg {:class "w-6 h-6", :fill "none", :stroke "currentColor", :viewbox "0 0 24 24", :xmlns "http://www.w3.org/2000/svg"}
      [:path {:stroke-linecap "round", :stroke-linejoin "round", :stroke-width "2", :d "M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"}]])]
  )

(defn add-row
  [item]
  [:tr
   [:td.p-2.whitespace-nowrap
    [:div.flex.items-center
     [:div.w-10.h-10.flex-shrink-0.mr-2.sm:mr-3
      [icons (:icon item)]]
     [:div {:class "font-medium text-gray-800"} (:name item)]]]
   [:td {:class "p-2 whitespace-nowrap"}
    [:div {:class "text-left"}
     [:input.py-1.px-2 {:type        "text"
                        :placeholder "Enter channel"
                        :value       (:channel item)
                        :onChange    #(do
                                        (println (get-value %1))
                                        (rf/dispatch [:update-channel-for-item (:id item) (get-value %1)]))}]]]
   [:td {:class "p-2 whitespace-nowrap"}
    [:div {:class "text-left font-medium text-green-500"}
     [:input.py-1.px-2 {:type        "text"
                        :placeholder "Enter message"
                        :value       (:message item)
                        :onChange    #(do
                                        (println (get-value %1))
                                        (rf/dispatch [:update-message-for-item (:id item) (get-value %1)]))}]]]
   [:td {:class "p-2 whitespace-nowrap"}
    [:div {:class "text-lg text-center"}
     [:button.bg-blue-500.hover:bg-blue-700.text-white.font-bold.py-1.px-4.rounded.focus:outline-none.focus:shadow-outline  {:type    "button"
                                                                                                                             :onClick #(send-message item)} "Send"]]]])

(defn main-component
  []
  (let [items @(rf/subscribe [:items])]
    [:section {:class "antialiased bg-gray-100 text-gray-600 h-screen px-4"}
     [:button.bg-blue-500.hover:bg-blue-700.text-white.font-bold.py-1.px-4.rounded.focus:outline-none.focus:shadow-outline  {:type "button"}
      [:a {:target "_blank"
           :href "https://www.github.com"} "GitHub link"]]
     [:button.bg-blue-500.hover:bg-blue-700.text-white.font-bold.py-1.px-4.rounded.focus:outline-none.focus:shadow-outline  {:type "button"}
      [:a {:target "_blank"
           :href "https://www.gitlab.com"} "GitLab link"]]
     [:div {:class "flex flex-col justify-center h-full"}
      [:div {:class "w-full max-w-2xl mx-auto bg-white shadow-lg rounded-sm border border-gray-200"}
       [:header {:class "px-5 py-4 border-b border-gray-100"}
        [:h2 {:class "font-semibold text-gray-800"} "Electron IPC message test"]]
       [:div {:class "p-3"}
        [:div {:class "overflow-x-auto"}
         [:table {:class "table-auto w-full"}
          [:thead {:class "text-xs font-semibold uppercase text-gray-400 bg-gray-50"}
       [:tr
        [:th {:class "p-2 whitespace-nowrap"}
         [:div {:class "font-semibold text-left"} "Name"]]
        [:th {:class "p-2 whitespace-nowrap"}
         [:div {:class "font-semibold text-left"} "Channel"]]
        [:th {:class "p-2 whitespace-nowrap"}
         [:div {:class "font-semibold text-left"} "Message"]]
        [:th {:class "p-2 whitespace-nowrap"}
         [:div {:class "font-semibold text-center"} "Send"]]]]
          [:tbody {:class "text-sm divide-y divide-gray-100"}

           (for [item items]
             ^{:key (str "Row" (:id item))}
             [add-row item])]]]]]]]))

(defn render
  []
  (println "Inside render")
  (r/render [main-component]
    (.getElementById js/document "app")))

(defn ^:dev/after-load clear-cache-and-render!
  []
  (println "Inside after load")
  (rf/clear-subscription-cache!)
  (render))

(defn ^:export main
  []
  (println "Inside main")
  (rf/dispatch-sync [:initialize])
  (dev-setup)
  (render))
