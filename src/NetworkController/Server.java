package NetworkController;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import View.NetworkView;

public class Server implements Runnable {
    private ServerSocket serverSocket;
    private ConcurrentHashMap<Integer, ClientHandler> clients;
    private int numPlayers;
    private NetworkView view;
    private int clientID;
    private boolean timeLimitFeature;
    ServerGame serverGame;
    private boolean gameStart;
    private Lock lock;


    public Server(ServerSocket ss) {
        clients = new ConcurrentHashMap<>();
        view = new NetworkView();
        this.serverSocket = ss;
        timeLimitFeature = false;
        gameStart = false;
        lock = new ReentrantLock();
    }

    public boolean checkName(String name) {
        view.showMessage("Name checked:" + name);
        for (ClientHandler client : clients.values()) {
            if (name.equals(client.toString())) return false;
        }
        return true;
    }

    public void removeClient(ClientHandler removedClient) {
        try {
            lock.lock();
            clients.remove(removedClient.getClientId());
        } finally {
            lock.unlock();
        }
        String message = "Current clients are:";
        for (ClientHandler client : clients.values()) {
            message += " " + client;
        }
        view.showMessage(message);
    }

    public boolean checkHasTimeLimit() {
        for (ClientHandler client : clients.values()) {
            if (!client.hasTimeLimit()) return false;
        }
        return true;
    }

    private boolean checkReadyStatus() {
        for (ClientHandler client : clients.values()) {
            if (!client.isReady()) return false;
        }
        return true;
    }

    protected String getJoinedPlayersName() {
        String joinedPlayers = "";
        for (ClientHandler client : clients.values()) {
            joinedPlayers += client.toString() + ProtocolMessages.AS;
        }
        return joinedPlayers;
    }

    protected String getAcceptedFunctions() {
        String Functions = "";
        if (timeLimitFeature) Functions += ProtocolMessages.TEAM_PLAY_FLAG;
        return Functions;
    }

    protected void putClientToClientList(ClientHandler client) {
        try {
            lock.lock();
            clients.put(clientID, client);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public synchronized void run() {
        Thread gameThread = new Thread(new GameThread(this));
        gameThread.start();

        while (true) {
            try {
                view.showMessage("Waiting for connection...");
                Socket clientSocket = serverSocket.accept();
                view.showMessage("Player " + clientID + " has connected!!!");

                ClientHandler client = new ClientHandler(clientSocket, this, clientID);
                Thread clientThread = new Thread(client);
                clientThread.start();
                Thread.sleep(200);
                clientID++;
                // if enough client then start game
//                numPlayers = clients.size();
//                if (numPlayers == 4) {
//                    // broadcast welcome message
//                    timeLimitFeature = checkHasTimeLimit();
//                    wait(2000);
//                    view.showMessage("Enough players. Let's get ready!");
//                    broadcastServerReady();
//                    play();
//                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                for (ClientHandler client : clients.values()) {
                    client.sendErrorToClient(ProtocolMessages.UNRECOGNIZED);
                }
            }
        }
    }


    public class GameThread implements Runnable {

        private Server server;

        public GameThread(Server server) {
            this.server = server;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    int numPlayers = server.getClients().size();

                    if (numPlayers == 4) {
                        // broadcast welcome message
                        timeLimitFeature = checkHasTimeLimit();
//                        wait(2000);
                        view.showMessage("Enough players. Let's get ready!");
                        broadcastServerReady();
                        play();
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                    for (ClientHandler client : clients.values()) {
                        client.sendErrorToClient(ProtocolMessages.UNRECOGNIZED);
                    }
                }

            }
        }
    }

    protected ConcurrentHashMap<Integer, ClientHandler> getClients() {
        return clients;
    }

    public void play() throws IOException, InterruptedException {
        while (!checkReadyStatus()) {
            Thread.sleep(10000);
            int readyCount = 0;
            for (ClientHandler client : clients.values()) {
                if (client.isReady()) {
                    readyCount++;
                    view.showMessage("Client " + client.getClientId() + " has ready.");
                }
            }
            if (readyCount >= 2) {
                view.showMessage("Game started with: " + readyCount + " clients.");
                break;
            }
        }

        ArrayList<ClientHandler> removeClient = new ArrayList<>();
        for (ClientHandler client : clients.values()) {
            view.showMessage("Current client: " + client + client.getClientId());
            if (!client.isReady()) {
                client.sendErrorToClient(ProtocolMessages.UNRECOGNIZED);
                removeClient.add(client);
            }
        }

        for (ClientHandler client : removeClient) {
            boolean removed = clients.remove(client.getClientId(), client);
            view.showMessage("client " + client.getClientId() + "/" + client + " has been removed - " + removed);
        }

        view.showMessage("Game started with: " + clients.size() + " clients.");
        serverGame = new ServerGame(this);
        broadcastStartGame();
        gameStart = true;
        serverGame.start();

    }

