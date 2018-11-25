/**
 * CS18000 Project 4 - Simple Server
 *
 * Type of message the object is when transferred between server and client
 *
 * @author Jacquie Yu, Siddarth Pillai
 *
 * @version November 26th, 2018
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

    //messageType {(0 = normal), (1 = logout), (2 = directMessage), (3 = /list command)}
    public ChatMessage(int messageType, String message, String recipient) {
        this.message = message;
        this.messageType = messageType;
        this.recipient = recipient;
    }

    public ChatMessage(int messageType, String message) {
        this(messageType, message, null); //Using null to represent a public message
    }

    public ChatMessage(int messageType) {
        this(messageType, null, null); //Using null (might be a problem later)******************************
    }

    public String getRecipient() {
        return recipient;
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
