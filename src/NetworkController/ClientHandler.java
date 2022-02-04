package NetworkController;

import Model.ServerGame;
import View.NetworkView;

import java.io.*;

import java.net.Socket;
import java.util.Arrays;
import java.util.List;

/**
 * ClientHandler is the thread created by the server to handle all connection
 * by each client.
 * @author Hung Nguyen
 * @version finale
 */
public class ClientHandler implements Runnable{
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private Server server;
    private String clientName;
    private int clientId;
    private boolean gameStarted;
    private NetworkView view;
    private boolean hasChatFunction;
    private boolean running;
    private boolean hasTimeLimit;
    private boolean isReady;
    private ServerGame serverGame;


    /**
     * @param socket the socket connection.
     * @param server the master server.
     * @param id the client's id.
     */
    public ClientHandler(Socket socket, Server server, int id ){
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.server = server;
            this.view = new NetworkView();
            this.hasTimeLimit = false;
            this.running = true;
            this.hasChatFunction = false;
            this.isReady = false;
            this.clientId = id;

        } catch (IOException e) {
            shutDown();
        }
    }

    /**
     * Setter for the serverGame.
     * @param serverGame the serverGame that handles this client.
     */

    public void setServerGame (ServerGame serverGame) {this.serverGame = serverGame;}

    /**
     * shut down the connection and remove this thread from the server.
     */

    public void shutDown() {
        server.removeClient(this);
        running = false;
        if (server.getGameState()) serverGame.getPlayerByID(this.getClientId()).setAborted(true);
        view.showMessage(this + " has been shutdown. ID: " + this.clientId);
    }

    /**
     * override method for the thread. Used to consecutively read message from the client and pass it on to the
     * server or serverGame.
     */
    @Override
    public void run() {
        try {
            do {
                String message;
                if ((message = in.readLine()) != null) {
                    view.showMessage("message from " + this.clientId + ": " + message);
                    String[] messages = message.split(ProtocolMessages.SEPARATOR);
                    handleCommand(messages);
                    if (gameStarted && serverGame.gameOver() ) {
                        server.broadcastWinner();
                    }
                }
            }
            while (running);

        } catch (IOException e) {
            view.showMessage("IOException in run of: " + this);
            e.printStackTrace();
            shutDown();
        }
    }

    /**
     * handling the command sent by the client
     * @param command the commands sent by the client.
     * @throws IOException when cannot read the message.
     */
    public synchronized void handleCommand(String[] command) throws IOException {
        switch(command[0]) {
            case ProtocolMessages.HELLO:
                if (command.length < 2) {
                    view.showMessage("Sending client unrecognized since command length too short.");
                    sendErrorToClient(ProtocolMessages.UNRECOGNIZED);
                    shutDown();
                }
                else {
                    if (!server.checkName(command[1])) {
                        sendErrorToClient(ProtocolMessages.DUPLICATE_NAME);
                        shutDown();
                        view.showMessage("Duplicate name sent to: " + this);
                    }
                    else {
                        setName(command[1]);
                        server.putClientToClientList(this);
                        view.showMessage("Name verified. adding " + this + " to client list.");

                        List<String> featureList = Arrays.asList(command);
                        for (String feature : featureList) {
                            if (feature.equals(ProtocolMessages.CHAT_FLAG)) hasChatFunction = true;
                            if (feature.equals(ProtocolMessages.TURN_TIME_FLAG)) hasTimeLimit = true;
                        }

                        String message = ProtocolMessages.HELLO + ProtocolMessages.SEPARATOR +
                                server.getJoinedPlayersName() + ProtocolMessages.SEPARATOR
                                + ProtocolMessages.TURN_TIME_FLAG;
                        sendMessageToClient(message + "\n");
                        view.showMessage("message broadcast: " + message + " to " + this.clientId);
                        server.broadcastWelcomeMessage(this);
                    }
                }
                break;

            case ProtocolMessages.CLIENTREADY:
                isReady = true;
                view.showMessage(this + "(" + clientId +") has ready.");
                break;

            case ProtocolMessages.ABORT:
                server.broadcastAbort(this);
                view.showMessage(this + "(" + clientId +") has aborted.");
                if (server.getGameState()) {
                    serverGame.setAbort(this.clientId);
                    serverGame.doMove(this, ProtocolMessages.PASS, "Pass");
                }
                else shutDown();
                break;

            case ProtocolMessages.MOVE:
            case ProtocolMessages.PASS:
                if (server.getGameState()) {
                    String move;
                    if (command.length == 1) move = "Pass";
                    else move = command[1];
                    serverGame.doMove(this, command[0], move);
                } else {
                    sendErrorToClient(ProtocolMessages.OUT_OF_TURN);
                }
                break;
        }
    }

    /**
     * check the time limit feature of the client.
     * @return true if this client has a time limit feature, false otherwise.
     */
    public boolean hasTimeLimit() {
        return hasTimeLimit;
    }

    /**
     * check the ready status of the client
     * @return true if this client is ready, false otherwise.
     */
    public boolean isReady() {return isReady;}

    /**
     * setter for the client's name.
     * @param name the name to be set.
     */

    private void setName(String name) {
        this.clientName = name;
    }

    /**
     * toString override.
     * @return client's name.
     */

    public String toString() {
        return clientName;
    }

    /**
     * to send message to the client.
     * @param message to send.
     */

    public void sendMessageToClient(String message) {
        try {
            out.write(message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            view.showMessage("Cannot send message to client: " + this + "(client ID: " + clientId + ")");
        }
    }
    /**
     * send error to the client.
     * @param error the error to send
     */

    public void sendErrorToClient(String error) {
        try {
            out.write(ProtocolMessages.ERROR + ProtocolMessages.SEPARATOR + error);
            out.flush();
        } catch (IOException e) {
            view.showMessage("Cannot send error to client: " + this);
            e.printStackTrace();
        }
    }

    /**
     * getter for this client's ID
     * @return client's ID.
     */

    public int getClientId() {
        return clientId;
    }


}
