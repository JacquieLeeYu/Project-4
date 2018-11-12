/**
 * CS18000 Project 4 - Simple Server
 *
 *
 *
 * @author Jacquie Yu, Siddarth Pillai
 *
 *
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
    private static int uniqueId = 0;
    private final List<ClientThread> clients = new ArrayList<>();
    private final int port;


    private ChatServer(int port) {
        this.port = port;
    }
    private ChatServer() {
        this(1500);
    }
    private ChatServer(int port, File badWords){
        this.port = port;

    }

    /*
     * This is what starts the ChatServer.
     * Right now it just creates the socketServer and adds a new ClientThread to a list to be handled
     */
    private void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while(true) {
                Socket socket = serverSocket.accept();
                Runnable r = new ClientThread(socket, uniqueId++);
                Thread t = new Thread(r);
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
        args = new String[1];
        String portNumber;
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
                break;
            } else {
                portNumber = "1500";
                break;
            }

        }

        args[0] = portNumber;

        ChatServer server = new ChatServer(Integer.parseInt(args[0]));
        server.start();
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



        private ClientThread(Socket socket, int id) {
            this.id = id;
            this.socket = socket;
            int writeMess = 0;
            try {
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput = new ObjectInputStream(socket.getInputStream());
                username = (String) sInput.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                writeMess = -1;
            }
        }

        /*
         * This is what the client thread actually runs.
         */
        @Override
        public void run() {
            // Read the username sent to you by client
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
            System.out.println(username + " has connected." );
            while(true) {
                try {
                    cm = (ChatMessage) sInput.readObject();
                    System.out.println(dtf.format(java.time.LocalTime.now()) + " " + username + ": " + cm.getMessage());
                    sOutput.writeObject(dtf.format(java.time.LocalTime.now()) + " " + username + ": " + cm.getMessage() + "\n");
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            // Send message back to the client
               /* try {
                    sOutput.writeObject("Pong");
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            }
        }
    }
}
