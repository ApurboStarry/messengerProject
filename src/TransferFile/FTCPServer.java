package TransferFile;

import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class FTCPServer {
    private static ArrayList<ServerClientThread> clientsConnected = new ArrayList<>();
    public static void main(String[] args) throws Exception {
        try{
            ServerSocket serverSocket = new ServerSocket(8000);
            System.out.println("Server started");
            int counter = 0;
            while(true){
                counter++;
                Socket serverClient = serverSocket.accept();
                System.out.println("Client no: " + counter + " connected");
                ServerClientThread sct = new ServerClientThread(serverClient,counter);
                sct.start();
                clientsConnected.add(sct);
            }
        }catch (Exception e){
            System.out.println(e);
        }
    }

    static class ServerClientThread extends Thread{
        Socket serverClient;
        int counter;
        DataInputStream inStream;
        DataOutputStream outStream;

        public ServerClientThread(Socket serverClient,int counter){
            this.serverClient = serverClient;
            this.counter = counter;
        }

        @Override
        public void run(){
            try{
                inStream = new DataInputStream(serverClient.getInputStream());
                outStream = new DataOutputStream(serverClient.getOutputStream());
                String clientMessage = "";
                int desiredReceiver = 0;
                while(!clientMessage.equals("bye")){
                    clientMessage = inStream.readUTF();
                    String [] split = clientMessage.split(">>");
                    desiredReceiver = Integer.parseInt(split[1]);
                    for(int i = 0;i<clientsConnected.size();i++){
                        ServerClientThread scThread = clientsConnected.get(i);
                        if(scThread.counter == desiredReceiver){
                            scThread.outStream.writeUTF(split[0]);
                            scThread.outStream.flush();
                        }
                    }
                }
                inStream.close();
                outStream.close();
                serverClient.close();
            }catch (Exception e){
                System.out.println(e);
            }finally {
                System.out.println("Client " + counter + " exited");
            }
        }
    }
}










