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

    public static class WhiteboardGUI extends JPanel {
        private Logger log;
        private boolean isConnected;
        private JTextField textFieldIPAddress;
        private JTextField textFieldPortNumber;
        private JButton btnConnect;

        public WhiteboardGUI() {
            this.log = new Logger(Logger.DEBUG);
            isConnected = false;

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
            btnConnect = new JButton("Connect");
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
            public void actionPerformed(ActionEvent evt) {
                log.debug("clicked connect/disconnect");
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
        WhiteboardGUI gui = new WhiteboardGUI();
        f.add(new WhiteboardGUI());
        f.pack();
        f.setMinimumSize(new Dimension(480, 200));
        f.setSize(500, 500);
        f.setVisible(true);
    }
}
