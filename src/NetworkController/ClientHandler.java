package NetworkController;

import View.NetworkView;

import java.io.*;

import java.net.Socket;
import java.util.Arrays;
import java.util.List;

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



    public ClientHandler(Socket socket, Server server, int id){
        try {
            this.socket = socket;
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.server = server;
            this.clientId = id;
            this.view = new NetworkView();
            this.hasTimeLimit = false;
            this.running = true;
            this.hasChatFunction = false;
            this.isReady = false;
            this.serverGame = server.serverGame;
        } catch (IOException e) {
            shutDown();
        }
    }

    public void shutDown() {
        server.removeClient(this);
        running = false;
    }


    @Override
    public void run() {
        try {
            while (running) {
                String message = in.readLine();
                view.showMessage("message from " + this.clientId + ": " + message);
                String[] messages = message.split(ProtocolMessages.SEPARATOR);
                handleCommand(messages);
                if (serverGame.gameOver()) {
                    server.broadcastWinner();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            shutDown();
            view.showMessage(this + " has been disconnected.");
        }
    }

    public void handleCommand(String[] command) throws IOException {
        switch(command[0]) {
            case ProtocolMessages.HELLO:
                if (command.length < 2)    {
                    sendErrorToClient(ProtocolMessages.UNRECOGNIZED);
                }
                else {
                    if (!server.checkName(command[1])) sendErrorToClient(ProtocolMessages.DUPLICATE_NAME);
                    else {
                        sendMessageToClient(ProtocolMessages.HELLO + ProtocolMessages.SEPARATOR +
                                server.getJoinedPlayersName() + ProtocolMessages.SEPARATOR
                                + server.getAcceptedFunctions());
                        setName(command[1]);
                        if (command.length > 3) {
                            List<String> featureList = Arrays.asList(command);
                            for (String feature : featureList) {
                                if (feature.equals(ProtocolMessages.CHAT_FLAG)) hasChatFunction = true;
                                if (feature.equals(ProtocolMessages.TURN_TIME_FLAG)) hasTimeLimit = true;
                            }
                        } else sendErrorToClient(ProtocolMessages.UNRECOGNIZED);
                    }
                }
                break;

            case ProtocolMessages.CLIENTREADY:
                isReady = true;
                break;

            case ProtocolMessages.ABORT:
                server.broadcastAbort(this);
                if (server.getGameState()) { serverGame.getPlayerByID(clientId).setAborted(true);}
                else shutDown();
                break;

            case ProtocolMessages.MOVE:
                if (serverGame.getCurrentPlayerID() != clientId) sendErrorToClient(ProtocolMessages.OUT_OF_TURN);
                else {
                    determineTileFromMove(command);
                    serverGame.updateScore(Integer.parseInt(command[2]));
                    server.broadcastMove(command[1],Integer.parseInt(command[2]));
                    serverGame.setNextPlayer();
                    serverGame.resetPassCount();
                    server.broadcastTurn(serverGame.getCurrentPlayerID());
                }
                break;
            case ProtocolMessages.PASS:
                if (serverGame.getCurrentPlayerID() != clientId) sendErrorToClient(ProtocolMessages.OUT_OF_TURN);
                else {
                    determineTileFromMove(command);
                    serverGame.doPass(command[1]);
                    server.broadcastMove(command[1],0);
                    serverGame.setNextPlayer();
                    serverGame.incrementPassCount();
                    server.broadcastTurn(serverGame.getCurrentPlayerID());
                }
                break;

            default:
                sendErrorToClient(ProtocolMessages.UNRECOGNIZED);
                break;
        }

    }

    private void determineTileFromMove(String[] command) {
        String[] moves = command[1].split(ProtocolMessages.AS);
        String[] tileUsed = new String[moves.length];
        for (int i = 1; i < moves.length; i++) {
            tileUsed[i] = moves[i].split("")[0];
        }
        String tileSend = serverGame.sendNewTiles(tileUsed);
        server.broadcastTiles(this,tileSend);
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

    protected void sendMessageToClient(String message) {
        try {
            out.write(message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void sendErrorToClient(String error) {
        try {
            out.write(ProtocolMessages.ERROR + ProtocolMessages.SEPARATOR + error);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected int getClientId() {
        return clientId;
    }


}
