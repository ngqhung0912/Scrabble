package NetworkController;

import Exceptions.ExitProgram;
import Exceptions.ServerUnavailableException;
import View.NetworkView;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

import static NetworkController.ProtocolMessages.*;

public class Client implements Runnable {
    private String name;
    private boolean hasFeature;

    private Socket serverSock;
    private BufferedReader in;
    private static BufferedWriter out;
    private NetworkView view;

    private ClientGame game;
    private String[] playersNames;
    private BufferedReader clientPrinter;

    public static void main(String[] args)  {
        System.out.println("Welcome to Scrabble!" + "\n Please enter your name: "
                + "(Your name shouldn't include space between letters or special symbols/characters. If your name has already existed, " +
                "please try and reconnect with a different name. ");
        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();
        while(name.isBlank() || name.isEmpty() || name.contains(AS) || name.contains(SEPARATOR)){
            System.out.println("Your name cannot include whitespace between letters. Please try again");
            name = scanner.nextLine();
        }
        Client client = new Client(name);
        Thread clientThread = new Thread(client);
        clientThread.start();


    }

    public Client(String name) {
        this.name = name;
        view = new NetworkView();
    }

    @Override

    public void run() {
        try {
            clientSideConnection();
            handleHello();
        } catch (ExitProgram e) {
            view.showMessage("Unexpected error detected. Shutting down the connection...");
        }

        while (true) {
            String serverCommand;
            try {
                serverCommand = readLineFromServer();
                handleServerCommand(serverCommand);
            } catch (ServerUnavailableException | IOException e) {
                view.showMessage("Unexpected error detected. Shutting down the connection...");
                break;
            }
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
                break;
            }
        }
    }

    public void clearConnection() {
        serverSock = null;
        in = null;
        out = null;

    }

    private void handleUserTurn() {
        String promptTurn = "It's your turn. " + "\nInput format: If you want to put a words, " +
                "for example DOG into the board," +
                "\nin the square A1, A2 and A3 , write your move as: MOVE D.A1 O.A2 G.A3" +
                "\nThe symbol \"-\" represents a blank tile, to determine a letter for the blank tile," +
                "\nchoose one of the letters below: " +
                "\nA B C D E F G H I J K L M N O P Q R S T U V W X Y Z\n" +
                "\nthen write: MOVE -D.A1 O.A2 G.A3" +
                "\nType SWAP to SWAP one or more letter(s) in your tray." +
                "or SWAP with no argument to pass your turn." +
                "\nTo quit the game, type ABORT";
        String input;
        try {
            input = view.getString(promptTurn);
            String[] clientMoves = game.getCurrentPlayer().determineMove(input.split(ProtocolMessages.AS));
            if (input.contains("MOVE")) {
                doMove(clientMoves);
            } else if (input.contains("SWAP")) {
                if (clientMoves.length > 1) doSwapWithTiles(clientMoves);
                else {
                    sendMessage(ProtocolMessages.PASS + "\n");
                }
            } else if (input.contains("ABORT")) {
                notifyClientAbort();
                shutDown();
            }
        } catch (ServerUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }

    public void handleServerCommand(String serverCommand) throws IOException, ServerUnavailableException {
        view.showMessage("Message from server: " + serverCommand);
        String[] command = serverCommand.split(ProtocolMessages.SEPARATOR);

        switch (command[0]){
            case ProtocolMessages.HELLO:
                view.showMessage("Hello! Bonjour! Xin chao!");
                break;

            case ProtocolMessages.WELCOME:
                view.showMessage( command[1] + " has just joined!.");
                break;

            case ProtocolMessages.SERVERREADY:
                notifyClientReady();
                break;

            case ProtocolMessages.START:
                view.showMessage("---START---");
                playersNames = command[1].split(AS);
                String namesList = "";
                for (int i = 0; i < playersNames.length; i++) {
                    namesList += (i+1) + ". " + playersNames[i] + "\n";
                }
                view.showMessage("Players have connected are: \n" + namesList + "\n");
                game = new ClientGame(playersNames);
                break;

            case ProtocolMessages.ABORT:
                view.showMessage("Player " + command[1] + " has left the game...");
                break;

            case ProtocolMessages.TILES:
                game.setCurrentPlayer(name);
                if (command.length > 1 ) {
                    String[] stringTileList  = command[1].split(ProtocolMessages.AS);
                    game.putTilesToTray(stringTileList);
                }
                if (game != null) view.update(game);

                break;

            case ProtocolMessages.TURN:
                game.setCurrentPlayer(command[1]);
                view.update(game);
                if(name.equals(command[1])) handleUserTurn();
                else {view.showMessage("It's currently " + command[1] + "'s turn");}
                break;

            case ProtocolMessages.MOVE:
                game.setCurrentPlayer(command[1]);
                String[] move = command[2].split(ProtocolMessages.AS);
                game.opponentMakeMove(move, Integer.parseInt(command[3]));
                view.update(game);
                break;

            case ProtocolMessages.PASS:
                if (command[1].equals(name)) {view.showMessage("You just passed your turn");}
                else {view.showMessage("Player " + command[1] + " has passed the turn");}
                break;

            case ProtocolMessages.GAMEOVER:
                view.printResult(game, command[1]);
                break;

            case ProtocolMessages.ERROR:
                if (command[1].equals(DUPLICATE_NAME)) {
                    view.showMessage("Name has already chosen. Please choose another name and connect again " +
                            "\n Shutting down connection...");
                    notifyClientAbort();
                    shutDown();
                }
                else if (command[1].equals(INVALID_MOVE)) {
                    view.showMessage("Your move is invalid and you will lost your turn.");
                }
                else if (command[1].equals(OUT_OF_TURN)) {
                    view.showMessage("Hold on! It's not your turn!");
                }
                else if (command[1].equals(UNRECOGNIZED)) {
                    view.showMessage("Unexpected error detected. Shutting down connection...");
                    shutDown();
                }
                break;
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

    public String readLineFromServer() throws ServerUnavailableException, IOException {
        if (in != null) {
            try {
                // Read and return answer from Server
                String answer= in.readLine();
                if (answer == null) {
                    throw new ServerUnavailableException("Server not detected.");
                }
                return answer;
            } catch (IOException e) {
                throw new IOException("Illegal input. Please try again");
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
                    + ProtocolMessages.SEPARATOR + ProtocolMessages.TURN_TIME_FLAG + "\n";
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
                : ProtocolMessages.ABORT+ "\n";
        sendMessage(command);
    }

    public void notifyClientAbort() throws ServerUnavailableException {
        String message = ProtocolMessages.ABORT + ProtocolMessages.SEPARATOR + name+ "\n";
        sendMessage(message);
    }

    public void doMove(String[] clientMoves) throws IOException, ServerUnavailableException {
        String[] clientInput = game.getCurrentPlayer().determineMove(clientMoves);

        String message = //(game.makeMove(clientMoves)) ?
                ProtocolMessages.MOVE + ProtocolMessages.SEPARATOR + game.sendMoveToServer(clientMoves);
        sendMessage(message);
    }

    public void doSwapWithTiles(String[] swapTiles) throws ServerUnavailableException {
        game.removeTiles(swapTiles);
        String swapTilesString = "";
        for(String swapTile: swapTiles) {
            swapTilesString += swapTile + AS;
        }
        String message = ProtocolMessages.PASS + ProtocolMessages.SEPARATOR + swapTilesString;
        sendMessage(message+ "\n");
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




}
