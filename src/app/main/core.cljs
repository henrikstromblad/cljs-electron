(ns app.main.core
  (:require ["electron" :refer [app BrowserWindow dialog ipcMain]]
            [cljs.nodejs :as nodejs]))

(def path (nodejs/require "path"))
(def url (nodejs/require "url"))

(def mainWindow (atom nil))

(defn create-browser-window
  []
  (BrowserWindow.
   (clj->js {:title          "CLJS electron"
             :width          1280
             :height         800
             :webPreferences {:nodeIntegration            false
                              :nodeIntegrationInWorker    true
                              :nodeIntegrationInSubFrames false
                              :contextIsolation           true
                              :enableRemoteModule         false
                              :disableBlinkFeatures       "AuxClick"
                              :preload                    (.join path (js* "__dirname") "preload_js.js")
                              ;;:preload                    (.join path (js* "__dirname") "preload.js")
                              }
             :show           false
             :modal          false
             :icon           (.join path (js* "__dirname") "assets" "clojure.png")})))

(defn initialize-main-window
  []
  (println "Inside main function")
  (let [window     (reset! mainWindow (create-browser-window))
        index-path (clj->js {:pathname (.join path
                                              (js* "__dirname")
                                              "index.html")
                             :protocol "file:"
                             :slashes  true})]
    (println "Index path " index-path)
    (.loadURL window
              (.format url index-path))
    (.on window "closed" #(reset! mainWindow nil))
    (.webContents.on window "did-finish-load" #(.show window))))

(defn show-dialog
  [data]
  (println "Inside show dialog: " data)
  (let [message (js->clj data :keywordize-keys true)        
        options (clj->js {:type      "info"
                          :buttons   ["Ok"]
                          :defaultId 0
                          :icon      (.join path (js* "__dirname") "assets" "clojure.png")
                          :title     (or (str (:title message)) "No title")
                          :message   (or (str (:message message)) "No message")})]
    (.showMessageBox dialog @mainWindow options #())))

(defn main
  []
  (println "Inside main function")

  (.on app "ready" #(initialize-main-window))
  
  (.on ipcMain "showDialog" #(show-dialog %2)))

