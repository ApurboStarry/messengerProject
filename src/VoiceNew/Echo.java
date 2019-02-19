package VoiceNew;

import java.net.*;
import java.io.*;
import java.util.*;

public class Echo
{
    public static void main(String[] args) throws Exception
    {
        ServerSocket serverSocket = new ServerSocket(3000);
        while (true)
        {
            Thread echoThread = new Thread(new EchoThread(serverSocket.accept()));
            echoThread.start();
        }
    }
}

class EchoThread implements Runnable
{

    public static Collection<Socket> sockets = new ArrayList<>();
    Socket connection = null;

    ObjectInputStream ois = null;
    ObjectOutputStream oos = null;

    public EchoThread(Socket conn) throws Exception
    {
        connection = conn;
        ois = new ObjectInputStream(connection.getInputStream());
        oos = new ObjectOutputStream(connection.getOutputStream());
        sockets.add(connection);
    }

    public void run()
    {
        int bytesRead = 0;
        byte[] inBytes = new byte[1];
        while (bytesRead != -1)
        {
//            try {
//                Object o = ois.readObject();
//                if(o instanceof VoiceSend){
//                    try
//                    {
//                        VoiceSend vs = (VoiceSend) ois.readObject();
//                        bytesRead = vs.bytesRead;
//                        if (vs != null)
//                        {
//                            System.out.println("In Echo: Bytes read: " + vs.bytesRead + " byte array: " + (int) vs.bArray[0]);
//                            oos.writeObject(vs);
//                        }
//                    } catch (Exception e)
//                    {
//                        System.out.println(e);
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            }
            try
            {
                VoiceSend vs = (VoiceSend) ois.readObject();
                bytesRead = vs.bytesRead;
                if (vs != null)
                {
                    System.out.println("In Echo: Bytes read: " + vs.bytesRead + " byte array: " + (int) vs.bArray[0]);
                    oos.writeObject(vs);
                }
            } catch (Exception e)
            {
                System.out.println(e);
            }
        }
        sockets.remove(connection);
    }
}
