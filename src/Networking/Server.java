package Networking;

import Game.Game;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable{
    private Game game;
    private ServerSocket ss;
    private List<ClientHandler> clients;
    private int next_client_no;
    private int numPlayers;
    private BufferedReader in;
    private BufferedWriter out;
    private ServerTUI view;

    public Server(ServerSocket ss){
        next_client_no = 1;
        clients = new ArrayList<>();
        view = new ServerTUI();

    }

    @Override
    public void run() {

    }

    public void acceptConnections() {
        boolean openNewSocket = true;
        while (openNewSocket) {
            try {
                // Sets up the game
                setUp();

                while (true) {
                    System.out.println("Waiting for connections...");
                    Socket socket = ss.accept();
                    numPlayers++;
                    System.out.println("Player " + numPlayers + " has connected!");

//                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                    out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                    ClientHandler clientHandler = new ClientHandler(socket, this, name);
                    Thread clientThread = new Thread(clientHandler);
                    clientThread.start();

                    clients.add(clientHandler);
                }

            } catch (IOException e) {
                System.out.println("Connection interrupted. Please try again");

            }
        }

    }

    private void setUp() {

    }

    public void closeConnections(){
        try {
            if (ss != null) ss.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }




}
