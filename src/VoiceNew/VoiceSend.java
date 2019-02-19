package VoiceNew;

import java.io.Serializable;

public class VoiceSend implements Serializable{
    public byte[] bArray;
    public int bytesRead;
    //String st;

    public VoiceSend(byte[] bArray, int bytesRead) {
        this.bArray = bArray;
        this.bytesRead = bytesRead;
        //this.st = st;
    }
  
}
