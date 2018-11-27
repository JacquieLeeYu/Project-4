/**
 * CS18000 Project 4 - Simple Server
 *
 * Links information to all or specific clients
 *
 * @author Jacquie Yu, Siddarth Pillai
 *
 * @version November 26th, 2018
 */

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

final class ChatServer {
    private static int uniqueId = -1;
    private final List<ClientThread> clients = new ArrayList<>();
    private final int port;
    private String badWords;


    private ChatServer(int port, String badWords){
        this.port = port;
        this.badWords = badWords;
    }
    private ChatServer(int port) {
        this(port,"");
    }
    private ChatServer() {
        this(1500);
    }


    /*
     * This is what starts the ChatServer.
     * Right now it just creates the socketServer and adds a new ClientThread to a list to be handled
     */
    private void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            if(!badWords.equals("")){
                System.out.println("Banned Words File: " + badWords);
                System.out.println("Banned Words:");
                ChatFilter cf = new ChatFilter(badWords);
                cf.listWords(badWords);
            } else {
                System.out.println("No Banned Words.");
            }
            System.out.println();
            System.out.println("Server waiting for clients on port " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                uniqueId++;
                Runnable r = new ClientThread(socket, uniqueId);
                Thread t = new Thread(r);
                for (ClientThread cl : clients) {
                    if (((ClientThread) r).username.equals(cl.username)) {
                        System.out.println("Client attempted to connect with duplicate username");

                    }
                }
                clients.add((ClientThread) r);
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     *  > java ChatServer
     *  > java ChatServer portNumber
     *  If the port number is not specified 1500 is used
     */
    public static void main(String[] args) {
        args = new String[2];
        String portNumber;
        String badWord;
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String command = scanner.nextLine(); //command taken from terminal
            ArrayList<String> spaceIndex = new ArrayList<>(); //Array of indexes with spaces

            for (int i = 0 ; i < command.length() ; i++) { //check for number of spaces
                if (command.charAt(i) == ' ') {
                    spaceIndex.add(String.valueOf(i));
                }
            }

            if (spaceIndex.size() == 2) {
                portNumber = command.substring(Integer.parseInt(spaceIndex.get(1)) + 1);
                badWord = "";
                break;
            } else if (spaceIndex.size() == 3) {
                portNumber = command.substring(Integer.parseInt(spaceIndex.get(1)) + 1,
                        Integer.parseInt(spaceIndex.get(2)));
                badWord = command.substring(Integer.parseInt(spaceIndex.get(2)) + 1);
                break;
            }  else {
                portNumber = "1500";
                badWord = "";
                break;
            }
        }
        args[0] = portNumber;
        args[1] = badWord;

        ChatServer server = new ChatServer(Integer.parseInt(args[0]), args[1]);
        server.start();
//        System.out.println("Main");

    }


    /*
     * This is a private class inside of the ChatServer
     * A new thread will be created to run this every time a new client connects.
     */
    private final class ClientThread implements Runnable {
        Socket socket;
        ObjectInputStream sInput;
        ObjectOutputStream sOutput;
        int id;
        String username;
        ChatMessage cm;
        boolean dupe;

