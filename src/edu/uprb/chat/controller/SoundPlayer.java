package edu.uprb.chat.controller;

import VoiceNew.VoiceSend;


import java.net.*;
import java.io.*;

import javax.sound.sampled.*;

public class SoundPlayer implements Runnable
{
    Socket connection = null;
    DataInputStream soundIn = null;
    SourceDataLine inSpeaker = null;

    ObjectInputStream ois = null;

    public SoundPlayer( ObjectInputStream ois) throws Exception
    {
        //connection = conn;
        //soundIn = new DataInputStream(connection.getInputStream());
        this.ois  = ois;//new ObjectInputStream(connection.getInputStream());
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
                System.out.println("Bytes received : " + vs.bytesRead + " Byte Array : " + (int)vs.bArray[0]);
                inSpeaker.write(vs.bArray,0,bytesRead);

            } catch (Exception e){
                System.out.println(e);

            }

            if(bytesRead >= 0 )
            {
                //inSpeaker.write(inSound, 0, bytesRead);
                //inSound = vs.bArray;
                //inSound = vs.bArray;
                //inSpeaker.write(vs.bArray,0,bytesRead);
            }
        }
    }
}
