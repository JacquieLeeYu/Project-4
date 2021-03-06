/**
 * CS18000 Project 4 - Simple Server
 *
 * Sends messages to ChatServer
 *
 * @author Jacquie Yu, Siddharth Pillai
 *
 * @version November 26th, 2018
 */


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

final class ChatClient {
    private static boolean quitit;
    private ObjectInputStream sInput;
    private ObjectOutputStream sOutput;
    private Socket socket;

    private final String server;
    private final String username;
    private final int port;



    private ChatClient(String username, int port, String server) {
        this.server = server;
        this.port = port;
        this.username = username;
    }
    private ChatClient(String username, int port) {
        this(username,port,"localhost");
    }
    private ChatClient(String username) {
        this(username,1500);
    }
    private ChatClient() {
        this("Anonymous");
    }

    /*
     * This starts the Chat Client
     */
    private boolean start() {
        // Create a socket
        try {
            socket = new Socket(server, port);
        } catch (ConnectException e) {
            System.out.println("Connection Failed.\nThe server does not exist or is not open.");
        } catch (IOException e) {
            System.out.println("Server could not connect");
        }
        // Create your input and output streams
        try {
            sInput = new ObjectInputStream(socket.getInputStream());
            sOutput = new ObjectOutputStream(socket.getOutputStream());
        } catch (NullPointerException e) {

        }
        catch (IOException e) {

        }

        // This thread will listen from the server for incoming messages
        Runnable r = new ListenFromServer();
        Thread t = new Thread(r);
//        System.out.println("OK where is it stopping geez");
//        System.out.println(((ListenFromServer) r).quit);
//        if (((ListenFromServer) r).quit) {
//            quitit = true;
//            return false;
//        }

        t.start();


        // After starting, send the clients username to the server.
        try {
            sOutput.writeObject(username);
        } catch (NullPointerException e) {
            System.out.print("");
        } catch (IOException e) {
            System.out.print("");

        }
        return true;
    }


    /*
     * This method is used to send a ChatMessage Objects to the server
     */
    private void sendMessage(ChatMessage msg) {
        if (msg.getMessageType() == 4) {
            try { sInput.close();
                sOutput.close();
                socket.close();
            } catch (IOException e) {
                System.out.println("");
            }
        } else if (msg.getMessage() == null) {
            try {
                sOutput.writeObject(msg);
            } catch (IOException e) {
                System.out.println("Oops! Your /list didn't work");
            }
        } else if(msg.getMessage().equalsIgnoreCase("/logout")) {
            try {
                sOutput.writeObject(msg);
            } catch (IOException e) {
                System.out.println("Could not send message to the server");
            }
            try {
                sInput.close();
                sOutput.close();
                socket.close();
                System.out.println("Server has closed the connection");
            } catch (IOException e) {
                System.out.println("Server has closed the connection");
            }
        } else {
            try {
                sOutput.writeObject(msg);
            } catch (NullPointerException e) {
                System.out.print("Server has closed the connection");
            } catch (IOException e ){
                System.out.print("Server has closed the connection");
            }
        }
    }


