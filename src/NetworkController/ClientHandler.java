package NetworkController;

import View.NetworkView;
import View.View;

import javax.crypto.KeyAgreement;
import java.io.*;

import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ClientHandler implements Runnable{
    //public static ArrayList<ClientHandler> clientHandlers;
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




    public ClientHandler(Socket socket, Server server, int id ){
        try {
            this.socket = socket;
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

    protected void setServerGame (ServerGame serverGame) {this.serverGame = serverGame;}

    public void shutDown()
    {
        server.removeClient(this);
        running = false;
        serverGame.getPlayerByID(this.getClientId()).setAborted(true);
        view.showMessage(this + " has been shutdown. ID: " + this.clientId);
    }


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


    protected boolean hasTimeLimit() {
        return hasTimeLimit;
    }

    protected boolean isReady() {return isReady;}

    private void setName(String name) {
        this.clientName = name;
    }

    public String toString() {
        return clientName;
    }

    protected void setID(int id) { this.clientId = id;}

    protected void sendMessageToClient(String message) {
        try {
            out.write(message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            view.showMessage("Cannot send message to client: " + this + "(client ID: " + clientId + ")");
        }
    }

    protected void sendErrorToClient(String error) {
        try {
            out.write(ProtocolMessages.ERROR + ProtocolMessages.SEPARATOR + error);
            out.flush();
        } catch (IOException e) {
            view.showMessage("Cannot send error to client: " + this);
            e.printStackTrace();
        }
    }

    protected int getClientId() {
        return clientId;
    }


}
