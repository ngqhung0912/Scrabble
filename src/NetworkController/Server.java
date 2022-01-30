package NetworkController;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import View.NetworkView;

public class Server implements Runnable{
    private ServerSocket serverSocket;
    private ArrayList<ClientHandler> clients;
    private int numPlayers;
    private NetworkView view;
    private int clientID;
    private boolean timeLimitFeature;
    ServerGame serverGame;
    private boolean gameStart;


    public Server(ServerSocket ss){
        clients = new ArrayList<>();
        view = new NetworkView();
        this.serverSocket = ss;
        clientID = 0;
        timeLimitFeature = false;
        serverGame = new ServerGame(this);
        gameStart = false;
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

                broadcastWelcomeMessage(client);
                clients.add(client);
                clientID++;

                // if enough client then start game
                numPlayers = clients.size();
                if (numPlayers == 4) {
                    // broadcast welcome message
                    timeLimitFeature = checkHasTimeLimit();
                    view.showMessage("Enough players. Let's get ready!");
                    broadcastServerReady();
                    play();
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                for (ClientHandler client : clients) {
                    client.sendErrorToClient(ProtocolMessages.UNRECOGNIZED);
                }
            }
        }
    }

    protected ArrayList<ClientHandler> getClients() { return clients;}

    public void play() throws IOException, InterruptedException {
        int readyCount = 0;
        while (!checkReadyStatus()) {
            for (ClientHandler client : clients) {
                if (client.isReady()) readyCount++; view.showMessage("Client " + client.getClientId() + " has ready.");
            }
            if (readyCount >= 2) {
                Thread.sleep(10000);
                view.showMessage("Game started with: " + readyCount + " clients.");
                break;
            }
        }
        for (ClientHandler client : clients) {
            if (!client.isReady()) {
                client.sendErrorToClient(ProtocolMessages.UNRECOGNIZED);
                clients.remove(client);
                view.showMessage("client " + client.getClientId() + " has been removed.");
            }
        }
        view.showMessage("Game started with: " + clients.size() + " clients.");
        broadcastStartGame();
        gameStart = true;
    }

    protected void broadcastAbort(ClientHandler abortedClient){
        for (ClientHandler client : clients) {
            if (client.getClientId() != abortedClient.getClientId()) {
                client.sendMessageToClient(ProtocolMessages.ABORT + ProtocolMessages.SEPARATOR + abortedClient.toString());
                view.showMessage("message broadcast: "+ abortedClient.getClientId() + " has aborted "  + " to " + client.getClientId() );

            }
        }
    }

    private void broadcastWelcomeMessage(ClientHandler newlyJoined) {
        String message = ProtocolMessages.WELCOME + ProtocolMessages.SEPARATOR + newlyJoined.toString();
        if (newlyJoined.hasTimeLimit()) message += ProtocolMessages.SEPARATOR + ProtocolMessages.TURN_TIME_FLAG;
        for (ClientHandler client : clients) {
            client.sendMessageToClient(message);
            view.showMessage("message broadcast: " + message + " to " + client.getClientId() );
        }
    }

    protected void broadcastTiles(ClientHandler client, String tiles) {
        client.sendMessageToClient(ProtocolMessages.TILES + ProtocolMessages.SEPARATOR + tiles);
        view.showMessage("tile broadcast: " + tiles + " to " + client.getClientId());

    }

    private void broadcastServerReady() {
        String message = ProtocolMessages.SERVERREADY + ProtocolMessages.SEPARATOR;
        for (ClientHandler client : clients) {
            if (client.isReady()) message += client.toString() + ProtocolMessages.AS;
        }
        for (ClientHandler client : clients) {
            client.sendMessageToClient(message);
            view.showMessage("message broadcast: " + message + " to " + client.getClientId() );

        }
    }

    private void broadcastStartGame() {
        for (ClientHandler client : clients) {
            client.sendMessageToClient(ProtocolMessages.START + ProtocolMessages.SEPARATOR +
                    client + ProtocolMessages.AS);
            view.showMessage("message broadcast: Startgame" + " to " + client.getClientId() );

            String tiles = serverGame.sendNewTiles(new String[7]);
            broadcastTiles(client, tiles);
        }
    }

    protected void broadcastTurn(int currentPlayer) {
        for (ClientHandler client : clients) {
            client.sendMessageToClient(ProtocolMessages.TURN + ProtocolMessages.SEPARATOR + clients.get(currentPlayer));
            view.showMessage("message broadcast: turn " + " to " + client.getClientId() );

        }
    }

    protected void broadcastMove(String move, int score) {
        for (ClientHandler client: clients) {
            if (serverGame.getCurrentPlayer().isAborted()) {
                client.sendMessageToClient(ProtocolMessages.PASS + ProtocolMessages.SEPARATOR +
                        clients.get(serverGame.getCurrentPlayerID()));
                view.showMessage("message broadcast: moved "  + " to " + client.getClientId() + "(aborted)" );

            }
            else if (client.getClientId() != serverGame.getCurrentPlayerID())
                client.sendMessageToClient(ProtocolMessages.MOVE + ProtocolMessages.SEPARATOR +
                        clients.get(serverGame.getCurrentPlayerID()) + ProtocolMessages.SEPARATOR +
                        move + ProtocolMessages.SEPARATOR + score);
            view.showMessage("message broadcast: moved "  + " to " + client.getClientId());
        }
    }

    protected void broadcastWinner () {
        ServerPlayer winner = serverGame.isWinner();
        for (ClientHandler client : clients) {
            client.sendMessageToClient(ProtocolMessages.GAMEOVER + ProtocolMessages.SEPARATOR + winner.getName());
            view.showMessage("message broadcast: winner "  + " to " + client.getClientId()  );

        }
    }

    protected boolean getGameState() { return gameStart;}

    public static void main(String[] args) throws IOException {
        Server server = new Server(new ServerSocket(8888));
        Thread serverThread = new Thread(server);
        serverThread.start();
    }

}


