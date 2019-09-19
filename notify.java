/* 
 *Name:- Pratik Pramod Singh
 *ID:- 1001670417
 *Lab Assignment:- 3
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;



// Client class to initialize the GUI
public class notify {
	BufferedReader in;
	PrintWriter out;
	JFrame frame = new JFrame("NOTIFICATIONS");
	JTextArea messageArea = new JTextArea(30, 50);

	Map<String, String> notify = new HashMap<String, String>(); // HashMap to store the Clients registered with the Server

	public notify() { // Constructor to initialize the Client
		messageArea.setEditable(false);
		frame.getContentPane().add(new JScrollPane(messageArea), "Center");
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
	
//	private String getClientName() {
//		return JOptionPane.showInputDialog(frame, "Client name", "Client Name",
//				JOptionPane.PLAIN_MESSAGE);
//	}
	

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

	class SayHello extends TimerTask {
		    public void run() {
		    	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
				out.println("NOTIFY,POST /localhost/server http/1.1,Host:" + serverAddress 
				+ ",User-Agent:Mozilla/5.0, Content-Type:text,Content-Length:"
				+ "0" + ", Date: "+dtf.format(LocalDateTime.now())); 
		    }
		}

		// And From your main() method or any other method
		Timer timer = new Timer();
		timer.schedule(new SayHello(), 5000, 10000);
				
		//String sName=null;
		// Process all messages from server, according to the protocol.
		try {
			while (true) {
				
				String line = in.readLine();
				System.out.println(line);
				
				if (line.startsWith("SUBMITNAME")) {
					out.println(/*getName()*/"notify"); // Send the desired screen name to the server for acceptance
				}
				// block of code is executed if name is accepted by the server
				else if (line.startsWith("NAMEACCEPTED")) {
					 //sName = line.split(",")[1];
					//messageArea.append("You are " + sName + "\nStart sending messages\n");
				} else if (line.startsWith("duplicate")) {
					JOptionPane.showMessageDialog(null, "Name already exist.Please choose another name");
				}else if (line.contains("disconnected")) {
					messageArea.append(line + "\n");		// notifying if any of the clients is disconnected
				} else if (line.contains("connected")) {
					messageArea.append(line + "\n");		// notifying if any of the clients is connected
				}
				// block of code is executed if message is received from another client
				else if (line.startsWith("MESSAGE")) {
					//messageArea.append("\n");
					//String[] msg = line.split(",");
					//DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
					//messageArea.append(msg[1] + " (" + dtf.format(LocalDateTime.now()) + ") - " + msg[2] + "\n");
				}
				else if(line.startsWith("CONN")) {
					out.println(getConnType());
				}
				else if(line.startsWith("REG1")) {
					
					String data = line.split(">")[1]+"\n";
    				messageArea.append(data);
    			}
				else if(line.startsWith("NON")) {
					messageArea.append("No messages found\n");
				}
				
			}
		} catch (Exception e1) {
			socket.close();
			messageArea.append("Server is offline");
		}
	}

	public static void main(String[] args) throws Exception {
		notify notify = new notify();
		notify.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		notify.frame.setVisible(true);
		notify.run();
	}
}