    protected void broadcastAbort(ClientHandler abortedClient) {
        for (ClientHandler client : clients.values()) {
            if (client.getClientId() != abortedClient.getClientId()) {
                client.sendMessageToClient(ProtocolMessages.ABORT + ProtocolMessages.SEPARATOR + abortedClient.toString() + "\n");
                view.showMessage("message broadcast: " + abortedClient.getClientId() + " has aborted " + " to " + client.getClientId());

            }
        }
    }

    protected void broadcastWelcomeMessage(ClientHandler newlyJoined) {
        String message = ProtocolMessages.WELCOME + ProtocolMessages.SEPARATOR + newlyJoined.toString();
        if (newlyJoined.hasTimeLimit()) message += ProtocolMessages.SEPARATOR + ProtocolMessages.TURN_TIME_FLAG;
        for (ClientHandler client : clients.values()) {
            client.sendMessageToClient(message + "\n");
            view.showMessage("message broadcast: " + message + " to " + client.getClientId());
        }
    }

    protected void broadcastTiles(ClientHandler client, String tiles) {
        client.sendMessageToClient(ProtocolMessages.TILES + ProtocolMessages.SEPARATOR + tiles + "\n");
        view.showMessage("tile broadcast: " + tiles + " to " + client.getClientId());
    }

    private void broadcastServerReady() {
        String message = ProtocolMessages.SERVERREADY + ProtocolMessages.SEPARATOR;
        for (ClientHandler client : clients.values()) {
            if (client.isReady()) message += client + ProtocolMessages.AS;
        }
        for (ClientHandler client : clients.values()) {
            client.sendMessageToClient(message + "\n");
            view.showMessage("message broadcast: " + message + " to " + client.getClientId());
        }
    }

    private void broadcastStartGame() {
        for (ClientHandler client : clients.values()) {
            client.sendMessageToClient(ProtocolMessages.START + ProtocolMessages.SEPARATOR +
                    client + ProtocolMessages.AS + "\n");
            view.showMessage("message broadcast: Start game" + " to " + client.getClientId());
            String tiles = serverGame.addNewTilesToTray(client.getClientId());
            broadcastTiles(client, tiles);
        }
    }

    protected void broadcastTurn(ClientHandler currentClient) {
        for (ClientHandler client : clients.values()) {
            client.sendMessageToClient(ProtocolMessages.TURN + ProtocolMessages.SEPARATOR + currentClient.toString() + "\n");
            view.showMessage("message broadcast: turn " + " to " + client);
        }
    }

    protected void broadcastMove(String move, int score) {
        for (ClientHandler client : clients.values()) {
            client.sendMessageToClient(ProtocolMessages.MOVE + ProtocolMessages.SEPARATOR +
                    clients.get(serverGame.getCurrentPlayerID()) + ProtocolMessages.SEPARATOR +
                    move + ProtocolMessages.SEPARATOR + score + "\n");
            view.showMessage("message broadcast: moved " + " to " + client.getClientId());
        }
    }

    protected void broadcastPass() {
        for (ClientHandler client : clients.values()) {
            client.sendMessageToClient(ProtocolMessages.PASS + ProtocolMessages.SEPARATOR +
                    clients.get(serverGame.getCurrentPlayerID()) + "\n");
        }
    }

    protected void broadcastWinner() {
        ServerPlayer winner = serverGame.isWinner();
        for (ClientHandler client : clients.values()) {
            client.sendMessageToClient(ProtocolMessages.GAMEOVER + ProtocolMessages.SEPARATOR + winner.getName() + "\n");
            view.showMessage("message broadcast: winner " + " to " + client.getClientId());

        }
    }

    protected boolean getGameState() {
        return gameStart;
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server(new ServerSocket(8888));
        Thread serverThread = new Thread(server);
        serverThread.start();
    }

}
