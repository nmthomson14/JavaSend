package javasend;

import io.javalin.Javalin;
import io.javalin.http.UploadedFile;
import io.javalin.http.staticfiles.Location;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ServerHandler {

    // Constants
    public static final int[] PORT_RESTRAINTS = { 1024, 65535 };

    // Handlers
    private final SettingsHandler settingsHandler;

    // Objects
    private String hostAddress;
    private Javalin app;

    // Getters
    public String getHostAddress() { return hostAddress; }

    public ServerHandler(SettingsHandler settingsHandler) {

        this.settingsHandler = settingsHandler;
    }

    public static int verifyPort(String portInput) {

        int port = -1;

        // Invalid inputs will print an error message in the UI console
        try {
            port = Integer.parseInt(portInput);
        } catch (Exception e) {
            SystemMessageHandler.sendMessages("Invalid port number: Please enter an integer between 1024 and 65535.");
            return -1;
        }

        if (port < PORT_RESTRAINTS[0] || port > PORT_RESTRAINTS[1]) {
            SystemMessageHandler.sendMessages("Invalid port number: Please enter an integer between 1024 and 65535.");
            return -1;
        }

        return port;
    }

    public boolean InitializeServer(String portInput) {

        int port = -1;

        // Invalid inputs will print an error message in the UI console
        try {
            port = Integer.parseInt(portInput);
        } catch (Exception e) {
            SystemMessageHandler.sendMessages("Invalid port number: Please enter an integer between 1024 and 65535.");
            return false;
        }

        if (port < PORT_RESTRAINTS[0] || port > PORT_RESTRAINTS[1]) {
            SystemMessageHandler.sendMessages("Invalid port number: Please enter an integer between 1024 and 65535.");
            return false;
        }

        // If everything checks out, create the URL address and open the HTTP server
        createURLAddress(port);
        startServer(port);
        return true;
    }

    private void createURLAddress(int port) {

        try {
            String ip = InetAddress.getLocalHost().getHostAddress(); // use the method from before
            hostAddress = "http://" + ip + ":" + port + "/";
            SystemMessageHandler.sendMessages("Server successfully started on port " + port + "." );
        }
        catch (UnknownHostException e) {
            SystemMessageHandler.sendMessages("Error starting server on port " + port + ". Please try again. \n Error message: " + e.getMessage());
        }
    }

    private void startServer(int port) {

        app = Javalin.create(config -> {
            config.staticFiles.add(staticFiles -> {
                staticFiles.directory = "web"; // Folder where your HTML lives
                staticFiles.hostedPath = "/";
                staticFiles.precompress = false;
                staticFiles.location = Location.EXTERNAL;
            });
        }).start("0.0.0.0", port); // Or use your settings.port

        app.get("/", ctx -> ctx.redirect("/index.html"));

        app.post("/upload", ctx -> {
            UploadedFile file = ctx.uploadedFile("file");

            if (file == null) {
                ctx.status(400).result("No file uploaded.");
                return;
            }

            // Get the user-defined download path from settings
            String downloadPath = settingsHandler.getSettings().getDownloadPath();

            // Ensure the folder exists
            File dir = new File(downloadPath);
            if (!dir.exists())
            {
                if (!dir.mkdirs())
                {
                    SystemMessageHandler.sendMessages("Failed to create upload directory at: " + dir.getAbsolutePath());
                    return;
                }
            }

            // Create destination file
            File dest = new File(dir, file.filename());

            try (InputStream in = file.content(); OutputStream out = new FileOutputStream(dest)) {
                in.transferTo(out);
                SystemMessageHandler.sendMessages("File saved to: " + dest.getAbsolutePath());
            } catch (IOException e) {
                SystemMessageHandler.sendMessages("Failed to save file: " + e.getMessage());
            }

            ctx.redirect("/upload_success.html");
        });
    }

    public void closeServer() {

        if (app != null) {
            app.stop();
            SystemMessageHandler.sendMessages("Server has successfully closed.");
        } else {
            SystemMessageHandler.sendMessages("Server is not running.");
        }
    }
}


