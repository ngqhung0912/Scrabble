package NetworkController;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import Model.ServerGame;
import Model.ServerPlayer;
import View.NetworkView;

/**
 * Server is the final point for hosting the game.
 * It decides who can connect, when to start the game and who can play.
 *
 * @author Hung Nguyen
 * @version finale
 */
public class Server implements Runnable {
    private ServerSocket serverSocket;
    private ConcurrentHashMap<Integer, ClientHandler> clients;
    private NetworkView view;
    private int clientID;
    private boolean timeLimitFeature;
    volatile ServerGame serverGame;
    private boolean gameStart;
    private boolean serverReady;
    private Lock lock;

    /**
     * Constructor for the server
     * @param ss the server Socket.
     */
    public Server(ServerSocket ss) {
        clients = new ConcurrentHashMap<>();
        view = new NetworkView();
        this.serverSocket = ss;
        timeLimitFeature = false;
        gameStart = false;
        lock = new ReentrantLock();
        serverReady = false;
    }

    /**
     * check a newly joined client's name to see if it is the same as a client that has already joined.
     * @param name the newly joined client's name.
     * @return return true if the name not yet exist, false otherwise
     */
    public boolean checkName(String name) {
        view.showMessage("Name checked:" + name);
        for (ClientHandler client : clients.values()) {
            if (name.equals(client.toString())) return false;
        }
        return true;
    }

    /**
     * remove the client out of the game.
     * @param removedClient client to be removed.
     */

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

    /**
     * check if the game can start with a time limit feature or not.
     * @return true if all clients have time limit feature, false otherwise.
     */
    public boolean checkHasTimeLimit() {
        for (ClientHandler client : clients.values()) {
            if (!client.hasTimeLimit()) return false;
        }
        return true;
    }

    /**
     * check if all clients is ready or not
     * @return true if all clients are ready, false otherwise.
     */
    private boolean checkReadyStatus() {
        for (ClientHandler client : clients.values()) {
            if (!client.isReady()) return false;
        }
        return true;
    }

    /**
     * getter for all the joined player's name.
     * @return joined player's name.
     */

    public String getJoinedPlayersName() {
        String joinedPlayers = "";
        for (ClientHandler client : clients.values()) {
            joinedPlayers += client.toString() + ProtocolMessages.AS;
        }
        return joinedPlayers;
    }

    /**
     * getter for all the accepted functions in the game.
     * @return a string represent the functions.
     */
    public String getAcceptedFunctions() {
        String Functions = "";
        if (timeLimitFeature) Functions += ProtocolMessages.TEAM_PLAY_FLAG;
        return Functions;
    }

    /**
     * put the client to the client's list after validating it's name.
     * @param client to be put.
     */
    public void putClientToClientList(ClientHandler client) {
        try {
            lock.lock();
            clients.put(clientID, client);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Run the server's thread.
     */
    @Override
    public synchronized void run() {

        Thread gameThread = new Thread(new GameThread(this));
        gameThread.start();

        while (true) {
            try {
                if (serverReady || gameStart) break;
                else {
                    view.showMessage("Waiting for connection...");
                    Socket clientSocket = serverSocket.accept();
                    view.showMessage("Player " + clientID + " has connected!!!");
                    ClientHandler client = new ClientHandler(clientSocket, this, clientID);
                    Thread clientThread = new Thread(client);
                    clientThread.start();
                    Thread.sleep(200);
                    clientID++;
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                for (ClientHandler client : clients.values()) {
                    client.sendErrorToClient(ProtocolMessages.UNRECOGNIZED);
                    break;
                }
            }
        }
    }

    public class GameThread implements Runnable {
        /**
         * This class create a gameThread and simultaneously running with
         * the server thread to determine whenever the client's number have reached 4 or not
         * in order to start the game.
         */

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
                } catch ( InterruptedException e) {
                    e.printStackTrace();
                    for (ClientHandler client : clients.values()) {
                        client.sendErrorToClient(ProtocolMessages.UNRECOGNIZED);
                    }
                }

            }
        }
    }

    /**
     * getter for the hashmap represents all the clients.
     * @return clients hash map.
     */
    public ConcurrentHashMap<Integer, ClientHandler> getClients() {
        return clients;
    }

    /**
     * play method if there is enough players in the game.
     * @throws InterruptedException if there is something interrupt the thread.sleep command.
     */
    public void play() throws InterruptedException {
        serverReady = true;
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
        setServerGameForHandlers();
        serverGame.start();

    }

    /**
     * setter for serverGame for clientHandlers after creating a game.
     */
    public void setServerGameForHandlers() {
        for (ClientHandler client : clients.values()) {
            client.setServerGame(serverGame);
        }
    }

