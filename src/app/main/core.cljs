(ns app.main.core
  (:require ["electron" :refer [app shell BrowserWindow dialog ipcMain Menu]]
            ["process" :as process]
            [cljs.nodejs :as nodejs]))

(def path (nodejs/require "path"))
(def url (nodejs/require "url"))

(def mainWindow (atom nil))

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

(defn create-menu-template
  []
  (let [darwin? (= (.-platform process) "darwin")]
    [(when darwin?
       {:label   (str (.-name app))
        :submenu [{:role "about"}
                  {:label   (str "Version " (.getVersion app))
                   :enabled true}
                  {:type "separator"}
                  {:role "quit"}]})
     {:label   "File"
      :submenu [{:label       "Save"
                 :accelerator "CmdOrCtrl+S"
                 :click       (fn []
                                (show-dialog (clj->js {:title   "Saving"
                                                       :message "Saving"})))}
                {:type "separator"}
                (if darwin?
                  {:role "close"}
                  {:role "quit"})]}
     {:label "View"
      :submenu [{:role "toggledevtools"}]}
     {:role "help"
      :submenu [{:label "Documentation"
                 :click (fn []
                          (.openExternal shell "https://www.github.com"))}
                {:type "separator"}
                {:label "Bad documentation link"
                 :click (fn []
                          (.openExternal shell "https://www.example.com"))}]}]))

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

(defn verify-url
  [event navigationUrl]
  (println "Verifying url before navigation")
  (let [parsedUrl (js/URL. navigationUrl)
        origin (.-origin parsedUrl)]
    (println parsedUrl " " origin)
    (when-not (= "https://www.github.com" origin)
      (println "Prevent browsing")
      (.preventDefault event))))

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

    (.setApplicationMenu Menu
                         (.buildFromTemplate Menu
                                             (clj->js (->> (create-menu-template)
                                                           (remove nil?)
                                                           (mapv identity)))))
    
    (.on window "closed" #(reset! mainWindow nil))
    (.webContents.on window "did-finish-load" #(.show window))))

(defn main
  []
  (println "Inside main function")

  (.on app "window-all-closed" #(when-not (= (.-platform process) "darwin")
                                  (println "Quit application")
                                  (.quit app)))

  (.on app "ready" #(initialize-main-window))

  (.on app "web-contents-created"
       #(.on %2 "will-navigate" verify-url))
  
  (.on ipcMain "showDialog" #(show-dialog %2)))

