/* 
 *Name:- Pratik Pramod Singh
 *ID:- 1001670417
 *Lab Assignment:- 3
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.time.LocalDateTime;


public class Server {
	JFrame serverFrame = new JFrame("Server");
	static JTextArea serverMessageArea = new JTextArea(50, 50);
	private static final int PORT = 9009; // port number on which the server listens to the client
	private static ArrayList<String> names = new ArrayList<String>(); // HashSet to store the names of connected clients

	private static ArrayList<PrintWriter> clients = new ArrayList<PrintWriter>(); // HashSet to store the clients
																				// connections
	private static ArrayList<PrintWriter> advisor = new ArrayList<PrintWriter>();
	
	private static ArrayList<PrintWriter> notify = new ArrayList<PrintWriter>();
	// Constructor to display server GUI
	public Server() {
		serverMessageArea.setEditable(true);
		serverFrame.getContentPane().add(new JScrollPane(serverMessageArea), "Center");
		serverFrame.pack();
		serverMessageArea.append(LocalDateTime.now()+"\nChat Server is running\n");
	}
	
	
	// Main method to initialize the server
	public static void main(String[] args) throws Exception {
		Server server = new Server();
		server.serverFrame.setVisible(true);
		ServerSocket listener = new ServerSocket(PORT);
		try {
			while (true) {
				new Client(listener.accept()).start();
			}
		} finally {
			listener.close();
		}
	}

	// Client class to handle connections
	private static class Client extends Thread {
		private String name;
		private Socket socket;
		private BufferedReader in;
		private PrintWriter out;

		// Function constructor to set the connection of the client trying to connect
		public Client(Socket socket) {
			this.socket = socket;
		}

		// Method to set the name of the client
		public void run() {
            try {
                // Create character streams for the socket.
                in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                // Request a name from this client.  Keep requesting until
                // a name is submitted that is not already used.  
                while (true) {
                    out.println("SUBMITNAME");
                    name = in.readLine();
                    if (name == null) {
                        return;
                    }
                    synchronized (names) {
                        if (!names.contains(name)) {
                            names.add(name);
                            break;
                        }
                        else {
                        	out.println("duplicate");
                        }
                    }
                }
                // if the name is accepted
                // add the socket's printwriter to the set of all writers
                // Now this client can receive broadcast messages.
                out.println("NAMEACCEPTED,"+name);
                System.out.println(out);
                for(PrintWriter writer : clients) {
                	writer.println("\n"+name + " is connected\n");
                	System.out.println("broadcast");
                }
//                for(PrintWriter writer : advisor) {
//                	//writer.println("\n"+name + " is connected\n");
//                	System.out.println("broadcast");
//                }
//                for(PrintWriter writer : notify) {
//                	//writer.println("\n"+name + " is connected\n");
//                	System.out.println("broadcast");
//                }
                clients.add(out);
                advisor.add(out);
                notify.add(out);
                System.out.println("client "+name+" is connected");  
                serverMessageArea.append(name + " is connected\n");
               
                // Accept messages from this client and broadcast them.
                
                while (true) {
                    String s = in.readLine();
                    System.out.println(s+",.......STS");
                    if(!(s.equals("null"))) {
                    String st =s.split(",")[0];
                    System.out.println(st+".......ST");
                    if(st.equals("POLL")) {
                    	File file = new File("save.txt");
                    	
                    	serverMessageArea.append("Content:"+s.split(",")[0]+", "+s.split(",")[1]+",  "
                            	+s.split(",")[2]+",\n"+s.split(",")[3]+",\n"+s.split(",")[4]+",\n"+s.split(",")[5]+"\n"+s.split(",")[6]+"\n");
                    	
                    	if (file.length() != 0) {
                    		
                        BufferedReader reader;
                		try {
                			reader = new BufferedReader(new FileReader("save.txt"));
                			String line; 
                			while ((line = reader.readLine())!= null) {
                				// read next line
                				String clientName = "Advisor";
                				System.out.println(line);
                        		for (int y=0; y<names.size(); y++) {  
                        			if(clientName.contentEquals(names.get(y)) ) {
                        				System.out.println(clientName+"..............X");
                        				System.out.println(names.get(y));
                        				System.out.println(advisor.get(y));
                        				System.out.println(advisor);
                        				System.out.println(clients);
                        				advisor.get(y).println("REG,"+line);
                        				break;
                        			}
                        		}
                        		String data = in.readLine();
                        		System.out.println(data+"................... # ");
                        		if(!(data.contentEquals(""))) {
                        		String data1 = data.split(",")[1]+","+data.split(",")[2]+","+data.split(",")[3]+","+data.split(",")[4]+"\n";
                        		System.out.println(data1+"................... # ");
                        		
                				String file1 = "read.txt";
                            	FileWriter fr = new FileWriter(file1, true);
                            	BufferedWriter br = new BufferedWriter(fr);
                            	br.write(data1);
                            	br.close();
                            	fr.close();
                        		}
                			}
                			PrintWriter pw = new PrintWriter("save.txt");
                			pw.close();
                			reader.close();
                		}
                		 catch (IOException e) {
                			e.printStackTrace();
                		}
                    	} else {
                    		String clientName = "Advisor";
                    		for (int y=0; y<names.size(); y++) {  
                    			if(clientName.contentEquals(names.get(y)) ) {
                    				System.out.println(clientName+"..............X");
                    				System.out.println(names.get(y));
                    				System.out.println(advisor.get(y));
                    				System.out.println(advisor);
                    				System.out.println(clients);
                    				advisor.get(y).println("NON");
                    				break;
                    			}
                    		}
                    	}
                    }
                    
                    if(st.contentEquals("NOTIFY")) {
                    	File file = new File("read.txt");
                    	
                    	serverMessageArea.append("Content:"+s.split(",")[0]+", "+s.split(",")[1]+",  "
                            	+s.split(",")[2]+",\n"+s.split(",")[3]+",\n"+s.split(",")[4]+",\n"+s.split(",")[5]+",\n");
                    	
                    	if (file.length() != 0) {
                    		
                        BufferedReader reader;
                		try {
                			reader = new BufferedReader(new FileReader("read.txt"));
                			String line; 
                			while ((line = reader.readLine())!= null) {
                				// read next line
                				String clientName = "notify";
                				System.out.println(line);
                        		for (int y=0; y<names.size(); y++) {  
                        			if(clientName.contentEquals(names.get(y)) ) {
                        				System.out.println(clientName+"..............X");
                        				System.out.println(names.get(y));
                        				System.out.println(notify.get(y));
                        				System.out.println(notify);
                        				System.out.println(clients);
                        				notify.get(y).println("REG1>"+line);
                        				break;
                        			}
                        		}
                        		
                			}
                			PrintWriter pw = new PrintWriter("read.txt");
                			pw.close();
                			reader.close();
                		}
                		 catch (IOException e) {
                			e.printStackTrace();
                		}
                    	} else {
                    		String clientName = "notify";
            				for (int y=0; y<names.size(); y++) {  
                    			if(clientName.contentEquals(names.get(y)) ) {
                    				System.out.println(clientName+"..............X");
                    				System.out.println(names.get(y));
                    				System.out.println(notify.get(y));
                    				System.out.println(notify);
                    				System.out.println(clients);
                    				notify.get(y).println("NON");
                    				break;
                    			}
                    		}
                    	}
                    }
                    
                    if(!(st.equals("POLL")) && !(st.equals("NOTIFY")) && !(st.contentEquals(""))) {
                    
                    serverMessageArea.append("Content:"+s.split(",")[0]+", "+s.split(",")[1]+",  "
                        	+s.split(",")[2]+",\n"+s.split(",")[3]+",\n"+s.split(",")[4]+",\n"+s.split(",")[5]+",\n"+s.split(",")[6]+",\n");
                    System.out.println(s+"..........S");
                    String student;
                    String advisor;
                    String subject;
                    student=s.split(",")[0];
                    advisor=s.split(",")[1];
                    subject=s.split(",")[2];
                    System.out.println(student+advisor+subject);
                    if (student == null && advisor == null && subject == null) {
                        return;
                    }
                    
                    if((student != null && advisor != null && subject != null) ) {
                    	String file = "save.txt";
                    	FileWriter fr = new FileWriter(file, true);
                    	BufferedWriter br = new BufferedWriter(fr);
                    	String data = student+","+advisor+","+subject+"\n";
                    	br.write(data);

                    	br.close();
                    	fr.close();	
                    	}
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                    serverMessageArea.append( "From client "+name + ":" + student +"\n"+dtf.format(LocalDateTime.now())+"\n");    
                    }
                
               } 
             }      
            } catch (IOException e) {
                System.out.println(e);
            } finally {
                // This client is going down!  Remove its name and its print
                // writer from the sets, and close its socket.
                if (name != null) {
                    names.remove(name);    
                    System.out.println("nr");
                }
                if (out != null) {
                    clients.remove(out);
                    advisor.remove(out);
                    notify.remove(out);
                    for(PrintWriter writer : clients) {
                    	writer.println(name + " is disconnected\n");
                    	System.out.println("broadcast");
                    }
                	serverMessageArea.append(name + " is disconnected\n");
                	System.out.println("cr");
                }
                try {
                    socket.close();
                } catch (IOException e) {
                System.out.println(e);
                }
            }
        }
    }
}