    /**
     * Broadcast to all clients that there is one client has aborted.
     * @param abortedClient the aborted client.
     */
    public void broadcastAbort(ClientHandler abortedClient) {
        for (ClientHandler client : clients.values()) {
            if (client.getClientId() != abortedClient.getClientId()) {
                client.sendMessageToClient(ProtocolMessages.ABORT + ProtocolMessages.SEPARATOR + abortedClient + "\n");
                view.showMessage("message broadcast: " + abortedClient.getClientId() + " has aborted " + " to " + client.getClientId());

            }
        }
    }

    /**
     * Broadcast to all clients that a new client has just joined.
     * @param newlyJoined the newly joined client.
     */
    public void broadcastWelcomeMessage(ClientHandler newlyJoined) {
        String message = ProtocolMessages.WELCOME + ProtocolMessages.SEPARATOR + newlyJoined.toString();
        if (newlyJoined.hasTimeLimit()) message += ProtocolMessages.SEPARATOR + ProtocolMessages.TURN_TIME_FLAG;
        for (ClientHandler client : clients.values()) {
            client.sendMessageToClient(message + "\n");
            view.showMessage("message broadcast: " + message + " to " + client.getClientId());
        }
    }

    /**
     * Broadcast to a specific client their new tiles.
     * @param client the target client.
     * @param tiles to send.
     */
    public void broadcastTiles(ClientHandler client, String tiles) {
        client.sendMessageToClient(ProtocolMessages.TILES + ProtocolMessages.SEPARATOR + tiles + "\n");
        view.showMessage("tile broadcast: " + tiles + " to " + client.getClientId());
    }

    /**
     * Broadcast to all clients that the server is ready.
     */
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

    /**
     * Broadcast to all clients that the game has started.
     */
    private void broadcastStartGame() {
        String message = "";
        for (ClientHandler client : clients.values()) {
            message += (client + ProtocolMessages.AS);
        }
            for (ClientHandler client : clients.values()) {
            client.sendMessageToClient(ProtocolMessages.START + ProtocolMessages.SEPARATOR +
                    message + "\n");
            view.showMessage("message broadcast: Start game" + " to " + client.getClientId());
            String tiles = serverGame.addNewTilesToTray(client.getClientId());
            broadcastTiles(client, tiles);
        }
    }

    /**
     * Broadcast to a specific client that it is their turn.
     * @param currentClient the targeted client.
     */
    public void broadcastTurn(ClientHandler currentClient) {
        for (ClientHandler client : clients.values()) {
            client.sendMessageToClient(ProtocolMessages.TURN + ProtocolMessages.SEPARATOR + currentClient.toString() + "\n");
            view.showMessage("message broadcast: turn of "+ currentClient + " to " + client);
        }
    }

    /**
     * Broadcast to everyone a move that a client has just made.
     * @param move the move information.
     * @param score the associated score.
     * @param currentPlayerID the player that has just made the move.
     */
    public void broadcastMove(String move, int score, int currentPlayerID) {
        for (ClientHandler client : clients.values()) {
            client.sendMessageToClient(ProtocolMessages.MOVE + ProtocolMessages.SEPARATOR +
                    clients.get(currentPlayerID) + ProtocolMessages.SEPARATOR +
                    move + ProtocolMessages.SEPARATOR + score + "\n");
            view.showMessage("message broadcast: move from  " + currentPlayerID + " " + move + " score: " + score +  " to " + client);
        }
    }

    /**
     * Broadcast to all clients that the current client has just passed the game.
     */
    public void broadcastPass() {
        for (ClientHandler client : clients.values()) {
            client.sendMessageToClient(ProtocolMessages.PASS + ProtocolMessages.SEPARATOR +
                    clients.get(serverGame.getCurrentPlayerID()) + "\n");
            view.showMessage("message broadcast: passed " + " to " + client.getClientId());

        }
    }

    /**
     * Broadcast to all clients that there is a winner.
     */
    public void broadcastWinner() {
        ServerPlayer winner = serverGame.isWinner();
        for (ClientHandler client : clients.values()) {
            client.sendMessageToClient(ProtocolMessages.GAMEOVER + ProtocolMessages.SEPARATOR + winner.getName() + "\n");
            view.showMessage("message broadcast: winner " + " to " + client.getClientId());

        }
    }

    /**
     * Broadcast to the respective client that their move was invalid.
     * @param client to broadcast.
     */
    public void broadcastInvalidMove(ClientHandler client) {
        client.sendErrorToClient(ProtocolMessages.INVALID_MOVE);
    }

    /**
     * Getter for the gameState
     * @return true if game has started, false otherwise.
     */
    public boolean getGameState() {
        return gameStart;
    }

    /**
     * getter for the view
     * @return view
     */
    public NetworkView getView() { return view; }

    public static void main(String[] args) throws IOException {
        Server server = new Server(new ServerSocket(8888));
        Thread serverThread = new Thread(server);
        serverThread.start();
    }


}
