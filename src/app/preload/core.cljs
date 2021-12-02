(ns app.preload.core
  (:require ["electron" :refer [contextBridge ipcRenderer]]))

(.exposeInMainWorld contextBridge
                    "api"
                    (clj->js {}))

;; (def exportInMainWorld
;;   )

;; (.exposeInMainWorld contextBridge
;;                       "api"
;;                       {}
;;                       ;; (clj->js {:send (fn [channel data]
;;                       ;;                   (.send ipcRenderer channel data))})
;;                       )

(defn main
  []
  )
