package voice;

import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import javax.sound.sampled.*;
import javax.swing.*;

public class Program
{
    public final static String SERVER = JOptionPane.showInputDialog("Please enter server ip");
    public static void main(String[] args) throws Exception
    {
        AudioFormat af = new AudioFormat(8000.0f,8,1,true,false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, af);
        TargetDataLine microphone = (TargetDataLine)AudioSystem.getLine(info);
        microphone.open(af);
        Socket conn = new Socket(SERVER,3000);
        microphone.start();
        DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
        ObjectOutputStream oos = new ObjectOutputStream(conn.getOutputStream()); //
        int bytesRead = 0;
        byte[] soundData = new byte[1];
        Thread inThread = new Thread(new SoundReceiver(conn));
        inThread.start();
        while(bytesRead != -1)
        {
            bytesRead = microphone.read(soundData, 0, soundData.length);
            VoiceSend vs = new VoiceSend(soundData[0],bytesRead); //

            System.out.println("Bytes read : " + vs.bytesRead + " Byte Array : " + vs.bArray);
            if(bytesRead >= 0)
            {
                //dos.write(soundData, 0, bytesRead);
                oos.writeObject(vs);
            }
        }
        System.out.println("IT IS DONE.");
    }
}
