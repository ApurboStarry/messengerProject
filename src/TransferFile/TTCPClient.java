package TransferFile;

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class TTCPClient {
    static Scanner scn  = new Scanner(System.in);
    public static void main(String[] args) {
        try{
            Socket socket = new Socket("127.0.0.1",8888);
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

            int input = 0;
            while(input != 3){
                System.out.println("1.Send File");
                System.out.println("2.Receive File");
                System.out.println("3.Exit");
                System.out.println("\nenter your option : ");
                input = scn.nextInt();

                /*COMMENTED OUT.BUT IT WAS BEAUTIFUL
                if(inputStream.readUTF().equals("URGENT")){
                    Scanner scanner = new Scanner(System.in);
                    System.out.println("\tDo you want to download the file?");
                    System.out.println("\t1. Yes");
                    System.out.println("\t2. No");
                    System.out.println("Enter your option : ");
                    int choice = scanner.nextInt();
                    outputStream.writeInt(choice);

                    switch (choice){
                        case 1: TTCPClient.receive(outputStream,socket);
                            break;

                        case 2: break;
                    }
                }
                //COMMENTED OUT
                */

                switch (input){
                    case 1: outputStream.writeUTF("sending");
                            outputStream.flush();
                            System.out.println("Enter the client number to whom you want to send : ");
                            int clientNumber = scn.nextInt();
                            System.out.println("To send Client no. " + clientNumber);
                            //sending receiver name (int)
                            outputStream.writeInt(clientNumber);
                            outputStream.flush();
                            TTCPClient.send(inputStream,outputStream);
                            break;

                    case 2:
//                            if(inputStream.readUTF().equals("URGENT")) {
//                                Scanner scanner = new Scanner(System.in);
//                                System.out.println("\tDo you want to download the file?");
//                                System.out.println("\t1. Yes");
//                                System.out.println("\t2. No");
//                                System.out.println("Enter your option : ");
//                                int choice = scanner.nextInt();
//                                outputStream.writeInt(choice);
//                            }
                            TTCPClient.receive(outputStream,socket);
                            break;

                    case 3:
                        System.out.println("You ave exited successfully");

                    default:
                        System.out.println("Invalid input\nTry again");
                }
                inputStream.close();
                outputStream.close();
                socket.close();
            }
        }catch (Exception e){
            System.out.println(e);
        }

    }

    public static void send(DataInputStream inputStream,DataOutputStream outputStream) throws IOException{

        String fileLocation;
        System.out.println("Enter the file location : ");
        fileLocation = scn.next();

        FileInputStream fileInputStream = null;
        BufferedInputStream bufferedInputStream = null;

        try{
            //creating object to send file
            File file = new File (fileLocation);
            byte [] byteArray  = new byte [(int)file.length()];
            fileInputStream = new FileInputStream(file);
            bufferedInputStream = new BufferedInputStream(fileInputStream);
            bufferedInputStream.read(byteArray,0,byteArray.length); // copied file into byteArray

            //sending file through socket
            System.out.println("Sending " + fileLocation + "( size: " + byteArray.length + " bytes)");
            outputStream.write(byteArray,0,byteArray.length);	//copying byteArray to socket
            outputStream.flush();	//flushing socket
            System.out.println("File sent");
            //System.out.println(inputStream.readUTF());
        }catch (Exception e){
            System.out.println(e);
        }finally {
            if (bufferedInputStream != null) bufferedInputStream.close();
            if (outputStream != null) bufferedInputStream.close();
        }
    }

    public static void receive(DataOutputStream outputStream, Socket socket) throws IOException{
        outputStream.writeUTF("receiving");
        String fileLocation;
        System.out.println("Please enter file location with file name to save (e.g. 'D:\\abc.txt'): ");		//you can modify this program to receive file name from server and then you can skip this step
        fileLocation=scn.next();
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());

        int bytesRead=0;
        int current = 0;
        FileOutputStream fileOutputStream = null;
        BufferedOutputStream bufferedOutputStream = null;

        try{
            // receive file
            byte [] byteArray  = new byte [6022386];					//I have hard coded size of byteArray, you can send file size from socket before creating this.
            System.out.println("Please wait.Downloading file...");

            //reading file from socket
            fileOutputStream = new FileOutputStream(fileLocation);
            bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            bytesRead = inputStream.read(byteArray,0,byteArray.length);					//copying file from socket to byteArray

            current = bytesRead;
            do {
                bytesRead = inputStream.read(byteArray, current, (byteArray.length-current));
                if(bytesRead >= 0) current += bytesRead;
            } while(bytesRead > -1);
            bufferedOutputStream.write(byteArray, 0 , current);							//writing byteArray to file
            bufferedOutputStream.flush();												//flushing buffers

            System.out.println("File " + fileLocation  + " downloaded ( size: " + current + " bytes read)");
        }catch (IOException ioe){
            ioe.printStackTrace();
        }finally {
            if (fileOutputStream != null) fileOutputStream.close();
            if (bufferedOutputStream != null) bufferedOutputStream.close();
            inputStream.close();
        }
    }
}
