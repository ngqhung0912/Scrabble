package NetworkController;

import Exceptions.ExitProgram;
import Exceptions.ServerUnavailableException;
import Model.Board;
import Model.Square;
import View.NetworkView;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Locale;

import static NetworkController.ProtocolMessages.*;

public class Client {
    private String name;
    private boolean hasFeature;

    private Socket serverSock;
    private BufferedReader in;
    private static BufferedWriter out;
    private NetworkView view;

    private ClientGame game;
    private String[] playersNames;
    private BufferedReader clientPrinter;

    public Client() {
        view = new NetworkView();
    }

    public void start() throws ExitProgram, IOException, ServerUnavailableException {
        boolean running = true;
        name = view.getString("Welcome to Scrabble!\n Please enter your name: ");

            clearConnection();
            clientSideConnection();
            handleHello(); //To be implemented: handle ERROR.INVALIDNAME


        while (true) {
            String serverCommand = readLineFromServer();
            handleServerCommand(serverCommand);

        }
    }

    public void clientSideConnection() throws ExitProgram {
        clearConnection();
        while (serverSock == null) {
            String host = "localhost";
            int port = 8888;

            try {
                InetAddress address = InetAddress.getByName(host);
                System.out.println("Attempting to connect to " + address + ":"
                        + port + "...");
                serverSock = new Socket(address, port);
                in = new BufferedReader(new InputStreamReader(serverSock.getInputStream()));
                out = new BufferedWriter(new OutputStreamWriter(serverSock.getOutputStream()));

            } catch (IOException e) {
                System.out.println("ERROR: could not create a socket on "
                        + host + " and port " + port + ".");
            }
        }
    }

    public void clearConnection() {
        serverSock = null;
        in = null;
        out = null;
    }

    public void handleUserInput() throws IOException, ServerUnavailableException {
        String prompt = "It's your turn. " + "\nInput format: If you want to put a words, " +
                "\nfor example DOG into the board," +
                "\nin the square A1, A2 and A3 , write your move as: MOVE D-A1 O-A2 G-A3" +
                "\ntype SWAP to SWAP one or more letter(s) in your tray." +
                "\nor SWAP with no argument to pass your turn." +
                "\nTo quit the game, type ABORT";

       view.showMessage(prompt);
       clientPrinter = new BufferedReader(new InputStreamReader(System.in));

       while (true) {
           String input = clientPrinter.readLine();
           if (input != null && input.contains("ABORT")) notifyClientAbort();
           else if(input != null && input.contains("MOVE")) doMove(input);
       }

    }

    public void handleServerCommand(String serverCommand) throws IOException, ServerUnavailableException {
        String[] command = serverCommand.split(ProtocolMessages.SEPARATOR);

        switch (command[0]){
            case ProtocolMessages.HELLO:
                playersNames = command[1].split("-");
                String namesList = "";
                for (int i = 0; i < playersNames.length; i++) {
                    namesList = (i+1) + ". " + playersNames[i] + "\n";
                }
                view.showMessage("Players have connected are: \n" + namesList + "\n"
                //+ playersList.length + ". " + nickname
                +"\n Supported features are: " + command[2]);
                break;

            case ProtocolMessages.WELCOME:
                playersNames = command[1].split("-");
                String names = "";
                for (int i = 0; i < playersNames.length; i++) {
                    names = (i+1) + ". " + playersNames[i] + "\n";
                }
                view.showMessage("Welcome to Scrabble"
                        + "\nPlayers list: \n" + names + "\n"
                        //+ completePlayerList.length + ". " + nickname
                        +"\n Supported features are: " + command[2]);
                break;

            case ProtocolMessages.SERVERREADY:
                notifyClientReady();
                break;

            case ProtocolMessages.START:
                view.showMessage("---START---");
                game = new ClientGame(playersNames);
                break;

            case ProtocolMessages.ABORT:
                view.showMessage("Player " + command[1] + " has left the game...");
                break;

            case ProtocolMessages.TILES:
                String[] stringTileList  = command[1].split(ProtocolMessages.AS);
                game.putTilesToTray(stringTileList);
                break;

            case ProtocolMessages.TURN:
                game.setCurrentPlayer(command[1]);

                if(name.equals(game.getCurrentPlayer().getName())) handleUserInput();
                else {view.showMessage("It's currently " + command[1] + "'s turn");}
                break;

            case ProtocolMessages.MOVE:
                game.setCurrentPlayer(command[1]);
                String[] move = command[2].split(ProtocolMessages.AS);
                game.opponentMakeMove(move, Integer.parseInt(command[3]));
                break;

            case ProtocolMessages.PASS:
                view.showMessage("Player " + command[1] + " has passed the turn");
                break;

            case ProtocolMessages.GAMEOVER:
                view.showMessage("---GAMEOVER---" + "\n********************" +
                        "\nThe winner is " + command[1].toUpperCase());
                break;

            case ProtocolMessages.ERROR:
                if (command[1].equals(DUPLICATE_NAME)) {
                    view.showMessage("Name already chosen. Please choose another name and connect again " +
                            "\n Shutting down connection...");
                    shutDown();
                }
//                    else if (command[1].equals(INVALID_MOVE)) {
//                        view.showMessage("");
//
//                    }
                else if (command[1].equals(OUT_OF_TURN)) {
                    view.showMessage("It's not your turn!");
                }
                else if (command[1].equals(UNRECOGNIZED)) {
                    view.showMessage("Unexpected error detected. Shutting down connection...");
                    shutDown();
                }
                break;

//

        }
    }


