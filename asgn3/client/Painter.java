import java.awt.BorderLayout;
import java.awt.Dimension;
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

public class Painter {

    final static String BTN_CONNECT = "Connect";
    final static String BTN_DISCONNECT = "Disconnect";

    private static PaintClient controller;
    private static boolean isConnected;

    public Painter() {
        isConnected = false;
        controller = new PaintClient();
    }

    public static class WhiteboardGUI extends JPanel {
        private Logger log;
        private boolean isConnected;
        private JTextField textFieldIPAddress;
        private JTextField textFieldPortNumber;
        private JButton btnConnect;

        public WhiteboardGUI() {
            setLayout(new BorderLayout(0, 0));
            // Area at the top for setting connection parameters
            JPanel panelConnectionParams = new JPanel();
            // IP Address
            panelConnectionParams.add(new JLabel("IP Address"));
            textFieldIPAddress = new JTextField("localhost");
            panelConnectionParams.add(textFieldIPAddress);
            textFieldIPAddress.setColumns(12);
            // Port
            panelConnectionParams.add(new JLabel("Port"));
            textFieldPortNumber = new JTextField("5555");
            panelConnectionParams.add(textFieldPortNumber);
            textFieldPortNumber.setColumns(5);
            // Connect/Disconnect Button
            btnConnect = new JButton(BTN_CONNECT);
            btnConnect.addActionListener(new ConnectDisconnectListener());
            panelConnectionParams.add(btnConnect);
            add(panelConnectionParams, BorderLayout.NORTH);

            // The area in the center for the actual painting
            PaintPanel paintPanel = new PaintPanel(); // create paint panel
            add(paintPanel, BorderLayout.CENTER); // in center

            // create a label and place it in SOUTH of BorderLayout
            add(new JLabel( "Drag the mouse to draw" ), BorderLayout.SOUTH);
        }

        private class ConnectDisconnectListener implements ActionListener {
            private Logger log;

            public void actionPerformed(ActionEvent evt) {
                log = new Logger(Logger.DEBUG);
                log.debug("clicked connect/disconnect");
                if (!isConnected) {
                    log.debug("establishing connection");
                    try {
                        String ipAddress = textFieldIPAddress.getText();
                        int portNumber = Integer.parseInt(textFieldPortNumber.getText());
                        log.debug("controller " + controller);
                        boolean result = controller.connect(ipAddress, portNumber);
                        btnConnect.setText(BTN_DISCONNECT);
                        isConnected = true;
                        log.info("Connected with result: "+result);
                    } catch (NumberFormatException e) {
                        log.error("Non-integer port number supplied");
                    } catch (Exception e) {
                        log.error(e.toString());
                    }
                } else { // is connected already
                    log.debug("disconnecting");
                    try {
                        controller.disconnect();
                        isConnected = false;
                        log.debug("disconnected successfully");
                        btnConnect.setText(BTN_CONNECT);
                    } catch (Exception e) {
                        log.error(e.toString());
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        JFrame f = new JFrame("Whiteboard");
        Painter gui = new Painter();
        f.add(new WhiteboardGUI());
        f.pack();
        f.setMinimumSize(new Dimension(480, 200));
        f.setSize(500, 500);
        f.setVisible(true);
    }

}
