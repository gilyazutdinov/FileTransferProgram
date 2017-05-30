package client.model;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.Observable;


public class Model extends Observable{
	private String host="127.0.0.1";
	private String user;
	private String password;
	private int port=11111;
	private String[] files;
	private Socket socket;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private boolean connected = false;
	private static final int BUFFER_SIZE = 4096;
	private long fileSize=0;
    private long totalBytesRead;
	
	public void setHost(String h) {
		host = h;
	}
	
	public long getFileSize() {
		return fileSize;
	}
	
	public long getTotalBytesRead() {
		return totalBytesRead;
	}
	
	public void setPort(int p) {
		port = p;
	}
	
	public String getHost() {
		return host;
	}
	
	public int getPort() {
		return port;
	}
	public void setFiles(String[] f) {
		files = f;
	}
	
	public String[] getFiles() {
		return files;
	}
	
	public void connect(String h, int p, String u, String passw) {
		host = h;
		port = p;
		user = u;
		password = passw;
		
		try {
			socket = new Socket(InetAddress.getByName(host), port);
		} catch(IOException ioe) {
			System.out.println("Unknown Host :" + host);
			ioe.printStackTrace();
            socket = null; 
            setChanged();
            notifyObservers("UnknownHost");
		}
		if(socket == null) 
            return; 
		try {
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
			oos.writeObject(user + " " + password);
			if (!((String)ois.readObject()).equals("SUCC")) {
				ois.close();
				oos.close();
				socket.close();
				setChanged();
				notifyObservers("WrongLogin");
				return;
			}
            connected = true;
            setChanged();
            notifyObservers("Connect");
            updateFiles();
		} catch(IOException ioe) { 
            System.out.println("Exception during communication. Server probably closed connection."); 
        } catch(ClassNotFoundException cnfe) {}
	}
	
    public void disconnect() {
        if (connected) {
            try {
        		oos.writeObject("QUIT");
        		oos.flush();
                ois.close(); 
                oos.close(); 
            	socket.close();
            	files = null;
            	connected = false;
            	setChanged();
            	notifyObservers("Disconnect");
            } catch(Exception e) { 
                e.printStackTrace(); 
            }   
        }
    }
    
    
    
    public void updateFiles() {
    	try {
    		oos.writeObject("LIST");
    		oos.flush();
    		files = (String[])ois.readObject();
    		setChanged();
    		notifyObservers("Tree");
    	} catch(IOException ioe) { 
            System.out.println("Exception during communication. Server probably closed connection.");
            ioe.printStackTrace();
        } catch (ClassNotFoundException cnfe){
        	cnfe.printStackTrace();
        }
    }
    
    public void download(String file, String savePath) {
    	try {
    		oos.writeObject("RETR " + file);
    		fileSize = ois.readLong();
    		File downloadFile = new File(savePath + File.separator + file);
    		FileOutputStream outputStream = new FileOutputStream(downloadFile);
            int bytesRead = -1;
            totalBytesRead = 0;
    		setChanged();
    		notifyObservers("TransferStarted");
            byte[] buffer = new byte[BUFFER_SIZE];
            while (totalBytesRead != fileSize) {
            	bytesRead = ois.read(buffer);
            	outputStream.write(buffer, 0, bytesRead);
            	totalBytesRead += bytesRead;
            	setChanged();
            	notifyObservers("Progress");
            }
    		setChanged();
    		notifyObservers("TransferFinished");
            outputStream.close();
            updateFiles();
    	} catch(IOException ioe){
    		ioe.printStackTrace();
    	} 
    }
    
    public void upload(String file) {
		FileInputStream inputStream;
        int bytesRead = -1;
        totalBytesRead = 0;
        byte[] buffer = new byte[BUFFER_SIZE];
    	String[] parts = file.split(File.separator);
    	String fileName = parts[parts.length-1];
		File fileToSend = new File(file);
		fileSize = fileToSend.length();
		try {
			inputStream= new FileInputStream(fileToSend);
		} catch(FileNotFoundException fnfe) {
			inputStream = null;
		}
    	try {
    		oos.writeObject("STOR " + fileName);
    		oos.writeLong(fileSize);
    		setChanged();
    		notifyObservers("TransferStarted");
            while ((bytesRead = inputStream.read(buffer)) != -1) {
            	oos.write(buffer, 0, bytesRead);
            	totalBytesRead += bytesRead;
            	setChanged();
            	notifyObservers("Progress");
            }
            oos.flush();
            inputStream.close();
    		setChanged();
    		notifyObservers("TransferFinished");
    		updateFiles();
    	} catch(IOException ioe) {
    		ioe.printStackTrace();
    	}
    }
}
