package Networking;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{
    //public static ArrayList<ClientHandler> clientHandlers;
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    private Server server;
    private String clientName;

    public ClientHandler(Socket socket, Server server, String clientName){
        try {
            this.socket = socket;
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            this.server = server;
            this.clientName = clientName;
        } catch (IOException e) {
            shutDown();
        }
    }



    @Override
    public void run() {
        String command;

        try {
            command = in.readLine();
            while (command != null) {
                System.out.println();
                hand
            }
        }

        while (socket.isConnected()) {
            try {
                command = in.readLine();
                broadcastMessage(command);

            } catch (IOException e) {
                shutDown(socket);
                break;
            }
        }
    }

    public void handleCommand(String command) throws IOException {
        String[] input
    }
}
