package javasend;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LogHandler implements MessageReceiver {

    // Constants
    private static final String LOG_FILE_NAME = "log.txt";

    // Handlers
    private final FileHandler logFileHandler;

    // Objects
    private final SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public LogHandler() {

        this.logFileHandler = new FileHandler(LOG_FILE_NAME);
    }

    // Method from javasend.MessageReceiver interface
    @Override
    public void receiveMessage(String message) {

        String timestamp = timestampFormat.format(new Date());
        String timeStampedMessaged = "[" + timestamp + "] " + message;

        logFileHandler.appendText(timeStampedMessaged);
    }

    public void openLogFile() {

        logFileHandler.openFile();
    }

    public void deleteLogFile() {

        logFileHandler.deleteFile();
    }
}