    public static synchronized void sendMessage(String msg) throws ServerUnavailableException {
        if (out != null) {
            try {
                out.write(msg);
                out.newLine();
                out.flush();
            } catch (IOException e) {
                System.out.println("Cannot read input. Please try again");
            }
        }
        else {
            throw new ServerUnavailableException("Server not detected.");
        }
    }

    public String readLineFromServer() throws ServerUnavailableException{
        if (in != null) {
            try {
                // Read and return answer from Server
                String answer = in.readLine();
                if (answer == null) {
                    throw new ServerUnavailableException("Server not detected.");
                }
                return answer;
            } catch (IOException e) {
                throw new ServerUnavailableException("Server not detected.");
            }
        } else {
            throw new ServerUnavailableException("Server not detected.");
        }
    }

//    public String readMultipleLinesFromServer()
//            throws ServerUnavailableException {
//        if (in != null) {
//            try {
//                // Read and return answer from Server
//                StringBuilder sb = new StringBuilder();
//                for (String line = in.readLine(); line != null
//                        && !line.equals(ProtocolMessages.EOT);
//                     line = in.readLine()) {
//                    sb.append(line + System.lineSeparator());
//                }
//                return sb.toString();
//            } catch (IOException e) {
//                throw new ServerUnavailableException("Could not read "
//                        + "from server.");
//            }
//        } else {
//            throw new ServerUnavailableException("Could not read "
//                    + "from server.");
//        }
//    }


    public void handleHello() {
        try {
            String answer = ProtocolMessages.HELLO + ProtocolMessages.SEPARATOR + name
                    + ProtocolMessages.SEPARATOR + ProtocolMessages.TURN_TIME_FLAG;
            sendMessage(answer);
        }
        catch (ServerUnavailableException e) {
            view.showMessage("No server found. Please try to connect again");
        }
    }

    public void notifyClientReady() throws ServerUnavailableException, IOException {
        boolean answer = view.getBoolean("The game is ready to start. Do you want to start now? " +
                "\n Enter \"Y\" to start or \"N\" to abort");
        String command = (answer == true) ?  ProtocolMessages.CLIENTREADY + ProtocolMessages.SEPARATOR + name
                : ProtocolMessages.ABORT;
        sendMessage(command);
    }

    public void notifyClientAbort() throws ServerUnavailableException {
        String message = ProtocolMessages.ABORT + ProtocolMessages.SEPARATOR + name;
        sendMessage(message);
    }

    public void doMove(String moves) throws IOException, ServerUnavailableException {
        String[] clientMoves = game.getCurrentPlayer().determineMove(moves.split(ProtocolMessages.AS));

        if (clientMoves[0].equals("SWAP")) {
            if (clientMoves.length > 1) doPassWithTiles(clientMoves[1]);
            else {sendMessage(ProtocolMessages.PASS);}
        }
        else {
            String message = (game.makeMove(clientMoves)) ?
                    ProtocolMessages.MOVE + ProtocolMessages.SEPARATOR + game.sendMoveToServer(clientMoves)
                    + ProtocolMessages.SEPARATOR + game.getCurrentPlayer().getTotalPoints()
                    : ProtocolMessages.PASS;
            sendMessage(message);
        }
    }

    public void doPassWithTiles(String swapTiles) throws ServerUnavailableException {
        String message = ProtocolMessages.PASS + ProtocolMessages.SEPARATOR + swapTiles;
        sendMessage(message);
    }


    public void shutDown(){
        view.showMessage("Closing the connection...");
        try {
            in.close();
            out.close();
            serverSock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)  {
        Client client = new Client();
        try {
            client.start();
        } catch (ExitProgram e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServerUnavailableException e) {
            e.printStackTrace();
        }
    }
}
