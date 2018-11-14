/**
 * CS18000 Project 4 - Simple Server
 *
 *
 *
 * @author Jacquie Yu, Siddarth Pillai
 *
 *
 */


import java.io.Serializable;
import java.text.SimpleDateFormat;

final class ChatMessage implements Serializable {
    private static final long serialVersionUID = 6898543889087L;

    // Here is where you should implement the chat message object.
    // Variables, Constructors, Methods, etc.


    private int messageType;
    private String message;
    private String recipient;

    //messageType {(0 = normal), (1 = logout), (2 = directMessage)}
    public ChatMessage(int messageType, String message, String recipient) {
        this.message = message;
        this.messageType = messageType;
        this.recipient = recipient;
    }

    public ChatMessage(int messageType, String message) {
        this(messageType, message, "1"); //Using "1" to represent a public message since users can't have #
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    } //not sure if needed

    public int getMessageType() {
        return messageType;
    }

    public String getMessage() {
        return message;
    }


}
