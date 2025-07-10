package javasend;

import java.io.*;
import java.nio.file.Path;

public class SettingsHandler {

    // Constants
    private static final String SETTINGS_FILE_NAME = "settings.ser";

    // Handlers
    private final FileHandler settingsFileHandler;

    // Objects
    private Settings currentSettings;

    public Path getTempUploadsPath() { return settingsFileHandler.getTempUploadsPath(); }
    public void clearTempUploadFiles() { settingsFileHandler.clearTempFiles(); }

    public SettingsHandler() {
        this.settingsFileHandler = new FileHandler(SETTINGS_FILE_NAME);
    }

    public Settings getSettings() {
        if (currentSettings == null) {
            currentSettings = loadSettings();
        }

        return currentSettings;
    }

    private Settings loadSettings() {
        File settingsFile = settingsFileHandler.getFile();

        if (!settingsFile.exists()) {
            SystemMessageHandler.sendMessages("Settings file not found. Creating default settings.");
            return createDefaultSettings();
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(settingsFile))) {
            Settings settings = (Settings) in.readObject();
            SystemMessageHandler.sendMessages("Successfully loaded settings from: " + settingsFile.getAbsolutePath());
            return settings;

        } catch (IOException | ClassNotFoundException e) {
            SystemMessageHandler.sendMessages("Error loading settings: " + e.getMessage());
            return createDefaultSettings();
        }
    }

    public void updateSettings(int port, String downloadPath, String ipAddress) {
        currentSettings.setPort(port);
        currentSettings.setDownloadPath(downloadPath);
        currentSettings.setIpAddress(ipAddress);
        saveSettings();
    }

    public void resetSettings() {
        currentSettings = createDefaultSettings();
        deleteSettings();
    }

    private void saveSettings() {
        settingsFileHandler.overwriteFile(currentSettings);
    }

    private void deleteSettings() {
        settingsFileHandler.deleteFile();
    }

    private Settings createDefaultSettings() {
        return new Settings();
    }

}
