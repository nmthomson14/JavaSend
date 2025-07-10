package javasend;

import java.awt.*;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Path;

public class FileHandler {

    // Objects
    private final File file;
    private final String fileName;

    // Constructor
    public FileHandler(String fileName) {
        File directory = defineDirectory();
        this.fileName = fileName;
        this.file = new File(directory, fileName);
    }

    // Getters
    public File getFile() { return file; }
    public Path getTempUploadsPath() {
        File tempUploads = new File(defineDirectory(), "TempUploads");

        if (!tempUploads.exists()) {
            tempUploads.mkdirs();  // create folder if it doesn't exist
        }

        return tempUploads.toPath();
    }

    private File defineDirectory() {
        try {
            return new File(SettingsHandler.class
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI())
                    .getParentFile();
        } catch (URISyntaxException e) {

            SystemMessageHandler.sendMessages("Failed to locate JAR directory, using current directory.");
            return new File(".").getAbsoluteFile();
        }
    }

    public void overwriteFile(Object object) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(object);
            SystemMessageHandler.sendMessages(fileName + " saved successfully.");
        } catch (IOException e) {
            SystemMessageHandler.sendMessages("Error saving " + fileName + ": " + e.getMessage());
        }
    }

    public void openFile() {
        try {

            if (!file.exists()) {
                SystemMessageHandler.sendMessages("Attempted to open " + fileName + ", but it does not exist.");
                return;
            }

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            } else {
                SystemMessageHandler.sendMessages("Desktop API is not supported on this system, cannot open " + fileName + ".");
            }

        } catch (IOException e) {
            SystemMessageHandler.sendMessages("Failed to open " + fileName + ": " + e.getMessage());
        }
    }

    public void appendText(String logMessage) {
        try (FileWriter fw = new FileWriter(file, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(logMessage);
        } catch (IOException e) {
            SystemMessageHandler.sendMessages("Failed to append " + fileName + ": " + e.getMessage());
        }
    }

    public void deleteFile() {
        if (file.exists()) {

            if (file.delete()) {
                SystemMessageHandler.sendMessages(fileName + " reset successfully.");
            } else {
                SystemMessageHandler.sendMessages(("Failed to reset " + fileName + "."));
            }

        } else {
            SystemMessageHandler.sendMessages(fileName + " was not found and could not be reset.");
        }
    }

    public void clearTempFiles() {
        Path tempUploadsPath = getTempUploadsPath();
        File tempFolder = tempUploadsPath.toFile();

        if (tempFolder.exists() && tempFolder.isDirectory()) {
            File[] files = tempFolder.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isFile()) {
                        try {
                            if (!f.delete()) {
                                SystemMessageHandler.sendMessages("Failed to delete temp file: " + f.getName());
                            }
                        } catch (SecurityException se) {
                            SystemMessageHandler.sendMessages("Security exception deleting temp file: " + f.getName());
                        }
                    }
                }
                SystemMessageHandler.sendMessages("TempUploads folder cleared on shutdown.");
            }
        }
    }
}
