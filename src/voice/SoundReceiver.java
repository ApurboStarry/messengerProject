package voice;

import java.net.*;
import java.io.*;

import javax.sound.sampled.*;

public class SoundReceiver implements Runnable
{
    Socket connection = null;
    DataInputStream soundIn = null;
    SourceDataLine inSpeaker = null;

    ObjectInputStream ois = null;

    public SoundReceiver(Socket conn) throws Exception
    {
        connection = conn;
        soundIn = new DataInputStream(connection.getInputStream());
        ois  = new ObjectInputStream(connection.getInputStream());
        AudioFormat af = new AudioFormat(8000.0f,8,1,true,false);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, af);
        inSpeaker = (SourceDataLine)AudioSystem.getLine(info);
        inSpeaker.open(af);
    }

    public void run()
    {
        VoiceSend vs = null;
        int bytesRead = 0;
        byte[] inSound = new byte[1];
        inSpeaker.start();
        while(bytesRead != -1)
        {
            try{
                //bytesRead = soundIn.read(inSound, 0, inSound.length);
                vs = (VoiceSend) ois.readObject();
                bytesRead = vs.bytesRead;

            } catch (Exception e){
                e.printStackTrace();
            }
            if(bytesRead >= 0)
            {
                //inSpeaker.write(inSound, 0, bytesRead);
                inSound[0] = vs.bArray;
                inSpeaker.write(inSound,0,bytesRead);
            }
        }
    }
}
