const { contextBridge, ipcRenderer } = require('electron')

contextBridge.exposeInMainWorld(
  "api", {
          send: (channel, data) => {
            let validChannels = ["showDialog"];
            if (validChannels.includes(channel)) {
              ipcRenderer.send(channel, data);
            }
          },
          doStuff: (data) => {
            ipcRenderer.send('doStuff', data)
          }
        });