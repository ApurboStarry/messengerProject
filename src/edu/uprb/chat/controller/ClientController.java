package edu.uprb.chat.controller;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import VoiceNew.VoiceSend;
import edu.uprb.chat.model.ChatMessage;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import voice.Practice;
import voice.SoundReceiver;
import voice.SoundReceiverold;

import javax.sound.sampled.*;

public class ClientController {

	// Java FX Implementation
	@FXML
	private Button btnLogin;
	@FXML
	private Button btnLogout;
	@FXML
	private TextArea txtAreaServerMsgs;
	@FXML
	private TextField txtHostIP;
	@FXML
	private TextField txtUsername;
	@FXML
	private ListView<String> listUser;
	@FXML
	private TextArea txtUserMsg;
	@FXML
	private Button call;
	@FXML
	private Button stopCall;
	@FXML
	private Button transferFile;
	@FXML
	private Button submit;
	@FXML
	private ChoiceBox<String> choiceBox = new ChoiceBox<>();

	@FXML
	private Button tranferFile;



	private ObservableList<String> users;


	// Server Configuration
	private boolean connected;
	private String server, username;
	private int port;
	String desiredReceiver = "All";

	// for I/O
	private ObjectInputStream sInput;		// to read from the socket
	private ObjectOutputStream sOutput;		// to write on the socket
	DataInputStream dIn;
	DataOutputStream dOut;
	private Socket socket;
	FileChooser fileChooser = new FileChooser();
	Stage stage = new Stage();
	String fileToTransfer;

	public void transfer() throws IOException {
		configureFileChooser(fileChooser);
		File file = fileChooser.showOpenDialog(stage);
		if (file != null) {
			openFile(file);
		}
		ChatMessage cm = new ChatMessage(ChatMessage.TRANSFER,desiredReceiver);
		sOutput.writeObject(cm);
 		FileInputStream fileInputStream = null;
		BufferedInputStream bufferedInputStream = null;

		try{
			//creating object to send file
			File fileT = new File (fileToTransfer);
			byte [] byteArray  = new byte [(int)fileT.length()];
			fileInputStream = new FileInputStream(fileT);
			bufferedInputStream = new BufferedInputStream(fileInputStream);
			bufferedInputStream.read(byteArray,0,byteArray.length); // copied file into byteArray

			//sending file through socket
			System.out.println("Sending " + fileToTransfer + "( size: " + byteArray.length + " bytes)");
			//dOut.write(byteArray,0,byteArray.length);	//copying byteArray to socket
			//dOut.flush();	//flushing socket
			VoiceSend vs = new VoiceSend(byteArray,byteArray.length);
			System.out.println("File sent");
			//System.out.println(inputStream.readUTF());
		}catch (Exception e){
			System.out.println(e);
		}finally {
			if (bufferedInputStream != null) bufferedInputStream.close();
		}

	}

