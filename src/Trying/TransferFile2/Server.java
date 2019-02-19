package Trying.TransferFile2;/*
 * Server.java
 * Server continually awaits client connection, and spawns threads
 * for each connection in order to simultaneously transfer files.
 *
 * @author Zack Drescher
 * @author Conner Toney
 * @author Casey VanHoof
 *
 */
import java.io.*;
import java.net.*;

class Server{

	public static void main(String a[]) throws IOException {
		
		//Asks for port number to listen on, sets max connections to 10
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("What port should server listen on? (Range 9000-10000): ");
		int port = Integer.parseInt(inFromUser.readLine());
		int maxPendingConn = 10;
		
		if (port < 9000 || port > 10000) {
			System.out.println("Invalid port number.");
			System.out.println("Port number set to 9999.");
			port = 9999;
		}
			
		Socket socket;
		ServerSocket serverSocket = new ServerSocket(port, maxPendingConn);

		while (true) {
			// wait for the next client connection
			socket=serverSocket.accept();

			Worker w;
			//server.accept returns a client connection
			w = new Worker(socket);
			Thread t = new Thread(w);
			t.start();
			// And goes back immediately to accept another request
		}
	}
}