    /*
     * To start the Client use one of the following command
     * > java ChatClient
     * > java ChatClient username
     * > java ChatClient username portNumber
     * > java ChatClient username portNumber serverAddress
     *
     * If the portNumber is not specified 1500 should be used
     * If the serverAddress is not specified "localHost" should be used
     * If the username is not specified "Anonymous" should be used
     */
    public static void main(String[] args) {
        // Get proper arguments and override defaults


        args = new String[3];
        String username;
        String portNumber;
        int testingNumber;
        String serverAddress;
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String command = scanner.nextLine().trim(); //command taken from terminal
            ArrayList<String> spaceIndex = new ArrayList<>(); //Array of indexes with spaces

            for (int i = 0 ; i < command.length() ; i++) { //check for number of spaces
                if (command.charAt(i) == ' ') {
                    spaceIndex.add(String.valueOf(i));
                }
            }

            //if wrong command
            if (spaceIndex.size() > 1) {
                if (!command.substring(0, Integer.parseInt(spaceIndex.get(1))).equals("java ChatClient") ||
                        spaceIndex.size() > 4) {
                    System.out.println("Unknown command: " + command);
                    continue;
                }
            }


            //THIS DOES NOT HANDLE IF THE USER INPUTS SOMETHING OTHER THAN FOR JAVA CHATCLIENT
            try {
                if (spaceIndex.size() == 4) { //if contains all parameters
                    username = command.substring(Integer.parseInt(spaceIndex.get(1)) + 1,
                            Integer.parseInt(spaceIndex.get(2)));
                    portNumber = command.substring(Integer.parseInt(spaceIndex.get(2)) + 1,
                            Integer.parseInt(spaceIndex.get(3)));
                    testingNumber = Integer.parseInt(portNumber);
                    serverAddress = command.substring(Integer.parseInt(spaceIndex.get(3)) + 1);
                    break;
                } else if (spaceIndex.size() == 3) { //if no serverAddress
                    username = command.substring(Integer.parseInt(spaceIndex.get(1)) + 1,
                            Integer.parseInt(spaceIndex.get(2)));
                    portNumber = command.substring(Integer.parseInt(spaceIndex.get(2)) + 1);
                    testingNumber = Integer.parseInt(portNumber);
                    serverAddress = "localhost";
                    break;
                } else if (spaceIndex.size() == 2) { //if only contains username
                    username = command.substring(Integer.parseInt(spaceIndex.get(1)) + 1);
                    portNumber = "1500";
                    serverAddress = "localhost";
                    break;
                } else { // if it contains nothing.
                    username = "Anonymous";
                    portNumber = "1500";
                    serverAddress = "localhost";
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Wrong parameter input: " + command.substring(Integer.parseInt(spaceIndex.get(2))
                                + 1, Integer.parseInt(spaceIndex.get(3))));
            }
        }
        args[0] = username;
        args[1] = portNumber;
        args[2] = serverAddress;

        // Create your client and start it
        ChatClient client = new ChatClient(args[0], Integer.parseInt(args[1]), args[2]);
        client.start();

        // Send an empty message to the server
        client.sendMessage(new ChatMessage(0,"")); //what is this? *********************************************


        if (quitit) { //if duplicate sighhhhhhhhhhhhhhhhhhhhhhh
            client.sendMessage(new ChatMessage(4));
            return;
        }

        String message = scanner.nextLine();


        while (!message.equalsIgnoreCase(" ") && !message.equalsIgnoreCase("/logout")) {
            if (message.length() != 0 && message.charAt(0) == '/') { //Scans for if message is a command
                List<String> words = Arrays.asList(message.split(" "));
                if (words.size() > 1) {
                    int index = 4 + words.get(1).length() + 2;
                    String fullMessage;
                    if (index >= message.length()) {
                        fullMessage = "";
                    } else {
                        fullMessage = message.substring(index);
                    }
                    if (words.get(0).equalsIgnoreCase("/msg")) {
                                                                                // checks if first word is
                                                                                //"/msg" and username is not the user
                        if (!words.get(1).equals(username)) {
                            client.sendMessage(new ChatMessage(2, fullMessage, words.get(1)));
                        } else {
                            System.out.println("You cannot direct message yourself!");
                        }
                    }
                } else if (words.get(0).equalsIgnoreCase("/list")) {
                    client.sendMessage(new ChatMessage(3));
                } else {
                    System.out.println("Unknown command: Try again");
                }
            } else {
                client.sendMessage(new ChatMessage(0,message));
            }
            message = scanner.nextLine();
        } if(message.equalsIgnoreCase("/logout")) {
            client.sendMessage(new ChatMessage(1,message));
        }

    }


    /*
     * This is a private class inside of the ChatClient
     * It will be responsible for listening for messages from the ChatServer.
     * ie: When other clients send messages, the server will relay it to the client.
     */
    private final class ListenFromServer implements Runnable {
        public void run() {
            try {
                while(true) {
                    String msg = (String) sInput.readObject();
                    if (msg.equals("/logout")) {
                        quitit = true;
                        System.out.println("Duplicate usernames are not allowed");
                    } else {
                        quitit = false;
                        System.out.print(msg);
                    }
                }
            } catch (NullPointerException e) {
                System.out.println("-------\nUnable to connect to server.\nPlease close the client and try again");
            }catch (IOException | ClassNotFoundException e) {
                System.out.println();
                //should we try to find a way to not have to close the client?
            }
        }
    }
}
