package edu.uprb.chat.controller;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;

import VoiceNew.VoiceSend;
import com.chat.client.Client;
import edu.uprb.chat.model.ChatMessage;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class Server {
	// a unique ID for each connection
	private static int uniqueId;
	// an ArrayList to keep the list of the Client
	static ArrayList<ClientThread> clientsConnected;
	// if I am in a GUI
	private ServerController serverController;
	// to display time
	private SimpleDateFormat sdf;
	// the port number to listen for connection
	private int port;
	// the boolean that will be turned off to stop the server
	private boolean keepGoing;


	/*
	 *  server constructor that receive the port to listen to for connection as parameter
	 */
	public Server(int port) {
		this(port, null);
	}

	public Server(int port, ServerController serverController) {
		// GUI or not
		this.serverController = serverController;
		// the port
		this.port = port;
		// to display hh:mm:ss
		sdf = new SimpleDateFormat("HH:mm:ss");
		// ArrayList for the Client list
		clientsConnected = new ArrayList<ClientThread>();
	}

	public void start() {
		keepGoing = true;
		/* create socket server and wait for connection requests */
		try 
		{
			// the socket used by the server
			ServerSocket serverSocket = new ServerSocket(port);

			// infinite loop to wait for connections
			while(keepGoing) 
			{
				// format message saying we are waiting
				display("Server waiting for Clients on port " + port + ".");

				Socket socket = serverSocket.accept();  // accept connection
				// if I was asked to stop
				if(!keepGoing)
					break;
				ClientThread t = new ClientThread(socket);  // make a thread of it
				clientsConnected.add(t); // save it in the ArrayList
				t.start();
			}
			// I was asked to stop
			try {
				serverSocket.close();
				for(int i = 0; i < clientsConnected.size(); ++i) {
					ClientThread tc = clientsConnected.get(i);
					try {
						tc.sInput.close();
						tc.sOutput.close();
						tc.socket.close();
					}
					catch(IOException ioE) {
						// not much I can do
					}
				}
			}
			catch(Exception e) {
				display("Exception closing the server and clients: " + e);
			}
		}
		// something went bad
		catch (IOException e) {
			String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
			display(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}		
	/*
	 * For the GUI to stop the server
	 */
	public void stop() {
		keepGoing = false;
		// connect to myself as Client to exit statement 
		// Socket socket = serverSocket.accept();
		try {
			new Socket("localhost", port);
		}
		catch(Exception e) {
			// nothing I can really do
		}
	}
	/*
	 * Display an event (not a message) to the GUI
	*/
	private void display(String msg) {
		String time = sdf.format(new Date()) + " " + msg;
		serverController.appendEvent(time + "\n");
	}
	/*
	 *  to broadcast a message to all Clients
	*/
	private synchronized void broadcast(String message) {
		// add HH:mm:ss and \n to the message
		String time = sdf.format(new Date());
		String messageLf;
		if (message.contains("WHOISIN") || message.contains("REMOVE") || message.contains("STARTVOICECALL")){
			messageLf = message;
		} else {
			messageLf = time + " " + message + "\n";
			serverController.appendRoom(messageLf); // append in the room window
		}

		// we loop in reverse order in case we would have to remove a Client
		// because it has disconnected
		for(int i = clientsConnected.size(); --i >= 0;) {
			ClientThread ct = clientsConnected.get(i);
			// try to write to the Client if it fails remove it from the list
			if(!ct.writeMsg(messageLf)) {
				System.out.println("ct.writeMessage : " + messageLf);
				clientsConnected.remove(i);
				serverController.remove(ct.username);
				display("Disconnected Client " + ct.username + " removed from list.");
			}
		}
	}

	// for a client who logoff using the LOGOUT message
	synchronized void remove(int id) {
		// scan the array list until we found the Id
		for(int i = 0; i < clientsConnected.size(); ++i) {
			ClientThread ct = clientsConnected.get(i);
			// found it
			if(ct.id == id) {
				serverController.remove(ct.username);
				ct.writeMsg(ct.username + ":REMOVE");
				clientsConnected.remove(i);
				return;
			}
		}
	}

	/** One instance of this thread will run for each client */
	class ClientThread extends Thread {
		// the socket where to listen/talk
		Socket socket;
		ObjectInputStream sInput;
 		ObjectOutputStream sOutput;
		// my unique id (easier for disconnection)
		int id;
		// the Username of the Client
		String username;
		// the only type of message a will receive
		ChatMessage cm;
		String date;
		boolean flag = true;

		void startVoiceCall(){
			while (true){
				System.out.println("Please");

				serverController.appendEvent("Voice call started");
				boolean flag = true;
				while (true){
					try{
						DataInputStream dataIn = new DataInputStream(socket.getInputStream());
						DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
						int bytesRead = 0;
						byte[] inBytes = new byte[1];
						while(bytesRead != -1)
						{
							try{bytesRead = dataIn.read(inBytes, 0, inBytes.length);}catch (IOException e)       {}
							if(bytesRead >= 0)
							{
								//sendToAll(inBytes, bytesRead);
								for(int i=0; i<clientsConnected.size();i++){
									ClientThread ct = clientsConnected.get(i);
									Socket temp = ct.socket;
									DataOutputStream tempOut = null;
									try
									{
										tempOut = new DataOutputStream(temp.getOutputStream());
									} catch (IOException e1)
									{
										e1.printStackTrace();
									}
									try{tempOut.write(inBytes, 0, bytesRead);}catch (IOException e){}
								}
							}
						}
					}catch (Exception e){
						e.printStackTrace();
					}
				}
			}

		}

		// Constructor
		ClientThread(Socket socket) {
			// a unique id
			id = ++uniqueId;
			this.socket = socket;
			/* Creating both Data Stream */
			System.out.println("Thread trying to create Object Input/Output Streams");
			try
			{
				// create output first
				sOutput = new ObjectOutputStream(socket.getOutputStream());
				sInput  = new ObjectInputStream(socket.getInputStream());
				// read the username
				username = (String) sInput.readObject();
				serverController.addUser(username);
				broadcast(username + ":WHOISIN"); //Broadcast user who logged in
				writeMsg(username + ":WHOISIN");
				for(ClientThread client : clientsConnected) {
					writeMsg(client.username + ":WHOISIN");
				}
				display(username + " just connected.");
			}
			catch (IOException e) {
				display("Exception creating new Input/output Streams: " + e);
				return;
			}
			// have to catch ClassNotFoundException
			// but I read a String, I am sure it will work
			catch (ClassNotFoundException e) {
			}
			date = new Date().toString() + "\n";
		}


		// what will run forever
		public void run() {
			// to loop until LOGOUT
			boolean keepGoing = true;
			while(keepGoing) {
				// read a String (which is an object)
				try {
					//Object o = sInput.readObject();
					Object o = sInput.readObject();
					if(o instanceof ChatMessage){
						ChatMessage cm = (ChatMessage) o;
						String message = cm.getMessage();
						System.out.println(message);

						// Switch on the type of message receive
						switch(cm.getType()) {

							case ChatMessage.TRANSFER:
								String desiredReceiver = message;
								System.out.println("Received file. To send: " + desiredReceiver);
								System.out.println("Downloading files in server");
								String fileLocation = "downloadedByServer.jpg";
								int bRead = 0;
								int current = 0;
								FileOutputStream fileOutputStream = null;
								BufferedOutputStream bufferedOutputStream = null;

								try{
									byte[] byteArray = new byte[6022386];
									fileOutputStream = new FileOutputStream(fileLocation);
									bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
									VoiceSend vs = (VoiceSend) sInput.readObject();
									byteArray = vs.bArray;
									bRead = vs.bytesRead;
									//bytesRead = inStream.read(byteArray, 0, byteArray.length);                    //copying file from socket to byteArray

									current = bRead;
									/*
									do {
										bytesRead = inStream.read(byteArray, current, (byteArray.length - current));
										if (bytesRead >= 0) current += bytesRead;
									} while (bytesRead > -1);
									*/
									bufferedOutputStream.write(byteArray, 0, current);                            //writing byteArray to file
									bufferedOutputStream.flush();  //flushing buffers
								}catch (IOException ioE){
									ioE.printStackTrace();
								}finally {
									if (fileOutputStream != null) fileOutputStream.close();
									if (bufferedOutputStream != null) bufferedOutputStream.close();
								}
								System.out.println("Server side download completed");
								break;

							case ChatMessage.MESSAGE:
								String[] split = message.split(">>");
								String toSend = split[0];
								if(toSend.equals("All")){
									System.out.println("split[0] : " + split[0] + " split[1]: " + split[1]);
									broadcast(username + ": " + split[1]);
									System.out.println("IN BROADCAST");
								}
								else {
									String messageLf = "";
									for(int i =0;i<clientsConnected.size();i++){
										ClientThread ct =clientsConnected.get(i);
										if(split[0].equals(ct.username)){
											String time = sdf.format(new Date());
											messageLf = time + " " +username+":" +split[1] + "\n";
											serverController.appendRoom(messageLf);
											//System.out.println(time);
											//ct.writeMsg(messageLf);
											if(!ct.writeMsg(messageLf)) {
												clientsConnected.remove(i);
												serverController.remove(ct.username);
												display("Disconnected Client " + ct.username + " removed from list.");
											}
										}
									}
								}

								//String[] split = message.split(">>");
								//broadcast(username + ": " + message);
								break;
							case ChatMessage.LOGOUT:
								display(username + " disconnected with a LOGOUT message.");
								broadcast(username + ":REMOVE");
								keepGoing = false;
								break;

							case ChatMessage.VOICECALL:
//								for(int i=0;i<Server.clientsConnected.size();i++){
//									ClientThread ct = Server.clientsConnected.get(i);
//									try {
//										System.out.println("Server Preparing for voice call service");
//										Thread echoThread = new Thread(new EchoThread(ct.socket));
//										echoThread.start();
//									} catch (Exception e) {
//										e.printStackTrace();
//									}
//									System.out.println("Okay");
//								}
//								break;



								System.out.println("Please");
								broadcast(":STARTVOICECALL" );
								serverController.appendEvent("Voice call started");

								DataInputStream dataIn = new DataInputStream(socket.getInputStream());
								DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
								int bytesRead = 0;
								byte[] inBytes = new byte[1];
								while(bytesRead != -1)
								{
									try{bytesRead = dataIn.read(inBytes, 0, inBytes.length);}catch (IOException e)       {}
									if(bytesRead >= 0)
									{
										//sendToAll(inBytes, bytesRead);
										for(int i=0; i<clientsConnected.size();i++){
											ClientThread ct = clientsConnected.get(i);
											Socket temp = ct.socket;
											DataOutputStream tempOut = null;
											try
											{
												tempOut = new DataOutputStream(temp.getOutputStream());
											} catch (IOException e1)
											{
												e1.printStackTrace();
											}
											try{tempOut.write(inBytes, 0, bytesRead);}catch (IOException e){}
										}
									}
								}
								break;



							/*
							for(int i = 0;i<clientsConnected.size();i++) {
								ClientThread ct = clientsConnected.get(i);
								Thread n = new Thread(
										new Runnable() {
											@Override
											public void run() {
												ct.startVoiceCall();
											}
										}
								);
								System.out.println("Please");
								broadcast(":STARTVOICECALL");
								serverController.appendEvent("Voice call started");
							}
							break;
							*/
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

				// the messaage part of the ChatMessage
			}
			// remove myself from the arrayList containing the list of the
			// connected Clients
			remove(id);
			close();
		}

		// try to close everything
		private void close() {
			// try to close the connection
			try {
				if(sOutput != null) sOutput.close();
			}
			catch(Exception e) {}
			try {
				if(sInput != null) sInput.close();
			}
			catch(Exception e) {}
			try {
				if(socket != null) socket.close();
			}
			catch (Exception e) {}
		}

		/*
		 * Write a String to the Client output stream
		 */
		private boolean writeMsg(String msg) {
			// if Client is still connected send the message to it
			if(!socket.isConnected()) {
				close();
				return false;
			}
			// write the message to the stream
			try {
				sOutput.writeObject(msg);
			}
			// if an error occurs, do not abort just inform the user
			catch(IOException e) {
				display("Error sending message to " + username);
				display(e.toString());
			}
			return true;
		}
	}
}

class EchoThread implements Runnable
{
	public static Collection<Socket> sockets = new ArrayList<>();
	Socket connection = null;
	DataInputStream dataIn = null;
	DataOutputStream dataOut = null;

	public EchoThread(Socket conn) throws Exception
	{
		connection = conn;
		dataIn = new DataInputStream(connection.getInputStream());
		dataOut = new DataOutputStream(connection.getOutputStream());
		sockets.add(connection);
	}

	public void run()
	{
		int bytesRead = 0;
		byte[] inBytes = new byte[1];
		while(bytesRead != -1)
		{
			try{bytesRead = dataIn.read(inBytes, 0, inBytes.length);}catch (IOException e)       {}
			if(bytesRead >= 0)
			{
				sendToAll(inBytes, bytesRead);
			}
		}
		sockets.remove(connection);
	}

	public static void sendToAll(byte[] byteArray, int q)
	{
		Iterator<Socket> sockIt = sockets.iterator();
		while(sockIt.hasNext())
		{
			Socket temp = sockIt.next();
			DataOutputStream tempOut = null;
			try
			{
				tempOut = new DataOutputStream(temp.getOutputStream());
			} catch (IOException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try{tempOut.write(byteArray, 0, q);}catch (IOException e){}
		}
	}
}