	private static void configureFileChooser(
			final FileChooser fileChooser) {
		fileChooser.setTitle("View Pictures");
		fileChooser.setInitialDirectory(
				new File(System.getProperty("user.home"))
		);
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("All Images", "*.*"),
				new FileChooser.ExtensionFilter("JPG", "*.jpg"),
				new FileChooser.ExtensionFilter("PNG", "*.png")
		);
	}

	private void openFile(File file) {
		try {
			//desktop.open(file);
			String absPath = file.getAbsolutePath();
			fileToTransfer = absPath;
			System.out.println(fileToTransfer);
		} catch (Exception ex) {
			Logger.getLogger(Practice.class.getName()).log(
					Level.SEVERE, null, ex
			);
		}
	}

	/**
	 * Method used by btnLogin from Java FX
	 */
	public void login() {
		port = 1500;
		server = txtHostIP.getText();
		System.out.println(server);
		username = txtUsername.getText();
		// test if we can start the connection to the Server
		// if it failed nothing we can do
		if(!start())
			return;
		connected = true;
		btnLogin.setDisable(true);
		btnLogout.setDisable(false);
		txtUsername.setEditable(false);
		txtHostIP.setEditable(false);
		choiceBox.getItems().add("All");
		choiceBox.setValue("All");
		choiceBox.setDisable(false);
		System.out.println(choiceBox.getValue());
	}

	/**
	 * Method used by btnLogout from Java FX
	 */
	public void logout() {
		if (connected) {
			ChatMessage msg = new ChatMessage(ChatMessage.LOGOUT, "");
			try {
				sOutput.writeObject(msg);
				txtUserMsg.setText("");
				btnLogout.setDisable(false);
				btnLogin.setDisable(true);
				txtUsername.setEditable(true);
				txtHostIP.setEditable(true);
				choiceBox.setItems(users);
				choiceBox.setDisable(true);
				call.setDisable(true);
				stopCall.setDisable(true);
				transferFile.setDisable(true);
				submit.setDisable(true);
			}
			catch(IOException e) {
				display("Exception writing to server: " + e);
			}
		}
	}

	/**--------------------------------STARRY INCORPORATED---------------------------------- */
	public void voiceCall(){
		if(connected){
			ChatMessage msg = new ChatMessage(ChatMessage.VOICECALL, "");
			try {
				sOutput.writeObject(msg);
				//voiceCall2();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	public void voiceCall2() throws Exception{
		System.out.println("In voice call 2");
		AudioFormat af = new AudioFormat(8000.0f,8,1,true,false);
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, af);
		TargetDataLine microphone = (TargetDataLine)AudioSystem.getLine(info);
		microphone.open(af);
		microphone.start();
		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream()); //
		ObjectInputStream ois  = new ObjectInputStream(socket.getInputStream());
		int bytesRead = 0;
		//byte[] soundData = new byte[1];
		Thread inThread = new Thread(new VoiceNew.SoundReceiver(ois));
		inThread.start();
		while(bytesRead != -1)
		{
			byte[] soundData = new byte[microphone.getBufferSize()/5];
			bytesRead = microphone.read(soundData, 0, soundData.length);
			VoiceSend vs = new VoiceSend(soundData,bytesRead);
			System.out.println("Bytes read : " + vs.bytesRead + " Byte Array : " + (int)vs.bArray[0]);
			if(bytesRead >= 0)
			{
				oos.writeObject(vs);
			}
		}
		System.out.println("IT IS DONE.");

//		AudioFormat af = new AudioFormat(8000.0f,8,1,true,false);
//		DataLine.Info info = new DataLine.Info(TargetDataLine.class, af);
//		TargetDataLine microphone = (TargetDataLine)AudioSystem.getLine(info);
//		microphone.open(af);
//		microphone.start();
//		DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
//		int bytesRead = 0;
//		byte[] soundData = new byte[1];
//		Thread inThread = new Thread(new SoundReceiverold(socket));
//		inThread.start();
//		while(bytesRead != -1)
//		{
//			bytesRead = microphone.read(soundData, 0, soundData.length);
//			if(bytesRead >= 0)
//			{
//				dos.write(soundData, 0, bytesRead);
//			}
//		}
//		System.out.println("IT IS DONE.");
	}
	/**--------------------------------STARRY INCORPORATED---------------------------------- */


	/**
	 * To send a message to the server
	 */
	public void sendMessage() {
		if (connected) {
			String got = txtUserMsg.getText();
			String msgToSend = desiredReceiver + ">>" + got;
			ChatMessage msg = new ChatMessage(ChatMessage.MESSAGE, msgToSend);
			try {
				sOutput.writeObject(msg);
				System.out.println(msg.getMessage());
				System.out.println("******"+desiredReceiver);
				txtUserMsg.setText("");
			}
			catch(IOException e) {
				display("Exception writing to server: " + e);
			}
			if(!desiredReceiver.equals("All")){
				display(username + ": " + got);
			}
		}
	}

	/**
	 * Sends message to server
	 * Used by TextArea txtUserMsg to handle Enter key event
	 */
	public void handleEnterPressed(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			sendMessage();
			event.consume();
		}
	}

	public void onSubmitClicked(){
		desiredReceiver = choiceBox.getValue();
		System.out.println(desiredReceiver);
	}



	/**
	 * To start the dialog
	 */
	public boolean start() {
		// try to connect to the server
		try {
			socket = new Socket(server, port);
		} 
		// if it failed not much I can so
		catch(Exception ec) {
			display("Error connecting to server:" + ec);
			return false;
		}

		String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
		display(msg);

		/* Creating both Data Stream */
		try
		{
			sInput  = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());
			dIn = new DataInputStream(socket.getInputStream());
			dOut = new DataOutputStream(socket.getOutputStream());
		}
		catch (IOException eIO) {
			display("Exception creating new Input/output Streams: " + eIO);
			return false;
		}

		/**  CHANGED HERE */
		// creates the Thread to listen from the server
		Thread listenFromServer = new ListenFromServer();
		listenFromServer.start();
		//new ListenFromServer().start();
		// Send our username to the server this is the only message that we
		// will send as a String. All other messages will be ChatMessage objects
		try
		{
			sOutput.writeObject(username);
		}
		catch (IOException eIO) {
			display("Exception doing login : " + eIO);
			disconnect();
			return false;
		}
		// success we inform the caller that it worked
		return true;
	}

	/*
	 * To send a message to the console or the GUI
	 */
	private void display(String msg) {
		txtAreaServerMsgs.appendText(msg + "\n"); // append to the ServerChatArea
	}

	/*
	 * When something goes wrong
	 * Close the Input/Output streams and disconnect not much to do in the catch clause
	 */
	private void disconnect() {
		try { 
			if(sInput != null) sInput.close();
		}
		catch(Exception e) {} // not much else I can do
		try {
			if(sOutput != null) sOutput.close();
		}
		catch(Exception e) {} // not much else I can do
		try{
			if(socket != null) socket.close();
		}
		catch(Exception e) {} // not much else I can do

		// inform the GUI
		connectionFailed();

	}

	public void connectionFailed() {
		btnLogin.setDisable(false);
		btnLogout.setDisable(true);
		// let the user change them
		txtHostIP.setEditable(true);
		// don't react to a <CR> after the username
		connected = false;
	}

	/*
	 * a class that waits for the message from the server and append them to the JTextArea
	 * if we have a GUI or simply System.out.println() it in console mode
	 */
	class ListenFromServer extends Thread {

		public void run() {
			users =	FXCollections.observableArrayList();
			listUser.setItems(users);
			while(true) {
				try {
					Object o = sInput.readObject();
					if(o instanceof String){
						String msg = (String) o;
						String[] split = msg.split(":");
						if (split[1].equals("WHOISIN")) {
							Platform.runLater(() -> {
								users.add(split[0]);
								choiceBox.getItems().addAll(split[0]);
							});
						} else if (split[1].equals("REMOVE")) {
							Platform.runLater(() -> {
								users.remove(split[0]);
							});
						} else if(split[1].equals("STARTVOICECALL")){
							System.out.println("Preparing for voice call");
							txtAreaServerMsgs.appendText("\nYou are in a voice call\n\n");
							boolean flag = true;
							while (flag){
								voiceCall2();
							}
						} else{
							txtAreaServerMsgs.appendText(msg);
						}
					}else{

					}
					//String msg = (String) sInput.readObject();
				}
				catch(IOException e) {
					display("Server has close the connection");
					connectionFailed();
					Platform.runLater(() -> {
						listUser.setItems(null);
					});
					break;
				}
				// can't happen with a String object but need the catch anyhow
				catch(ClassNotFoundException e2) {

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
