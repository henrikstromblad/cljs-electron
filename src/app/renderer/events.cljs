(ns app.renderer.events
  (:require [re-frame.core :as rf]))

(def default-db {:items {1 {:id      1
                            :icon    :library
                            :name    "Library"
                            :channel "showDialog"
                            :message "A message"}
                         2 {:id      2
                            :icon    :home
                            :name    "Home"
                            :channel "showDialog2"}
                         3 {:id      3
                            :icon    :nonExisting
                            :name    "No icon"
                            :channel "showDialog3"}}})

(rf/reg-event-db
 :initialize
 (fn []
   default-db))

(rf/reg-sub
 :channel
 (fn [db _]
   (-> db :channel)))

(rf/reg-sub
 :items
 (fn [db _]
   (-> db :items vals)))

(rf/reg-event-db
 :update-channel-for-item
 (fn [db [_ id channel]]
   (println "Channel updated " id " " channel)
   (assoc-in db [:items id :channel] channel)))

(rf/reg-event-db
 :update-message-for-item
 (fn [db [_ id channel]]
   (println "Channel updated " id " " channel)
   (assoc-in db [:items id :message] channel)))

(rf/reg-event-db
 :save-message
 (fn [db [_ message]]
   (println "Message saved")
   (assoc db :message message)))
