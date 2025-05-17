package javasend;

public class SystemMessageHandler {

    // Singleton
    public static SystemMessageHandler instance;

    // Object Lists
    private MessageReceiver[] receivers;

    // Do not allow other classes to make the instance
    private SystemMessageHandler(MessageReceiver[] receivers) { this.receivers = receivers; }

    // Ensure only one instance is enabled at a time
    public static void initializeInstance(MessageReceiver[] receivers) {

        if (instance != null) {
            return;
        }

        instance = new SystemMessageHandler(receivers);
    }

    public void destroy() {
        instance = null;
        this.receivers = null;
    }

    public static void sendMessages(String message) {

        for (MessageReceiver receiver : instance.receivers) {
            receiver.receiveMessage(message);
        }
    }
}

