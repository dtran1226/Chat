package chat;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.Font;
import javax.swing.UIManager;
import javax.swing.JScrollPane;

public class ChatClient extends JFrame {

	/**
	 * Create instance variables
	 */
	private static final long serialVersionUID = 1L;
	private static JPanel contentPane;
	private static JTextArea txtaCont;
	private static JScrollPane scrPane;
	private static JTextField txtMsg;
	private static JButton btnSend;
	public static Socket socket;
	public static DataInputStream dis;
	public static DataOutputStream dos;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		// Create and display the application form
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChatClient frame = new ChatClient();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		// Create sockets, listen to input stream from server and display
		// it on chat's content area
		try {
			socket = new Socket("192.168.126.1", 4242);
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());

			// Keep listening to messages from server until the "exit"
			// signal
			String inMsg = "";
			while (!"exit".equals(inMsg)) {
				inMsg = dis.readUTF();
				String preCont = txtaCont.getText();
				if ("".equals(preCont)) {
					txtaCont.setText("Server: " + inMsg);
				} else {
					txtaCont.setText(preCont + "\nServer: " + inMsg);
				}
				// Scroll down to the newest message from server
				txtaCont.setCaretPosition(txtaCont.getDocument().getLength());
			}
		} catch (IOException iOE) {
			iOE.printStackTrace();
		}
	}

	/*
	 * Enable/disable 'Send' button when message text field changed
	 */
	public void updateSendButton() {
		if ("".equals(txtMsg.getText()) || "Type your message here...".equals(txtMsg.getText())) {
			btnSend.setEnabled(false);
		} else {
			btnSend.setEnabled(true);
		}
	}

	/*
	 * Actions to do when clicking 'Send' button
	 */
	public void clickSendBtn() {
		// Send entered message from client to server
		try {
			String outMsg = txtMsg.getText();
			dos.writeUTF(outMsg);
			// Update chat's content area
			String preCont = txtaCont.getText();
			if ("".equals(preCont)) {
				txtaCont.setText("\t\tClient: " + outMsg);
			} else {
				txtaCont.setText(preCont + "\n\t\tClient: " + outMsg);
			}
			// Keep the message text field being focused, so the client
			// can conveniently continue to write the next message
			txtMsg.setText("");
			txtMsg.requestFocusInWindow();
		} catch (IOException iOE) {
			iOE.printStackTrace();
		}
	}

	/**
	 * Create the frame.
	 */
	public ChatClient() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		// Create a text area to display the chat's conversation with
		// customized properties
		txtaCont = new JTextArea();
		txtaCont.setWrapStyleWord(true);
		txtaCont.setLineWrap(true);
		txtaCont.setEditable(false);
		txtaCont.setForeground(new Color(0, 0, 255));
		txtaCont.setBackground(new Color(0, 255, 255));
		txtaCont.setFont(new Font("UTF-8", Font.PLAIN, 20));
		txtaCont.setBounds(0, 0, 444, 211);
		// Add scroll bar for the chat conversation's area
		scrPane = new JScrollPane(txtaCont);
		scrPane.setBounds(0, 0, 444, 211);
		contentPane.add(scrPane);

		// Create a text field to enter messages with customized
		// properties
		txtMsg = new JTextField();
		txtMsg.setForeground(Color.GRAY);
		txtMsg.setText("Type your message here...");
		txtMsg.setFont(new Font("UTF-8", Font.PLAIN, 18));
		txtMsg.setBackground(Color.WHITE);
		txtMsg.setBounds(0, 211, 338, 49);
		contentPane.add(txtMsg);
		txtMsg.setColumns(10);
		// Set FocusListener event for message text field
		txtMsg.addFocusListener(new FocusListener() {
			/*
			 * If the text field for messages is focused:
			 * 
			 * 1. If client has not typed any text yet: default placeholder would be
			 * replaced by empty text in black foreground
			 * 
			 * 2. If client already typed some text but have not sent yet: do nothing
			 */
			@Override
			public void focusGained(FocusEvent e) {
				if (txtMsg.getText().equals("Type your message here...")) {
					txtMsg.setText("");
					txtMsg.setForeground(Color.BLACK);
				}
			}

			/*
			 * If the text field for messages is unfocused:
			 * 
			 * 1. If client has not typed any text yet: default placeholder would be shown
			 * in gray foreground
			 * 
			 * 2. If client already typed some text but have not sent yet: do nothing
			 */
			@Override
			public void focusLost(FocusEvent e) {
				if (txtMsg.getText().isEmpty()) {
					txtMsg.setForeground(Color.GRAY);
					txtMsg.setText("Type your message here...");
				}
			}
		});

		// Add text-changed listener to message text field
		txtMsg.getDocument().addDocumentListener(new TextChangedListener() {
			@Override
			public void update() {
				updateSendButton();
			}
		});

		// Acts like 'Send' button is clicked when client presses "Enter"
		// key while writing a message
		txtMsg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clickSendBtn();
			}
		});

		// Create a button to send messages with customized properties
		btnSend = new JButton("Send");
		btnSend.setEnabled(false);
		btnSend.setForeground(Color.BLUE);
		btnSend.setFont(new Font("Bell MT", Font.BOLD, 20));
		btnSend.setBackground(UIManager.getColor("Button.background"));
		btnSend.setBounds(337, 211, 107, 49);
		contentPane.add(btnSend);

		// Add 'Click' event for 'Send' button
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clickSendBtn();
			}
		});
	}
}
