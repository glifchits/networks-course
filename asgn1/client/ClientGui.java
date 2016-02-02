import java.net.SocketException;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * ClientGui
 *
 * This class encapsulates the Synonym Protocol GUI.
 * It only contains GUI elements and listeners for those GUI elements.
 * Actual dispatching of requests is handled entirely in the SynonymClient.
 *
 * @author Dallas Fraser - 110242560
 * @author George Lifchits - 100691350
 * @version 1.0
 * @see Class#SynonymClient
 */
public class ClientGui {

    private enum Tab {
        GET, SET, REMOVE
    }

    public static class ClientGuiJPanel extends JPanel {
        private JTextField textFieldIPAddress;
        private JTextField textFieldPortNumber;
        private JTextField textFieldGetWord;
        private JTextField textFieldSetWordA;
        private JTextField textFieldSetWordB;
        private JTextField textFieldRemoveWord;
        private JButton btnConnect;
        private JButton btnMakeRequest;
        private JTextArea textAreaResponseOutput;
        private boolean isConnected = false;

        private Tab currentTab = Tab.GET;

        /**
        * Some public methods to modify GUI element appearance
        */

        public void setConnectBtnText(String text) {
            btnConnect.setText(text);
        }

        public void setMakeRequestEnabled(boolean enabled) {
            btnMakeRequest.setEnabled(enabled);
        }

        /**
        * Create the panel.
        */
        public ClientGuiJPanel() {
            setLayout(new BorderLayout(0, 0));

            JPanel panelConnectionParams = new JPanel();
            add(panelConnectionParams, BorderLayout.NORTH);

            JLabel lblIpAddress = new JLabel("IP Address");
            panelConnectionParams.add(lblIpAddress);

            textFieldIPAddress = new JTextField("localhost");
            panelConnectionParams.add(textFieldIPAddress);
            textFieldIPAddress.setColumns(12);

            JLabel lblPort = new JLabel("Port");
            panelConnectionParams.add(lblPort);

            textFieldPortNumber = new JTextField("5555");
            panelConnectionParams.add(textFieldPortNumber);
            textFieldPortNumber.setColumns(5);

            btnConnect = new JButton("Connect");
            btnConnect.addActionListener(new ConnectDisconnectListener());
            panelConnectionParams.add(btnConnect);

            JPanel panelRequestMaker = new JPanel();
            add(panelRequestMaker, BorderLayout.CENTER);

            JTabbedPane tabbedPaneRequestMaker = new JTabbedPane(JTabbedPane.TOP);

            ChangeListener changeListener = new ChangeListener() {
                /*
                * Code adapted from http://www.java2s.com/Tutorial/Java/0240__Swing/ListeningforSelectedTabChanges.htm
                */
                public void stateChanged(ChangeEvent changeEvent) {
                    JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
                    int index = sourceTabbedPane.getSelectedIndex();
                    String title = sourceTabbedPane.getTitleAt(index);
                    switch (title) {
                        case "Get": currentTab = Tab.GET; break;
                        case "Set": currentTab = Tab.SET; break;
                        case "Remove": currentTab = Tab.REMOVE; break;
                    }
                }
            };
            tabbedPaneRequestMaker.addChangeListener(changeListener);
            panelRequestMaker.add(tabbedPaneRequestMaker);

            btnMakeRequest = new JButton("Send Request");
            btnMakeRequest.setEnabled(false);
            btnMakeRequest.addActionListener(new MakeRequestListener());
            panelRequestMaker.add(btnMakeRequest);

            JPanel panelGetRequest = new JPanel();
            tabbedPaneRequestMaker.addTab("Get", null, panelGetRequest, null);

            JLabel lblGetASynonym = new JLabel("Get synonyms for the word:");
            panelGetRequest.add(lblGetASynonym);

            textFieldGetWord = new JTextField();
            panelGetRequest.add(textFieldGetWord);
            textFieldGetWord.setColumns(10);

            JPanel panelSetRequest = new JPanel();
            tabbedPaneRequestMaker.addTab("Set", null, panelSetRequest, null);
            panelSetRequest.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

            JLabel lblDefineTwoWords = new JLabel("Define:");
            panelSetRequest.add(lblDefineTwoWords);

            textFieldSetWordB = new JTextField();
            panelSetRequest.add(textFieldSetWordB);
            textFieldSetWordB.setColumns(10);

            JLabel lblIsASynonym = new JLabel("is a synonym for");
            panelSetRequest.add(lblIsASynonym);

            textFieldSetWordA = new JTextField();
            panelSetRequest.add(textFieldSetWordA);
            textFieldSetWordA.setColumns(10);

            JPanel panelRemoveRequest = new JPanel();
            tabbedPaneRequestMaker.addTab("Remove", null, panelRemoveRequest, null);

            JLabel lblRemoveAWord = new JLabel("Remove a word from the dictionary:");
            panelRemoveRequest.add(lblRemoveAWord);

            textFieldRemoveWord = new JTextField();
            panelRemoveRequest.add(textFieldRemoveWord);
            textFieldRemoveWord.setColumns(10);

            JPanel panelResponseOutput = new JPanel();
            add(panelResponseOutput, BorderLayout.SOUTH);
            panelResponseOutput.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

            textAreaResponseOutput = new JTextArea();
            textAreaResponseOutput.setFont(new Font("Courier", Font.PLAIN, 14));
            textAreaResponseOutput.setRows(7);
            textAreaResponseOutput.setColumns(60);
            textAreaResponseOutput.setEditable(false);
            panelResponseOutput.add(textAreaResponseOutput);
        }

