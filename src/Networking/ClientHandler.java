package Networking;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable{
    //public static ArrayList<ClientHandler> clientHandlers;
    private Socket socket;
    private Server server;
    private String clientName;
    private BufferedReader handlerReader;
    private BufferedWriter handlerWriter;
    private boolean gameStarted;
    //private String gameJoined;

    public ClientHandler(Socket socket, Server server, String clientName){
        try {
            this.server = server;
            this.socket = socket;
            this.clientName = clientName;

            this.handlerReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.handlerWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            //gameJoined = null;
            gameStarted = false;

        } catch (IOException e) {
            shutDown();
        }
    }



    @Override
    public void run() {
        String command;

        try {
            command = handlerReader.readLine();
            while (command != null) {
                System.out.println();

            }
        }

        while (socket.isConnected()) {
            try {
                command = handlerReader.readLine();
                broadcastMessage(command);

            } catch (IOException e) {
                shutDown(socket);
                break;
            }
        }
    }

    public void handleCommand(String command) throws IOException {
        String[] input = command.split(ProtocolMessages.)
    }
}
