package server;


import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class Server extends JFrame {
	final static int PORT = 11111;
	final static String ROOT = "src/server/files";
	final static String LOGINS = "src/server/logins.txt";
	private ServerSocket serverSocket;
	private boolean serverOn = false;
	static final int BUFFER_SIZE = 4096;
	JTextArea textArea = new JTextArea();
	private String users;
	private String passwords;
	
	public Server() {
		super("FTServer");
		BorderLayout layout = new BorderLayout();
		layout.setHgap(30);
		layout.setVgap(30);
		setLayout(layout);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(500,300);
	    

		JScrollPane scrollPane = new JScrollPane(textArea); 
		textArea.setEditable(false);
		scrollPane.setBorder(BorderFactory.createTitledBorder("Log"));
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10)); 
		JButton startButton = new JButton("Start");
		JButton stopButton = new JButton("Stop");
		buttonPanel.add(startButton);
		buttonPanel.add(stopButton);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	new Thread() {
    				public void run() {
    					startServer();	
    				}
    			}.start();
            }
        });
		
        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stop();
            }
        });
		setVisible(true);
		
	}
	
	private void startServer() {
		try {
            serverSocket = new ServerSocket(PORT); 
            serverOn = true;
            setUsers();
            textArea.append("Server started.\n");
        } 
        catch(IOException ioe) {
            textArea.append("Failed to create server socket.\n");
        }
		
		while(serverOn) {
			try {
				Socket clientSocket = serverSocket.accept();
				ClientServiceThread cliThread = new ClientServiceThread(clientSocket);
				cliThread.start();
			} catch(IOException ioe) {
				if (serverOn) {
					textArea.append("Failed to create client socket.\n");
					ioe.printStackTrace();
				}
			}
		}
	}
	
	private void stop() {
		try {
			serverOn = false;
			serverSocket.close();
			textArea.append("Server stopped\n");
		} catch(Exception ioe) {
			textArea.append("Problem stopping server socket.\n");
            System.out.println("Problem stopping server socket\n"); 
            System.exit(-1); 
        } 
	}
	
	private void setUsers() {
			BufferedReader br=null;
		try {
			FileReader fr = new FileReader(LOGINS);
			br = new BufferedReader(fr);
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				users = sCurrentLine.split(" ")[0];
				passwords = sCurrentLine.split(" ")[1];
			}
		} catch(IOException ioe) {
			textArea.append("Can't read logins.\n");
		} finally {
			try {br.close();} catch(IOException e){};
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SwingUtilities.invokeLater(new Runnable() {
		      public void run() {
		        new Server();
		      }
		});
	}

	
class ClientServiceThread extends Thread {
	private Socket clientSocket;
	private boolean m_bRunThread = true;
	private ObjectInputStream ois;
	private boolean serverOn = true;
	private ObjectOutputStream oos;
	private String user;
	private String password;
	public ClientServiceThread() {
		super();
	}
	public ClientServiceThread(Socket s){
		clientSocket = s;
	}
	public void run()  {
		try {
			oos = new ObjectOutputStream(clientSocket.getOutputStream());
			ois = new ObjectInputStream(clientSocket.getInputStream());
			String auth = (String)ois.readObject();
			String[] parts;
			parts = auth.split(" ", 2);
			user = parts[0];
			password = parts[1];
			if(!login(user, password)) {
				oos.writeObject("FAIL");
				oos.flush();
				m_bRunThread = false;
			} else {
				oos.writeObject("SUCC");
				oos.flush();
				textArea.append("Client(" + clientSocket.getRemoteSocketAddress().toString() + ") connected.\n");
			}
			while(m_bRunThread) 
			{                    
				// read incoming stream 
				String clientCommand = (String)ois.readObject();
				parts = clientCommand.split(" ", 2);
				if(!serverOn) 
				{ 
                    m_bRunThread = false;
                    continue;
                } 

                if(parts[0].equalsIgnoreCase("quit")) { 
                    m_bRunThread = false;   
        			textArea.append("Client(" + clientSocket.getInetAddress().toString() + ") disconnected\n"); 
                } else if (parts[0].equalsIgnoreCase("list")) {
                	sendListFiles();
                } else if (parts[0].equalsIgnoreCase("retr")) {
                   	sendFile(parts[1]);
                } else if (parts[0].equalsIgnoreCase("stor")) {
                   	getFile(parts[1]);
                }
			}
		} catch(ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        } catch(IOException ioe) {
        	ioe.printStackTrace();
        } finally {
        	try {
        		oos.close();
        		ois.close();
        		clientSocket.close();
        	} catch(IOException ioe) {
        		textArea.append("Error during connection closing.\n");
        	}
        }
	} 
		
		
	private void sendListFiles() {
		String[] files = new File(Server.ROOT).list();
		try{
			oos.writeObject(files);
			oos.flush();
		} catch(Exception ioe){
			ioe.printStackTrace();
		}
	}
		
	private void sendFile(String file) {
		File fileToSend = new File(Server.ROOT + File.separator + file);
        byte[] buffer = new byte[Server.BUFFER_SIZE];
        int bytesRead = -1;
		FileInputStream inputStream;
		try {
			inputStream = new FileInputStream(fileToSend);
		} catch(FileNotFoundException fnfe) {
			inputStream = null;
			return;
		}
        try {
			oos.writeLong(fileToSend.length());
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				oos.write(buffer, 0, bytesRead);
			}
	        oos.flush();
			textArea.append("Client(" + clientSocket.getInetAddress().toString() + ") downloaded " + file + "\n");
		} catch(IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
		        inputStream.close();
			} catch(IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
	private void getFile(String file) {
		File fileToSave = new File(Server.ROOT + File.separator + file);
		long fileSize = 0;
		int totalBytesRead = 0;
        int bytesRead = -1;
    	FileOutputStream outputStream;
        byte[] buffer = new byte[Server.BUFFER_SIZE];
        try {
    		outputStream = new FileOutputStream(fileToSave);
    	} catch (FileNotFoundException fnfe) {
    		outputStream = null;
    		return;
        }
		try {
			fileSize = ois.readLong();
	    	while (totalBytesRead != fileSize) {
	           	bytesRead = ois.read(buffer);
	           	outputStream.write(buffer, 0, bytesRead);
	           	totalBytesRead += bytesRead;
	        }
	    	textArea.append("Client(" + clientSocket.getInetAddress().toString() + ") uploaded " + file + "\n");
	    } catch(IOException ioe){
	    	ioe.printStackTrace();
	    } finally {
	    	try {
		    	outputStream.close();
	    	} catch (IOException ioe) {
	    		ioe.printStackTrace();
	    	}
	    }
	}
	
	private boolean login(String u, String p) {
		return (users.equals(u) && passwords.equals(p));
	}
}
}

