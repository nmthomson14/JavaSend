# JavaSend

**JavaSend** is a lightweight file sharing tool built in Java. It allows fast local file transfers between your PC and mobile devices over a Wi-Fi network, using a simple HTTP server and a QR code interface.

## 🚀 Features

- ⚡ Fast file transfers over local network (no internet required).
- 📱 QR code access for easy phone-to-PC transfers.
- 🎯 No installation needed — just run the executable.
- 🧩 Simple UI with logging and serializable settings (including custom port number and download folder).

---

## 📦 Download

1. Download the latest version from the [Releases](https://github.com/nmthomson14/JavaSend/releases) page.
2. Unzip the file.
3. (Optional) Create a folder called Apps in your desktop and place the unzipped file in it for easy access.
4. (Optional) Right click on the exe file and select Show More Options > Send To > Desktop to create a shortcut. 

---

## 🛠️ How It Works

1. Launch the app (`JavaSend.exe` or `JavaSend.jar`)
2. (Optional) Adjust Settings by clicking the Settings button - you can change the port number and download path (defaults to download folder).
3. Click Start Server to run the server and generate a QR Code representation of your IP Address / port number.  
4. Scan the QR code with your phone / tablet to open the web interface.
5. This brings up a web page where you can select a file and click Upload.

This app is designed to be completely self contained. The log and settings files will save inside the main folder allowing for easy deletion with no stray files in AppData. This also means that any folder requiring admin priveledges for read/write (such as Program Files) is not the best place to keep this app. Instead, make a folder called "Apps" on your desktop and place the unzipped application in there.


