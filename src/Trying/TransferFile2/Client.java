package Trying.TransferFile2;/*
 * Client.java
 * Client receives requested files from server.
 *
 * @author Zack Drescher
 * @author Conner Toney
 * @author Casey VanHoof
 *
 */

import java.io.*;
import java.net.*;

public class Client {

	public static void main(String[] args) throws IOException {
		String saveFile = "";
		String path = "C:\\Users\\ASUS\\Desktop"; //SPECIFY PATH//
		String ip;
		int port;

		//Determines what IP & port the user will connect to
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter IP address: ");
		ip = inFromUser.readLine();

		//Defaults to 127.0.0.1 if invalid IP is given
		if (!ip.equals("127.0.0.1")) {
			System.out.println("Invalid IP address.");
			System.out.println("IP set to 127.0.0.1.");
			ip = "127.0.0.1";
		}

		//Defaults to 9999 if invalid port number is given
		System.out.println("Enter port number (9000-10000): ");
		port = Integer.parseInt(inFromUser.readLine());
		if (port < 9000 || port > 10000) {
			System.out.println("Invalid port number.");
			System.out.println("Port number set to 9999.");
		}

		while(true){
			//Creates socket to connect with server
			Socket clientSocket = new Socket(ip, port);
			System.out.println("Connecting...");

			//Asks user for desired file and sends info to server
			byte[] bArray = new byte[1000000];
			DataInputStream is = new DataInputStream(clientSocket.getInputStream());
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			System.out.println("What file would you like?");
			String fileName = inFromUser.readLine();
			outToServer.writeBytes(fileName + '\n');

			if(fileName.equals("exit"))
				System.exit(0);
			System.out.println("Requesting...");

			saveFile = path + fileName;

			//Receives server output and saves to new file
			try {
				FileOutputStream fos = new FileOutputStream(saveFile);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				int bytesRead = is.read(bArray, 0, bArray.length);
				int current = bytesRead;

				System.out.println(bytesRead);

				do {
					System.out.println(1);
					bytesRead = is.read(bArray, current, (bArray.length - current));
					System.out.println(2);
					if (bytesRead >= 0)
						current += bytesRead;
					System.out.println(3);
				} while(bytesRead > -1);

				bos.write(bArray, 0, current);
				bos.flush();
				System.out.println(
						"File " + saveFile + " downloaded (" + current + " bytes read)");

				bos.close();
				clientSocket.close();
			} catch (ArrayIndexOutOfBoundsException aie) {
				System.out.println("Error transferring file.");
				System.exit(-1);
			}
		}
	}
}