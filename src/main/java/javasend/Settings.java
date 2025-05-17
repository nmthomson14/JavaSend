package javasend;

import java.io.File;
import java.io.Serializable;

public class Settings implements Serializable {

    // Constants
    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_PORT = 8080;
    private static final String DEFAULT_DOWNLOAD_PATH = System.getProperty("user.home") + File.separator + "Downloads";

    // Objects
    private int port;
    private String downloadPath;


    // Create javasend.Settings object with default settings
    public Settings() {
        port = DEFAULT_PORT;
        downloadPath = DEFAULT_DOWNLOAD_PATH;
    }

    // Getters
    public int getPort() {
        return port;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    // Setters
    public void setPort(int port) {
        this.port = port;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }
}


