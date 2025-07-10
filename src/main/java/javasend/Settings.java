package javasend;

import java.io.File;
import java.io.Serializable;
import java.net.Inet4Address;
import java.net.UnknownHostException;

public class Settings implements Serializable {

    // Constants
    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_PORT = 8080;
    private static final String DEFAULT_DOWNLOAD_PATH = System.getProperty("user.home") + File.separator + "Downloads";
    private static final String DEFAULT_IP_ADDRESS;

    static {
        String defaultIpAddress;
        try {
            defaultIpAddress = Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            defaultIpAddress = "0.0.0.0";
            SystemMessageHandler.sendMessages("Failed to get local IP address, using default: " + defaultIpAddress + ". Consider setting IP address manually.");
        }
        DEFAULT_IP_ADDRESS = defaultIpAddress;
    }

    // Objects
    private int port;
    private String downloadPath;
    private String ipAddress;

    // Create javasend.Settings object with default settings
    public Settings() {
        port = DEFAULT_PORT;
        downloadPath = DEFAULT_DOWNLOAD_PATH;
        ipAddress = DEFAULT_IP_ADDRESS;
    }

    // Getters
    public int getPort() { return port; }
    public String getDownloadPath() { return downloadPath;}
    public String getIpAddress() { return ipAddress; }

    // Setters
    public void setPort(int port) { this.port = port; }
    public void setDownloadPath(String downloadPath) { this.downloadPath = downloadPath; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
}


