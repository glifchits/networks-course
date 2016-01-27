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

        private boolean isConnected = false;

        private Tab currentTab = Tab.GET;

        public void setConnectBtnText(String text) {
            btnConnect.setText(text);
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

    		textFieldIPAddress = new JTextField();
    		panelConnectionParams.add(textFieldIPAddress);
    		textFieldIPAddress.setColumns(12);

    		JLabel lblPort = new JLabel("Port");
    		panelConnectionParams.add(lblPort);

    		textFieldPortNumber = new JTextField();
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

            JButton btnMakeRequest = new JButton("Send Request");
            btnMakeRequest.addActionListener(new MakeRequestListener());
            panelRequestMaker.add(btnMakeRequest);

    		JPanel panelGetRequest = new JPanel();
    		tabbedPaneRequestMaker.addTab("Get", null, panelGetRequest, null);

    		JLabel lblGetASynonym = new JLabel("Get a synonym for the word:");
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

    		JTextArea textAreaResponseOutput = new JTextArea();
    		textAreaResponseOutput.setFont(new Font("Courier", Font.PLAIN, 14));
    		textAreaResponseOutput.setRows(7);
    		textAreaResponseOutput.setColumns(40);
    		panelResponseOutput.add(textAreaResponseOutput);
    	}

        private class ConnectDisconnectListener implements ActionListener {
            public void actionPerformed(ActionEvent evt) {
                System.out.println("clicked conn/disconnect");
                if (!isConnected) {
                    System.out.println("should connect");
                    try {
                        String ipAddress = textFieldIPAddress.getText();
                        int portNumber = Integer.parseInt(textFieldPortNumber.getText());
                        boolean result = controller.connect(ipAddress, portNumber);
                        System.out.println("Connected with result: " + result);
                        if (result) {
                            isConnected = result;
                            setConnectBtnText("Disconnect");
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
                        setConnectBtnText("Connect");
                    } catch (Exception e) {
                        System.err.println("Disconnect exception: " + e);
                    }
                }
            }
        }

        private class MakeRequestListener implements ActionListener {

            private void makeGet() {
                System.out.println("Make GET");
            }

            private void makeSet() {
                System.out.println("Make SET");
            }

            private void makeRemove() {
                System.out.println("Make REMOVE");
            }

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
        }

    }

    private static SynonymClient controller;

    public ClientGui() {
        this.controller = new SynonymClient();
    }

    /*
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
        f.add(new ClientGuiJPanel());
        f.pack();
        f.setVisible(true);
    }

}
