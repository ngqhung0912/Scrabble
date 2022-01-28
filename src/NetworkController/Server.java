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
                Socket clientSocket = serverSocket.accept();
                ClientHandler client = new ClientHandler(clientSocket,this,clientID);
                clients.add(client);
                clientID++;
                // if enough client then start game
                numPlayers = clients.size();
                if (numPlayers == 4) {
                    // broadcast welcome message
                    timeLimitFeature = checkHasTimeLimit();
                    startGame();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void startGame() throws IOException {
        loopingUntilAllClientsReady: while (true) {
            if (checkReadyStatus()) {
                // broadcast to all that server is ready.
                // maybe also sleep a bit?
                networkPlayers = new NetworkPlayer[clients.size()];
                for (int i = 0; i < numPlayers; i++) {
                    networkPlayers[i] = new NetworkPlayer(clients.get(i).toString(), i, view);
                }
                game = new Game(networkPlayers);
                loopingOverAllPlayers: while (true) {
                    if (game.gameOver()) {
                        view.printResult(game);
                        break loopingOverAllPlayers;
                    }
                    else {
                        view.update(game);

                    }
                }
            } else {
                // broadcast to all asking them to ready then sleep a bit.
            }
        }

    }

    public void requestMove(int id) {
        clients.get(id).sendMessageToClient(ProtocolMessages.MOVE);
    }

    public static void main(String[] args) {

    }
}


