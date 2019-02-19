package sample;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class soundTargetDataLine {
    public static void main(String[] args) {
        System.out.println("Starting sound test...");

        try{
            AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,44100,16,2,4,44100,false);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class,format);
            if(!AudioSystem.isLineSupported(info))
                System.err.println("Line not supported");

            final TargetDataLine targetLine = (TargetDataLine)AudioSystem.getLine(info);
            targetLine.open();
            System.out.println("Starting recording");
            targetLine.start();

            Thread thread = new Thread(){
                @Override
                public void run(){
                    AudioInputStream audioStraem = new AudioInputStream(targetLine);
                    File audioFile = new File("recorded.wav");
                    try {
                        AudioSystem.write(audioStraem,AudioFileFormat.Type.WAVE,audioFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Stopped recording");
                }
            };
            thread.start();
            Thread.sleep(5000);

            targetLine.stop();
            targetLine.close();
            System.out.println("Ended sound test");
        }catch (LineUnavailableException e) {
            e.printStackTrace();
        }catch (InterruptedException ie){
            ie.printStackTrace();
        }
    }
}
