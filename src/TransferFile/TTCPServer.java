package TransferFile;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class TTCPServer {
    private static ArrayList<TServerClientThread> clientsConnected = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        try {
            ServerSocket serverSocket = new ServerSocket(8888);
            System.out.println("Server started");
            int counter = 0;
            while (true) {
                counter++;
                Socket serverClient = serverSocket.accept();
                System.out.println("Client no: " + counter + " connected");
                DataInputStream in = new DataInputStream(serverClient.getInputStream());
                String flagDetector = in.readUTF();
                System.out.println("-------" + flagDetector + "-------");
                boolean flag;
                if(flagDetector.equals("sending")){
                    flag = true;
                }else{
                    flag = false;
                }
                TServerClientThread sct = new TServerClientThread(serverClient, counter,flag);
                sct.start();
                clientsConnected.add(sct);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    static class TServerClientThread extends Thread {
        Socket serverClient;
        int counter;
        boolean flag;
        DataInputStream inStream;
        DataOutputStream outStream;

        public TServerClientThread(Socket serverClient, int counter,boolean flag) {
            this.serverClient = serverClient;
            this.counter = counter;
            this.flag = flag;
        }

        @Override
        public void run() {
            try {
                /*COMMENTED OUT
                System.out.println("File transferring started");
                inStream = new DataInputStream(serverClient.getInputStream());
                outStream = new DataOutputStream(serverClient.getOutputStream());
                desiredReceiver = Integer.parseInt(inStream.readUTF());

                int bytesRead=0;
                int current = 0;

                try{
                    byte [] byteArray  = new byte [6022386];
                    bytesRead = inStream.read(byteArray,0,byteArray.length);					//copying file from socket to byteArray

                    current = bytesRead;
                    do {
                        bytesRead =inStream.read(byteArray, current, (byteArray.length-current));
                        if(bytesRead >= 0) current += bytesRead;
                    } while(bytesRead > -1);

                    for (int i = 0; i < clientsConnected.size(); i++) {
                        TServerClientThread scThread = clientsConnected.get(i);
                        if (scThread.counter == desiredReceiver) {
                            scThread.outStream.write(byteArray, 0, current);                            //writing byteArray to file
                            scThread.outStream.flush();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    System.out.println("File transferred to desired client");
                }
                //COMMENTED OUT
                */

                if(flag){
                    final String fileLocation = "C:\\Users\\ASUS\\Desktop\\downloadedByServer.JPG";
                    inStream = new DataInputStream(serverClient.getInputStream());
                    outStream = new DataOutputStream(serverClient.getOutputStream());

                    int desiredReceiver = inStream.readInt();

                    //writing the file into server download part
                    System.out.println("Downloading files in server");
                    int bytesRead = 0;
                    int current = 0;
                    FileOutputStream fileOutputStream = null;
                    BufferedOutputStream bufferedOutputStream = null;

                    try{
                        byte[] byteArray = new byte[6022386];
                        fileOutputStream = new FileOutputStream(fileLocation);
                        bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                        bytesRead = inStream.read(byteArray, 0, byteArray.length);  //copying file from socket to byteArray

                        current = bytesRead;
                        do {
                            bytesRead = inStream.read(byteArray, current, (byteArray.length - current));
                            if (bytesRead >= 0) current += bytesRead;
                        } while (bytesRead > -1);
                        bufferedOutputStream.write(byteArray, 0, current);                            //writing byteArray to file
                        bufferedOutputStream.flush();  //flushing buffers
                    }catch (IOException ioE){
                        ioE.printStackTrace();
                    }finally {
                        if (fileOutputStream != null) fileOutputStream.close();
                        if (bufferedOutputStream != null) bufferedOutputStream.close();
                        inStream.close();
                    }
                    System.out.println("Server side download completed");

                    //forwarding the file to its receiver
                    System.out.println("Uploading file in desired client side");
                    FileInputStream fileInputStream = null;
                    BufferedInputStream bufferedInputStream = null;
//                    System.out.println(flag);
//                    TServerClientThread sc = clientsConnected.get(1);
//                    System.out.println(sc.counter);
//                    System.out.println(clientsConnected.size());
                    System.out.println("Desired receiver : " + desiredReceiver);

                    for (int i = 0; i < clientsConnected.size(); i++) {
                        TServerClientThread scThread = clientsConnected.get(i);
                        if (scThread.counter == desiredReceiver) {
                            scThread.outStream = new DataOutputStream(scThread.serverClient.getOutputStream());
                            scThread.inStream = new DataInputStream(scThread.serverClient.getInputStream());
//                            scThread.outStream.writeUTF("URGENT");
//                            scThread.outStream.flush();
//                            int choice = scThread.inStream.readInt();

                            try {
                                File file = new File(fileLocation);
                                byte[] byteArr = new byte[(int) file.length()];
                                fileInputStream = new FileInputStream(file);
                                bufferedInputStream = new BufferedInputStream(fileInputStream);
                                bufferedInputStream.read(byteArr, 0, byteArr.length); // copied file into byteArray

                                //sending file through socket
                                System.out.println("Sending " + fileLocation + "( size: " + byteArr.length + " bytes)");

                                scThread.outStream.write(byteArr, 0, byteArr.length);    //copying byteArray to socket
                                scThread.outStream.flush();    //flushing socket
                            } catch (Exception e) {
                                e.printStackTrace();
                            }finally {
                                if (bufferedInputStream != null) bufferedInputStream.close();
                                if (scThread.outStream != null) fileInputStream.close();
                                scThread.outStream.close();
                            }
                            System.out.println("Working!");


                        }
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}


















