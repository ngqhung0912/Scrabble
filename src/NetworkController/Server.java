package NetworkController;

import Model.Game;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import View.NetworkView;

public class Server implements Runnable{
    private Game game;
    private ServerSocket serverSocket;
    private ArrayList<ClientHandler> clients;
    private NetworkPlayer[] networkPlayers;
    private int currentPlayer;
    private int numPlayers;
    private BufferedReader in;
    private BufferedWriter out;
    private NetworkView view;
    private int clientID;
    private boolean timeLimitFeature;


    public Server(ServerSocket ss){
        currentPlayer = 0;
        clients = new ArrayList<>();
        view = new NetworkView();
        this.serverSocket = ss;
        clientID = 0;
        timeLimitFeature = false;
    }

    public boolean checkName(String name) {
        for (ClientHandler client : clients) {
            if (name.equals(client.toString())) return false;
        }
        return true;
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    private boolean checkHasTimeLimit() {
        for (ClientHandler client : clients) {
            if (!client.hasTimeLimit()) return false;
        }
        return true;
    }

    private boolean checkReadyStatus() {
        for (ClientHandler client : clients) {
            if (!client.isReady()) return false;
        }
        return true;
    }

    protected String getJoinedPlayersName() {
        String joinedPlayers = "";
        for (ClientHandler client : clients) {
            joinedPlayers += client.toString() + " - ";
        }
        return joinedPlayers;
    }

    protected String getAcceptedFunctions() {
        String Functions = "";
        if (timeLimitFeature) Functions += ProtocolMessages.TEAM_PLAY_FLAG;
        return Functions;
    }

    @Override
    public void run() {
        while (true) {
            try {
                view.showMessage("Waiting for connection...");
                Socket clientSocket = serverSocket.accept();
                view.showMessage("Player " + clientID + " has connected!!!");
                ClientHandler client = new ClientHandler(clientSocket,this,clientID);
                Thread clientThread = new Thread(client);
                clientThread.start();
                clients.add(client);
                clientID++;
                // if enough client then start game
                numPlayers = clients.size();
                if (numPlayers == 4) {
                    // broadcast welcome message
                    timeLimitFeature = checkHasTimeLimit();
                    broadcastStartGame();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void play() throws IOException {

    }
    protected void broadcastStartGame() {
        for (ClientHandler client : clients) {
            client.sendMessageToClient(ProtocolMessages.START + ProtocolMessages.SEPARATOR +
                    client + ProtocolMessages.AS);
        }
    }

    protected void requestMove(int id) {
        clients.get(id).sendMessageToClient(ProtocolMessages.MOVE);
    }

    protected void broadcastMove() {

    }

    protected int getNextClient(int currentClientID) {
        return currentClientID < 3 ? currentClientID++ : 0;
    }


    public static void main(String[] args) throws IOException {
        Server server = new Server(new ServerSocket(8888));
        server.run();

    }
}


