import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

class ClientGuiJPanel extends JPanel {
	private JTextField textFieldIPAddress;
	private JTextField textFieldPortNumber;
	private JTextField textFieldGetWord;
	private JTextField textFieldSetWordB;
	private JTextField textFieldSetWordA;
	private JTextField textFieldRemoveWord;

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

		JButton btnConnect = new JButton("Connect");
		panelConnectionParams.add(btnConnect);

		JTabbedPane tabbedPaneRequestMaker = new JTabbedPane(JTabbedPane.TOP);
		add(tabbedPaneRequestMaker, BorderLayout.CENTER);

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

}


public class ClientGui {

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
