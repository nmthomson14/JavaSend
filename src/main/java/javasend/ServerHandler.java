package javasend;

import io.javalin.Javalin;
import io.javalin.http.UploadedFile;
import io.javalin.http.staticfiles.Location;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

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
    //public String getIpAddress() throws UnknownHostException { return InetAddress.getLocalHost().getHostAddress(); }

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

    public boolean InitializeServer(String portInput, String ipAddress) {
        int port = verifyPort(portInput);
        if (port == -1) return false;

        try {
            createURLAddress(port, ipAddress);
            startServer(port, ipAddress);
            SystemMessageHandler.sendMessages("Server started on port " + port);
            return true;
        } catch (Exception e) {
            SystemMessageHandler.sendMessages("Error starting server: " + e.getMessage());
            return false;
        }
    }

    private void createURLAddress(int port, String ipAddress) {
        hostAddress = "http://" + ipAddress + ":" + port + "/";
    }

    private void startServer(int port, String ipAddress) {
        app = Javalin.create(config -> {
            config.staticFiles.add(staticFiles -> {
                staticFiles.directory = "web"; // Folder where your HTML lives
                staticFiles.hostedPath = "/";
                staticFiles.precompress = false;
                staticFiles.location = Location.EXTERNAL;
            });
        }).start(ipAddress, port);

        app.get("/", ctx -> ctx.redirect("/index.html"));

        // Upload handler
        app.post("/upload", ctx -> {
            List<UploadedFile> files = ctx.uploadedFiles("file");

            if (files.isEmpty()) {
                ctx.status(400).result("No files uploaded.");
                return;
            }

            String downloadPath = settingsHandler.getSettings().getDownloadPath();
            File dir = new File(downloadPath);

            if (!dir.exists() && !dir.mkdirs()) {
                SystemMessageHandler.sendMessages("Failed to create upload directory at: " + dir.getAbsolutePath());
                ctx.status(500).result("Failed to create upload directory.");
                return;
            }

            for (UploadedFile file : files) {
                File dest = new File(dir, file.filename());

                try (InputStream in = file.content(); OutputStream out = new FileOutputStream(dest)) {
                    in.transferTo(out);
                    SystemMessageHandler.sendMessages("Saved: " + dest.getAbsolutePath());
                } catch (IOException e) {
                    SystemMessageHandler.sendMessages("Failed to save " + file.filename() + ": " + e.getMessage());
                }
            }

            ctx.redirect("/upload_success.html");
        });

        // NEW: JSON API for listing uploaded files
        app.get("/api/uploads", ctx -> {
            String uploadPath = settingsHandler.getTempUploadsPath().toString();
            File folder = new File(uploadPath);

            if (!folder.exists() || !folder.isDirectory()) {
                ctx.json(new String[0]);
                return;
            }

            String[] files = folder.list((dir, name) -> new File(dir, name).isFile());
            ctx.json(files != null ? files : new String[0]);
        });

        // NEW: Endpoint to serve file downloads
        app.get("/uploads/{filename}", ctx -> {
            String filename = URLDecoder.decode(ctx.pathParam("filename"), StandardCharsets.UTF_8);
            File file = new File(settingsHandler.getTempUploadsPath().toFile(), filename);

            if (!file.exists() || !file.isFile()) {
                ctx.status(404).result("File not found");
                return;
            }

            String mimeType = Files.probeContentType(file.toPath());
            if (mimeType == null) mimeType = "application/octet-stream";

            ctx.contentType(mimeType)
                    .result(new FileInputStream(file))
                    .header("Content-Disposition", "inline; filename=\"" + filename + "\"");
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


