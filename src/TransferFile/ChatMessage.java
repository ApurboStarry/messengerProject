package TransferFile;

import java.io.Serializable;

public class ChatMessage implements Serializable {

    protected static final long serialVersionUID = 1112122200L;

        // The different types of message sent by the Client

        // WHOISIN to receive the list of the users connected
        // MESSAGE an ordinary message

        // LOGOUT to disconnect from the Server

        // SENDING_FILE to send a file

    static final int WHOISIN = 0, MESSAGE = 1, LOGOUT = 2;

    static final int SENDING_FILE = 3;  // <-----------------



    private int type;

    private String message;

    private byte[] file;                // <-----------------



        // constructor

    ChatMessage(int type, String message) {

        this.type = type;

        this.message = message;

    }



        // constructor for file transfer <--------------------

    ChatMessage(byte[] buffer) {

        file = buffer;

        type = SENDING_FILE;

    }





        // getters

    byte[] getFile() {

        return file;

    }

}

