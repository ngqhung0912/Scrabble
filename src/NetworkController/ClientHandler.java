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
    private boolean hasLobbyFunction;
    private boolean hasChatFunction;
    private boolean hasMultiplayer;
    private boolean hasTimeLimit;
    private boolean isReady;



    public ClientHandler(Socket socket, Server server, int id){
        try {
            this.socket = socket;
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.server = server;
            this.clientId = id;
            this.view = new NetworkView();
            this.hasTimeLimit = false;
            this.hasMultiplayer = false;
            this.hasChatFunction = false;
            this.isReady = false;
        } catch (IOException e) {
            shutDown();
        }
    }

    public void shutDown() {
        server.removeClient(this);
    }






    @Override
    public void run() {
        try {
            String message = in.readLine();
            view.showMessage("message from " + this.clientId + ": " + message);
            String[] messages = message.split(ProtocolMessages.SEPARATOR);
            handleCommand(messages);

        } catch (IOException e) {
            e.printStackTrace();
            shutDown();
            view.showMessage(this + " has been disconnected.");
        }
    }

    public void handleCommand(String[] command) throws IOException {
        switch(command[0]) {
            case ProtocolMessages.HELLO:
                if (!server.checkName(command[1])) sendErrorToClient(ProtocolMessages.DUPLICATE_NAME, "Duplicate name");
                else{
                    sendMessageToClient(ProtocolMessages.HELLO + ProtocolMessages.SEPARATOR +
                            server.getJoinedPlayersName() + ProtocolMessages.SEPARATOR
                            + server.getAcceptedFunctions() );
                    setName(command[1]);
                    view.showMessage("name set " + this.clientName);
                    if (command.length > 2) {
                        List<String> featureList = Arrays.asList(command);
                        if (featureList.contains(ProtocolMessages.CHAT_FLAG)) hasChatFunction = true;

                        if (featureList.contains(ProtocolMessages.TURN_TIME_FLAG)) hasTimeLimit = true;

                        if (!featureList.containsAll(Arrays.asList(ProtocolMessages.TEAM_PLAY_FLAG, ProtocolMessages.TEAM_PLAY_FLAG, ProtocolMessages.TURN_TIME_FLAG)))
                            sendErrorToClient(ProtocolMessages.ERROR, "Invalid feature code.");
                    }
                }
                break;
            case ProtocolMessages.CLIENTREADY:
                isReady = true;
                break;
            case ProtocolMessages.ABORT:
                //shutdown;
                break;

            case ProtocolMessages.MOVE:
                // check if it's this client's turn or not. If not then send error.
                // if yes, then send to server "doMove".
                break;

            case ProtocolMessages.PASS:
                // same as above
                break;

            default:
                sendErrorToClient(ProtocolMessages.ERROR,"Invalid Command");
                break;
        }
    }

    public boolean hasTimeLimit() {
        return hasTimeLimit;
    }

    public boolean hasChatFunction() {
        return hasChatFunction;
    }

    public boolean hasMultiplayer() {
        return hasMultiplayer;
    }

    public boolean isReady() {return isReady;}

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

    protected void sendErrorToClient(String error, String message) {
        try {
            out.write(error + ProtocolMessages.SEPARATOR +message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
    protected int getClientId() {
        return clientId;
    }



}