        /**
         * Listener for the connect/disconnect button
         * Will establish or end a connection using the SynonymClient
         * Will display any exceptions in the response text area
         */
        private class ConnectDisconnectListener implements ActionListener {
            public void actionPerformed(ActionEvent evt) {
                if (!isConnected) {
                    System.out.println("should connect");
                    try {
                        String ipAddress = textFieldIPAddress.getText();
                        int portNumber = Integer.parseInt(textFieldPortNumber.getText());
                        boolean result = controller.connect(ipAddress, portNumber);
                        System.out.println("Connected with result: " + result);
                        if (result) {
                            clientConnect();
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("non-integer port number supplied");
                    } catch (Exception e) {
                        System.err.println(e);
                    }

                } else /* isConnected = true */ {
                    System.out.println("should disconnect");
                    try {
                        controller.disconnect();
                        clientDisconnect();
                    } catch (Exception e) {
                        System.err.println("Disconnect exception: " + e);
                    }
                }
            }
        }

        /**
         * Helper function which changes the GUI to its state once a connection
         * is created.
         * @param message a message to display in the response textarea with
         * information about the connection status
         */
        private void clientConnect(String message) {
            // now connected: the button should offer chance to disconnect
            isConnected = true;
            setConnectBtnText("Disconnect");
            setMakeRequestEnabled(true);
            textAreaResponseOutput.setText(message);
        }

        /**
         * Puts GUI in connected state, with default message simply "Connected"
         */
        private void clientConnect() {
            clientConnect("Connected");
        }

        /**
         * Puts the GUI in a disconnected state with message
         */
        private void clientDisconnect(String message) {
            isConnected = false;
            setConnectBtnText("Connect");
            setMakeRequestEnabled(false);
            textAreaResponseOutput.setText(message);
        }
        /**
         * Puts the GUI in a disconnected state with just the message
         * "Disconnected"
         */
        private void clientDisconnect() {
            clientDisconnect("Disconnected");
        }

        /**
         * Listener for the button titled "Send Request"
         * Here we collect the request parameters from the GUI elements
         * and invoke the appropriate method on the SynonymClient
         */
        private class MakeRequestListener implements ActionListener {

            /**
             * When the Send Request button is clicked, this method Decides
             * which type of request to dispatch based on the current tab.
             */
            public void actionPerformed(ActionEvent evt) {
                switch (currentTab) {
                    case GET:
                    makeGet(); break;
                    case SET:
                    makeSet(); break;
                    case REMOVE:
                    makeRemove(); break;
                }
            }

            /**
             * Makes a GET request to the SynonymProtocol server
             */
            private void makeGet() {
                System.out.println("Make GET");
                String word = textFieldGetWord.getText();
                try {
                    String response = controller.get(word);
                    textAreaResponseOutput.setText(response);
                } catch (SocketException e) {
                    clientDisconnect(
                    "Error: connection to the server was lost.\n" +
                    "Ensure the server is running and still reachable."
                    );
                }
            }
            /**
             * Makes a SET request to the SynonymProtocol server
             */
            private void makeSet() {
                System.out.println("Make SET");
                String word1 = textFieldSetWordA.getText();
                String word2 = textFieldSetWordB.getText();
                try {
                    String response = controller.set(word1, word2);
                    textAreaResponseOutput.setText(response);
                } catch (SocketException e) {
                    clientDisconnect(
                    "Error: connection to the server was lost.\n" +
                    "Ensure the server is running and still reachable."
                    );
                }
            }

            /**
             * Makes a REMOVE request to the SynonymProtocol server
             */
            private void makeRemove() {
                System.out.println("Make REMOVE");
                String word = textFieldRemoveWord.getText();
                try {
                    String response = controller.remove(word);
                    textAreaResponseOutput.setText(response);
                } catch (SocketException e) {
                    clientDisconnect(
                    "Error: connection to the server was lost.\n" +
                    "Ensure the server is running and still reachable."
                    );
                }
            }

        }

    }

    private static SynonymClient controller;

    public ClientGui() {
        this.controller = new SynonymClient();
    }

    /**
    * This code is adapted from
    * https://docs.oracle.com/javase/tutorial/uiswing/painting/step2.html
    */

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        JFrame f = new JFrame("Synonym Protocol GUI");
        ClientGui gui = new ClientGui();
        f.add(new ClientGuiJPanel());
        f.pack();
        f.setVisible(true);
    }

}
