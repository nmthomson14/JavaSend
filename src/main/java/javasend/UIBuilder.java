package javasend;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

public class UIBuilder implements MessageReceiver {

    // Constants
    private static final int UI_HEIGHT = 650;
    private static final int UI_WIDTH = 700;
    private static final String PROGRAM_NAME = "Java Send";

    // Ui components
    private JTextArea systemOutput;
    private CardLayout cardLayout;
    private JPanel stackedPanel;
    private JTextField portInput;
    private JTextField downloadPathField;

    // Handlers
    private final ServerHandler server;
    private final SettingsHandler settingsHandler;
    private final LogHandler logHandler;

    public UIBuilder(ServerHandler server, SettingsHandler settingsHandler, LogHandler logHandler) {

        this.server = server;
        this.settingsHandler = settingsHandler;
        this.logHandler = logHandler;

        initializeUI();
    }

    private void initializeUI() {

        // Prevent concurrency issues by running on the event dispatch thread (handles all GUI events)
        SwingUtilities.invokeLater(this::buildMainMenu);
    }

    private void buildMainMenu() {

        // Define JComponents
        systemOutput = new JTextArea(10, 20);
        cardLayout = new CardLayout();
        stackedPanel = new JPanel(cardLayout);
        JFrame mainFrame = new JFrame(PROGRAM_NAME);
        JPanel topPanel = new JPanel();
        JPanel bottomPanel = new JPanel();
        JScrollPane systemOutputScrollPane = new JScrollPane(systemOutput);
        JLabel logo;

        // Get Logo
        URL logoUrl = getClass().getResource("/logo.png");

        if (logoUrl != null) {

            ImageIcon originalIcon = new ImageIcon(logoUrl);
            Image scaledImage = originalIcon.getImage().getScaledInstance(75, 75, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);

            logo = new JLabel(scaledIcon);

        } else {

            receiveMessage("Could not load logo from resources.");
            logo = new JLabel("Logo not found");
        }

        // Handle the systemOutput messaging box
        systemOutput.setEditable(false);
        systemOutputScrollPane.setPreferredSize(new Dimension(500, 100));
        systemOutputScrollPane.setMaximumSize(new Dimension(500, 100));

        // Handle the top panel
        topPanel.add(Box.createVerticalStrut(25)); // spacer
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        addComponent(logo, topPanel, 10);

        // Handle the bottom panel
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS)); // Respect vertical sizing
        addComponent(systemOutputScrollPane, bottomPanel, 50);

        // Handle main frame
        mainFrame.setSize(UI_WIDTH, UI_HEIGHT);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.add(topPanel, BorderLayout.NORTH);
        mainFrame.add(stackedPanel, BorderLayout.CENTER);
        mainFrame.add(bottomPanel, BorderLayout.SOUTH);

        // Build the tabs
        buildMainTab();
        buildSettingsTab();
        buildLoggerTab();
        buildHelpTab();
        mainFrame.setVisible(true);

        // Print welcome message after initialization
        receiveMessage("Welcome to Java Send! Click help if you're stuck.");
    }

    private void buildMainTab() {

        // JComponents
        JPanel panel = new JPanel();
        JButton startServerButton = new JButton("Start Server");
        JButton settingsButton = new JButton("Settings");
        JButton loggerButton = new JButton("Logs");
        JButton helpButton = new JButton("Help");

        // Handle panel defaults
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(Box.createVerticalStrut(10)); // spacer

        // Handle button listeners
        startServerButton.addActionListener(e -> handleStartServer(String.valueOf(settingsHandler.getSettings().getPort())));
        settingsButton.addActionListener(e -> handleLoadSettingsTab());
        loggerButton.addActionListener(e -> cardLayout.show(stackedPanel, "LOGGER"));
        helpButton.addActionListener(e -> cardLayout.show(stackedPanel, "HELP"));

        // Handle adding components
        addComponent(startServerButton, panel, 10);
        addComponent(settingsButton, panel, 10);
        addComponent(loggerButton, panel, 10);
        addComponent(helpButton, panel, 10);

        // Add this panel to the stacked panes
        stackedPanel.add(panel, "MAIN");
    }

    private void buildSettingsTab() {

        // Define JComponents
        portInput = new JTextField(5);
        downloadPathField = new JTextField(20);
        JPanel panel = new JPanel();
        JLabel portLabel = new JLabel("(Advanced) Enter Port Number: ");
        JLabel downloadPathLabel = new JLabel("Download Files Here:");
        JButton browseButton = new JButton("Change Folder");
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Main Menu");
        JButton resetSettings = new JButton("Reset Settings");

        // Create a new panel and add to stacked panes
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalStrut(10)); // spacer

        // Handle portInput defaults
        portInput.setEditable(true);
        portInput.setMaximumSize(new Dimension(100, 25));

        // Handle downloadPath defaults
        downloadPathField.setEditable(false);
        downloadPathField.setMaximumSize(new Dimension(300, 25));

        // Handle button listeners
        browseButton.addActionListener(e ->
        {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = chooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION)
            {
                downloadPathField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });
        saveButton.addActionListener(e -> handleSettingsSaved());
        cancelButton.addActionListener(e -> cardLayout.show(stackedPanel, "MAIN"));
        resetSettings.addActionListener(e -> handleSettingsReset());

        // Handle adding components
        addComponent(downloadPathLabel, panel, 10);
        addComponent(downloadPathField, panel, 10);
        addComponent(browseButton, panel, 10);
        addComponent(saveButton, panel, 10);
        addComponent(cancelButton, panel, 10);
        addComponent(resetSettings, panel, 10);
        addComponent(portLabel, panel, 10);
        addComponent(portInput, panel, 10);

        // Initialize the UI with current settings
        loadSettingsFields();

        // Add this panel to the stacked panes
        stackedPanel.add(panel, "SETTINGS");
    }

    private void buildHelpTab() {

        // Define JComponents
        JPanel panel = new JPanel();
        JTextArea helpText = new JTextArea(10, 40);
        JScrollPane scrollPane = new JScrollPane(helpText);
        JButton mainMenuButton = new JButton("Main Menu");

        // Handle panel defaults
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(Box.createVerticalStrut(10)); // spacer

        // Handle scroll pane defaults and text
        scrollPane.setMaximumSize(new Dimension(500, 500));
        helpText.setEditable(false);
        helpText.setText("""
                Welcome to Java Send!\s
                This program works by creating a local server from your\s
                computer's IP address. It generates a QR code that let's\s
                you easily load a web input form from most mobile devices.\s
                
                QUICK START GUIDE:\s
                Before starting, you may want to change the download location\s
                where your files will be saved to. To do that, look at HOW TO\s
                ADJUST DOWNLOAD LOCATION below. Now, to get started,\s
                simply press the Start Server button in the main menu, and\s
                scan the QR code that pops up (or type in the URL below it)\s
                with your mobile device. This will take you to a web page\s
                form where you can choose almost any file to upload.\s
                
                LOST A FILE?\s
                If you don't remember where a file was saved to, check the\s
                logs by clicking the Logs button then Open Log File. This\s
                should tell you where any file was saved to.\s
                
                HOW TO ADJUST DOWNLOAD LOCATION:\s
                Click the settings button from the main window, then click\s
                choose folder. Navigate to the folder you would like to save\s
                to and click open. Make sure to save the settings afterwards!\s
                
                HOW TO CHANGE PORT NUMBER (ADVANCED):\s
                Most of the time, the default port 8080 should work fine, but\s
                if you would like to change it, then simply go to settings,\s
                type in the port number next to "Enter Port Number" then click\s
                save.\s
                
                HOW TO RESET SETTINGS TO DEFAULTS:\s
                This program will create a settings.ser file next to your jar\s
                program only if you save a setting. You can delete this file\s
                by clicking the Reset javasend.Settings button. This will also reset\s
                the settings to default.\s
                """);

        // Handle MainMenuButton listener
        mainMenuButton.addActionListener(e -> cardLayout.show(stackedPanel, "MAIN"));

        // Handle adding components panel
        addComponent(scrollPane, panel, 10);
        addComponent(mainMenuButton, panel, 10);

        // add the panel to the stacked panes
        stackedPanel.add(panel, "HELP");
    }

    private void buildLoggerTab() {

        // Define JComponents
        JPanel panel = new JPanel();
        JButton loadLogFile = new JButton("Open Log File");
        JButton deleteLogFile = new JButton("Delete Logs");
        JButton mainMenuButton = new JButton("Main Menu");

        // Create a new panel and add to stacked panes
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(Box.createVerticalStrut(10)); // spacer

        // Handle button listeners
        loadLogFile.addActionListener(e -> handleLoadLogFile());
        deleteLogFile.addActionListener(e -> handleDeleteLogFile());
        mainMenuButton.addActionListener(e -> cardLayout.show(stackedPanel, "MAIN"));

        // Handle adding buttons to panel
        addComponent(loadLogFile, panel, 10);
        addComponent(deleteLogFile, panel, 10);
        addComponent(mainMenuButton, panel, 10);

        // add the panel to the stacked panes
        stackedPanel.add(panel, "LOGGER");
    }

    private void buildServerTab() {

        // Get IP address
        String url = server.getHostAddress();

        // Define JComponents
        JPanel panel = new JPanel();
        JLabel scanMeLabel = new JLabel("Scan Me!");
        JLabel serverLabel = new JLabel("URL: " + url);
        JLabel qrLabel;
        JButton closeServerButton = new JButton("Close Server");

        // Set panel defaults
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(Box.createVerticalStrut(10)); // spacer

        // Handle Qr code generation
        BufferedImage qrImage = QRCodeGenerator.generateQRCodeImage(url, 200, 200);

        if (qrImage != null) {
            qrLabel = new JLabel(new ImageIcon(qrImage));
        } else {
            qrLabel = new JLabel("No QR code found");
        }

        // Button handlers
        closeServerButton.addActionListener(e -> handleCloseServer());

        // Add components to panel
        addComponent(scanMeLabel, panel, 10);
        addComponent(qrLabel, panel, 10);
        addComponent(serverLabel, panel, 10);
        addComponent(closeServerButton, panel, 10);

        // add the panel to the stacked panes
        stackedPanel.add(panel, "SECONDARY");
    }

    // Method inherited from javasend.MessageReceiver interface
    @Override
    public void receiveMessage(String message) {

        systemOutput.setText(message);
    }

    // javasend.Settings handlers
    private void handleLoadSettingsTab() {

        cardLayout.show(stackedPanel, "SETTINGS");
        loadSettingsFields();
    }

    private void handleSettingsSaved() {

        int port = ServerHandler.verifyPort(portInput.getText());

        // Failed port verification
        if (port < 0)
        {
            return;
        }

        settingsHandler.updateSettings(port, downloadPathField.getText());
        cardLayout.show(stackedPanel, "MAIN");
    }

    private void handleSettingsReset() {

        // On settings reset, also update the values in the text fields to show the defaults
        settingsHandler.resetSettings();
        loadSettingsFields();
    }

    private void loadSettingsFields() {

        // On settings reset, also update the values in the text fields to show the defaults
        portInput.setText(String.valueOf(settingsHandler.getSettings().getPort()));
        downloadPathField.setText(settingsHandler.getSettings().getDownloadPath());
    }

    // Log handlers
    private void handleLoadLogFile() {

        logHandler.openLogFile();
    }

    private void handleDeleteLogFile() {

        logHandler.deleteLogFile();
        cardLayout.show(stackedPanel, "MAIN");
    }

    // Server handlers
    private void handleStartServer(String portInput) {

        // Attempt to initialize Server
        boolean successfulStart = server.InitializeServer(portInput);

        // If successful, then switch to the secondary pane
        if (successfulStart)
        {
            buildServerTab();
            cardLayout.show(stackedPanel, "SECONDARY");
        }
    }

    private void handleCloseServer() {

        server.closeServer();
        cardLayout.show(stackedPanel, "MAIN");
    }

    // helper method: Adds a component to a panel and automatically creates a spacer beneath it
    private void addComponent(JComponent component, JPanel panel, int spacer) {

        component.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(component);
        panel.add(Box.createVerticalStrut(spacer)); // spacer
    }
}
