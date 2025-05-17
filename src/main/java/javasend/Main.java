package javasend;

public class Main {

    public static void main(String[] args) {

        // Initialize class handlers
        LogHandler logHandler = new LogHandler();
        SettingsHandler settingsHandler = new SettingsHandler();
        ServerHandler serverHandler = new ServerHandler(settingsHandler);

        // Initialize UI using handler classes
        UIBuilder ui = new UIBuilder(serverHandler, settingsHandler, logHandler);

        // Initialize all message receivers
        SystemMessageHandler.initializeInstance(
                new MessageReceiver[] { ui, logHandler } );

    }
}
