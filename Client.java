/* 
 *Name:- Pratik Pramod Singh
 *ID:- 1001670417
 *Lab Assignment:- 3
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;


public class Client {
	BufferedReader in;
	PrintWriter out;
	JFrame frame = new JFrame("Student");
	JTextField textField = new JTextField("Name",20);
	JTextField subField = new JTextField("Subject name",20);
	JTextField advisor = new JTextField("Advisor name",20);
	JButton jb = new JButton("Request");
	JTextArea messageArea = new JTextArea(15, 50);

	Map<String, String> clients = new HashMap<String, String>(); // HashMap to store the Clients registered with the Server

	public Client() { // Constructor to initialize the Client
		textField.setEditable(false);
		subField.setEditable(false);
		messageArea.setEditable(false);
		advisor.setEditable(false);
		frame.getContentPane().add(textField, "North");
		frame.getContentPane().add(subField, "Center");
		frame.getContentPane().add(advisor, "West");
		frame.getContentPane().add(new JScrollPane(messageArea), "South");
		frame.getContentPane().add(jb, "East");
		frame.pack();
	}

	// Method for dialog box to enter the IP address of the Server. Enter
	// "127.0.0.1" as the IP address
	/*private String getServerAddress() {
		return JOptionPane.showInputDialog(frame, "Enter IP Address of the Server:", "Welcome to the Chat Room",
				JOptionPane.QUESTION_MESSAGE);
	}*/

	// Method for dialog box to register the name of the Client
//	private String getName() {
//		return JOptionPane.showInputDialog(frame, "Choose a screen name:", "Screen name selection",
//				JOptionPane.PLAIN_MESSAGE);
//	}
	
	private String getConnType() {
		return JOptionPane.showInputDialog(frame, "Choose a Connection Type: \n 1. Deliver the message to a single, designated client (1-to-1)\n 2. Deliver the message to all clients (1-to-N)", "Connection Type selection",
				JOptionPane.PLAIN_MESSAGE);
	}
	
	private String getClientName() {
		return JOptionPane.showInputDialog(frame, "Client name", "Client Name",
				JOptionPane.PLAIN_MESSAGE);
	}
	

	// Method to connect to server and start processing messages between client and
	// server
	private void run() throws IOException, ParseException {
		//String serverAddress = getServerAddress(); // getting the IP address from getServerAddress method
		String serverAddress = "127.0.0.1";
		Socket socket = new Socket(serverAddress, 9009); // Initialize a new socket connection
		in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // instance of Buffer Reader to accept
																					// messeges from the server
		out = new PrintWriter(socket.getOutputStream(), true); // instance of PrintWriter to send messages to the server
		DateTimeFormatter dtfd = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String tDate = dtfd.format(LocalDateTime.now());
		System.out.println(tDate);

		jb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { // get text from the text area
				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
				out.println(textField.getText() + ","+ subField.getText() + ","+ advisor.getText() + ",POST /localhost/server http/1.1,Host:" + serverAddress
						+ ",User-Agent:Mozilla/5.0, Content-Type:text,Content-Length:"
						+ Integer.toString(textField.getText().length()) + ", Date: "+dtf.format(LocalDateTime.now()));
				textField.setText("");
				subField.setText("subject");
				advisor.setText("advisor");
			}
		});
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
		String sName=null;
		// Process all messages from server, according to the protocol.
		try {
			while (true) {
				
				String line = in.readLine();
				System.out.println(line);
				if (line.startsWith("SUBMITNAME")) {
					out.println("Student");
//					out.println(getName()); // Send the desired screen name to the server for acceptance
				}
				// block of code is executed if name is accepted by the server
				else if (line.startsWith("NAMEACCEPTED")) {
					textField.setEditable(true);
					subField.setEditable(true);
					advisor.setEditable(true);
					 sName = line.split(",")[1];
					messageArea.append("You are " + sName + "\nStart sending messages\n");
				} else if (line.startsWith("duplicate")) {
					JOptionPane.showMessageDialog(null, "Name already exist.Please choose another name");
				} else if (line.contains("disconnected")) {
					messageArea.append(line + "\n");		// notifying if any of the clients is disconnected
				} else if (line.contains("connected")) {
					messageArea.append(line + "\n");		// notifying if any of the clients is connected
				}
				// block of code is executed if message is received from another client
				else if (line.startsWith("MESSAGE")) {
					String[] msg = line.split(",");
						messageArea.append(msg[1] + " (" + dtf.format(LocalDateTime.now()) + ") - " + msg[2] + "\n");
				}
				else if(line.startsWith("CONN")) {
					out.println(getConnType());
				}
				else if(line.startsWith("CNAME")) {
					out.println(getClientName());
				}
				else if (line.startsWith("invalid")) {
					JOptionPane.showMessageDialog(frame,"Ivalid Option Try again!!");
				}
				else if (line.startsWith("invalid2")) {
					JOptionPane.showMessageDialog(frame,"Client unreachable Try again!!");
				}
			}
		} catch (Exception e1) {
			socket.close();
			messageArea.append("Server is offline");
		}
	}

	public static void main(String[] args) throws Exception {
		Client client = new Client();
		client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		client.frame.setVisible(true);
		client.run();
	}
}