package edu.uprb.chat.controller;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class VoiceServer extends Thread {
    public static Collection<Socket> sockets = new ArrayList<>();
    Socket connection = null;
    DataInputStream dataIn = null;
    DataOutputStream dataOut = null;


    public VoiceServer(Socket conn, ArrayList<Server.ClientThread> clientsConnected) throws Exception
    {
        connection = conn;
        dataIn = new DataInputStream(connection.getInputStream());
        dataOut = new DataOutputStream(connection.getOutputStream());
        sockets.add(connection);
        for(int i = 0; i < clientsConnected.size(); ++i) {
            Server.ClientThread ct = clientsConnected.get(i);
            sockets.add(ct.socket);
        }
    }

    @Override
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
