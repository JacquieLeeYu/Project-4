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

final class ChatMessage implements Serializable {
    private static final long serialVersionUID = 6898543889087L;

    // Here is where you should implement the chat message object.
    // Variables, Constructors, Methods, etc.


    private int messageType;
    private String message;

    //messageType {(0 = normal), (1 = logout)}
    public ChatMessage(int messageType, String message) {
        this.message = message;
        this.messageType = messageType;

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

    private void broadcast(String message) {

    }

    private boolean writeMessage(String msg) {
        ChatServer s = new ChatServer();

        return true;
    }

    private void remove(int id) {

    }

    private void run() {

    }

    private void close() {

    }
}
