package Trying.TransferFile2;

import java.io.*;
import java.net.*;


public class Worker implements Runnable  
{
	private Socket client;

	//Constructor transfers client from server to this
	Worker(Socket client) {
		this.client = client;
	}

	public void run(){

		String file = "";
		String path = "/path/to/server/directory/"; //CHANGE PATH//

		try{
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));

			//Reads in client's desired file
			String fileName = inFromClient.readLine();

			file = path + fileName;
			System.out.println("Client wants " + fileName);

			if(fileName.equals("exit")){
				System.out.println("Client has exited.");
				client.close();
				return;
			}
		} catch(IOException e){
			System.out.println("Problem connecting with client.");
			System.exit(-1);
		}

		//Attempts to convert file to bytes and transfer to client
		try {

			File outFile = new File(file);
			byte[] byteArray = new byte[(int)outFile.length()];
			FileInputStream fis = new FileInputStream(outFile);

			BufferedInputStream bis = new BufferedInputStream(fis);
			bis.read(byteArray, 0, byteArray.length);

			DataOutputStream outToClient = 
					new DataOutputStream(client.getOutputStream());
			System.out.println(
					"Sending " + file + " (" + byteArray.length + " bytes)");
			outToClient.write(byteArray, 0, byteArray.length);
			outToClient.flush();
			System.out.println("Done.");

			bis.close();
			client.close();

		} catch(FileNotFoundException e) {
			System.out.println("Client requested a file that does not exist.");
			System.out.println("Client disconnected.");
			System.exit(-1);
		}
		catch (IOException e) {
			System.out.println("I/O problem occured.");
			System.exit(-1);
		}

	}
}