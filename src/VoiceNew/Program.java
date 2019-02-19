package VoiceNew;

import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import javax.sound.sampled.*;

public class Program
{
    public final static String SERVER = "127.0.0.1";
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
        ObjectInputStream ois  = new ObjectInputStream(conn.getInputStream());
        int bytesRead = 0;
        Thread inThread = new Thread(new SoundReceiver(ois));
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
    }
}