        private ClientThread(Socket socket, int id) {
            this.id = id;
            this.socket = socket;
            try {
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput = new ObjectInputStream(socket.getInputStream());
                username = (String) sInput.readObject();
                for (ClientThread cl : clients) { //Checks for a duplication in username
                    if (cl.username.equals(username)) {
                        dupe = true;
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Client could not be created");
            }
        }

        /*
         * This is what the client thread actually runs.
         */
        @Override
        public void run() {
            // Read the username sent to you by client

            if (dupe) {
                System.out.println("Removing duplicate...");
                try {
                    sOutput.writeObject("/logout");
                    close();
                    remove(this.id);
                } catch (IOException e) {
                    System.out.println("Server could not connect to client");
                }
                System.out.println("Duplicate removed");
                return;
            }

            System.out.println(username + " has connected.");
            try {
                sOutput.writeObject("Connection accepted " + port + "\n");
            } catch (IOException e) {
                System.out.println("Server is not connected to client");
            }
            int first = 0;
            while(true) {
                try {
                    cm = (ChatMessage) sInput.readObject();
                    if (cm.getMessageType() == 1) {
                        System.out.println(username + " disconnected with a LOGOUT message");
                        close();
                        remove(this.id);
                        return;
                    } else if (cm.getMessageType() == 2) {
                        directMessage(" " + username + " -> " + cm.getRecipient() +
                                        ": "+ cm.getMessage(), cm.getRecipient());
                    } else if (cm.getMessageType() == 3) {
                        list();
                    } else {
                        if (first != 0) {
                            broadcast(" " + username + ": " + cm.getMessage());
                        } else {
                            System.out.println("Server waiting for Clients on port " + port);
                        }
                    }
                    first = 1;
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        public synchronized void list() { //List command - lists out current users
            int once = 0;
            for (ClientThread ct : clients) {
                if (ct != null && !ct.username.equals(username)) {
                    if (this.socket.isConnected()) {
                        try {
                            sOutput.writeObject(ct.username + "\n");
                            once += 1;
                        } catch (IOException e) {
                            System.out.println("Server is not connected to client");
                        }

                    } else {
                        System.out.println("Error: Could not print list of users");
                    }
                }
            }
            try {
                if (once == 0) {
                    sOutput.writeObject("There are no other users connected to the server\n");
                } else {
                    sOutput.writeObject("Current total number of users: " + (once + 1) + "\n");
                }
            } catch (IOException e) {
                System.out.println("Server is not connected to client");
            }
        }

        public synchronized void directMessage(String message, String username) {
            boolean sent = false;
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
            String messageComplete = dtf.format(java.time.LocalTime.now()) + message;
            System.out.println(messageComplete);
            for (ClientThread ct : clients) {
                if (ct.username.equals(username)) {
                    if (ct.writeMessage(message + "\n")) { //This should send the message to the correct recipient
                        writeMessage(message + "\n");
                        sent = true;
                        break;
                    } else {
                        System.out.println("Server is not connected to client | " + ct.username +
                                "\nMessage: " + ct.cm.getMessage() +
                                "\nSent from: " + this.username + "\n(might not be right)");
                        sent = true;
                        break;
                    }
                }
            }
            if (!sent) {
                System.out.println("No user: " + username + "\nin database");
                try {
                    sOutput.writeObject("Could not find user: " + username + "\n");
                } catch (IOException e) {
                    System.out.println("Not connected to server");
                }
            }
        }

        private synchronized void broadcast(String message) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
            String messageComplete = dtf.format(java.time.LocalTime.now()) + message;
            ChatFilter cf = new ChatFilter(badWords);
            messageComplete = cf.filter(messageComplete);
            System.out.println(messageComplete);
            for (ClientThread ct : clients) { //using writeMessage to output to all clients
                if (ct.writeMessage(message + "\n")) {

                } else System.out.println("Server is not connected to client | " + ct.username +
                        "\nMessage: " + ct.cm.getMessage());
            }
        }

        private boolean writeMessage(String msg) {
            ChatFilter cf = new ChatFilter(badWords);
            msg = cf.filter(msg);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
            String messageComplete = dtf.format(java.time.LocalTime.now()) + msg;
            try {
                sOutput.writeObject(messageComplete);
            } catch (IOException e) {
                System.out.println("Server is not connected to client");
            }
            return this.socket.isConnected();
        }

        private synchronized void remove(int id) {
            for (int i = 0 ; i < clients.size() ; i++) {
                if (id == clients.get(i).id) {
                    clients.remove(i);
                    break;
                }
            }
//            if (clients.size() == 0) {
//                start();
//            }
        }

        private void close() {
            try {
                sInput.close();
                sOutput.close();
                socket.close();
            } catch (IOException e) {
                System.out.println("Could not close the connection with client: " + username);
            }
        }
    }
}
