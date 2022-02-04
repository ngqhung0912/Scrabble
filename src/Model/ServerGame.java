package Model;

import NetworkController.ClientHandler;
import NetworkController.ProtocolMessages;
import NetworkController.Server;

import java.util.*;
/**
 * This class implements the basic game on the Server functions of Scrabble.
 * @author Hung Nguyen
 * @version finale
 */

public class ServerGame extends Game {

    private Server server;
    private ServerPlayer[] serverPlayers;
    private int turnScore;
    private volatile String moveType;
    private volatile String move;

    /**
     * Creates a new game
     *
     * @param server The game server
     */
    public ServerGame(Server server) {
        super();
        this.server = server;
        numPlayer = server.getClients().size();
        serverPlayers = new ServerPlayer[numPlayer];
        turnScore = 0;
        int i = 0;
        for (ClientHandler client : server.getClients().values()) {
            serverPlayers[i] = new ServerPlayer(client);
            i++;
        }
    }

    /**
     * add new tiles to the indexed player's tray.
     * @param currentPlayerID index to the player.
     * @return the string represents the character of the tiles added.
     */
    public String addNewTilesToTray(int currentPlayerID) {
        ServerPlayer currentPlayer = getPlayerByID(currentPlayerID);
        ArrayList<Tile> tray = currentPlayer.getTray();
        int bagSize = tileBag.size();

        int missingTiles = bagSize == 0 ? 0 : Math.min(bagSize, (7 - tray.size()));

        String tileSend = "";

        for (int i = 0; i < missingTiles; i++) {
            bagSize = tileBag.size();
            int j = new Random().nextInt(bagSize);
            Tile tile = tileBag.get(j);
            tileBag.remove(tile);
            tileSend += tile.getLetter() + ProtocolMessages.AS;
            tray.add(tile);
        }

        return tileSend;
    }

    /**
     * getter for the current turn's score.
     * @return current turn's score.
     */
    protected int getTurnScore() { return this.turnScore;}

    /**
     * Call after each turn to reset the turn score.
     */
    protected void resetTurnScore() {this.turnScore = 0;}

    /**
     * Handle the move that the ClientHandler sent.
     * @param client the client that sent the move.
     * @param moveType type of move, as described in the protocol.
     * @param move the move content.
     */
    public void doMove(ClientHandler client, String moveType, String move) {
        if (getCurrentPlayerID() != client.getClientId()) client.sendErrorToClient(ProtocolMessages.OUT_OF_TURN);
        else {
            this.moveType = moveType;
            this.move = move;
            synchronized (this) {
                notifyAll();
            }
        }
    }

    /**
     * Set the state of a Player to abort.
     * @param id of the player.
     */
    public void setAbort(int id) {
        getPlayerByID(id).setAborted(true);

    }

    /**
     * Start the game.
     */
    public void start() {
        while (!gameOver()) {
            server.getView().update(this);
            ServerPlayer currentPlayer = getCurrentPlayer();
            ClientHandler currentClient = currentPlayer.getClient();
            if (currentPlayer.isAborted()) {
                server.broadcastPass();
                incrementPassCount();
            } else {
                server.broadcastTurn(currentClient);
                synchronized (this) {
                    while (moveType == null) {
                        try { wait(); }
                        catch (InterruptedException e) {
                            System.out.println("Wait interrupted.");
                            continue;
                        }
                    }
                }
                String[] moves = move.split(ProtocolMessages.AS);
                switch (moveType) {
                    case ProtocolMessages.MOVE:
                        System.out.println("MOVE. " + moveType + move);
                        boolean validMove = makeMove(moves);
                        if (validMove) {
                            System.out.println("Move validated.");
                            int turnScore = getTurnScore();
                            resetTurnScore();
                            resetPassCount();
                            server.broadcastMove(move, turnScore, getCurrentPlayerID());
                        }
                        else {
                            server.broadcastInvalidMove(currentClient);
                            server.broadcastPass();
                            incrementPassCount();
                        }
                        break;
                    case ProtocolMessages.PASS:
                        System.out.println("PASS. " + moveType + move);
                        if (!move.equals("Pass")) {
                            swapTray(moves);
                        }
                        server.broadcastPass();
                        incrementPassCount();
                        break;

                    default:
                        System.out.println("DEFAULT. " + moveType + move);
                        break;
                }
            }
            move = null;
            moveType = null;
            server.broadcastTiles(currentClient, addNewTilesToTray(currentClient.getClientId()));
            setNextPlayer();

            // then clientHandler handle moves
            // clientHandler then broadcast the move to all other clients.
            // then clientHandler call server to send new tiles to current player.
            // then next turn.
        }
        // print result
    }

    public ServerPlayer isWinner() {
        Map<ServerPlayer, Integer> finalDeduct = new HashMap<>();

        ArrayList<Tile> tilesLeft = null;

        //Create a map of players with their deduct points
        for (ServerPlayer serverPlayer : serverPlayers) {
            tilesLeft = serverPlayer.getTray();
            int deductPoints = 0;
            for (Tile tile : tilesLeft) {
                deductPoints += tile.getPoint();
            }
            finalDeduct.put(serverPlayer, deductPoints);
        }


        for (int i = 0; i < serverPlayers.length; i++) {
            int finalPoints = 0;
            if (tilesLeft.size() == 0) {
                int totalDeductPoints = finalDeduct.get(serverPlayers[0]) + finalDeduct.get(serverPlayers[1])
                        + finalDeduct.get(serverPlayers[2]) + finalDeduct.get(serverPlayers[3]);
                finalPoints = serverPlayers[i].getTotalPoints() + totalDeductPoints;
            } else {
                finalPoints = serverPlayers[i].getTotalPoints() - finalDeduct.get(serverPlayers[i]);
            }
            serverPlayers[i].setFinalPoints(finalPoints);
        }

        ServerPlayer winner = serverPlayers[0];
        for (int i = 1; i < serverPlayers.length; ) {
            int compare = winner.compareTo(serverPlayers[i]);
            if (compare < 0) {
                winner = serverPlayers[i];
            } else if (compare == 0) {
                if (winner.getTotalPoints() + finalDeduct.get(winner) < serverPlayers[i].getTotalPoints() + finalDeduct.get(serverPlayers[i])) {
                    winner = serverPlayers[i];
                } else if (winner.getTotalPoints() + finalDeduct.get(winner) == serverPlayers[i].getTotalPoints() + finalDeduct.get(serverPlayers[i])) {
                    return null;
                }
            }
            i++;
        }
        return winner;
    }

    /**
     * Determine the coordinate in the form of H7 from square index.
     * @param location index of the square.
     * @return the String representation of the coordinate.
     */
    private String determineCoordinateFromSquareInt( int location ) {
        int xPosition = location % 15;
        int yPosition = location / 15;
        String[] alphaArr = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("");
        String xCoordinate = alphaArr[xPosition];
        return (xCoordinate + yPosition);
    }

    /**
     * getter for the current player's ID.
     * @return current player's ID.
     */
    public int getCurrentPlayerID() {
        return serverPlayers[currentPlayer].getId();
    }

    public ServerPlayer getCurrentPlayer() { return serverPlayers[currentPlayer];}

    public ServerPlayer getPlayerByID(int id) {
        for (ServerPlayer serverPlayer : serverPlayers) {
            if (serverPlayer.getId() == id) return serverPlayer;
        }
        return null;
    }

    protected boolean makeMove(String[] moveTiles) {
        LinkedHashMap<String, String> letterSquareMap = mapLetterToSquare(moveTiles);
        if (letterSquareMap == null) return false;
        Board validBoard = isValidMove(letterSquareMap);

        if (validBoard != null) {
            board = validBoard.clone();
            return true;
        }
        return false;
    }

    protected LinkedHashMap<String, String> mapLetterToSquare(String[] move){
        LinkedHashMap<String , String > letterToSquare = new LinkedHashMap<>();

        for (int i = 0; i < move.length; i++) {
            String[] letterSquarePairs = move[i].split("");
            String charMove = "";
            int coordinate;
            if (letterSquarePairs.toString().contains("-")) {
                charMove = letterSquarePairs[0] + letterSquarePairs[1];
                String coordinateString = "";
                for (int j = 2; j < letterSquarePairs.length; j++ ) {
                    coordinateString += letterSquarePairs[j];
                }
                try {
                    coordinate = Integer.parseInt(coordinateString);
                } catch (NumberFormatException e) {
                    return null;
                }
            } else {
                charMove = letterSquarePairs[0];
                String coordinateString = "";
                for (int j = 1; j < letterSquarePairs.length; j++ ) {
                    coordinateString += letterSquarePairs[j];
                }
                try {
                    coordinate = Integer.parseInt(coordinateString);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            letterToSquare.put(determineCoordinateFromSquareInt(coordinate),charMove);
        }
        return letterToSquare;
    }

    protected void swapTray(String[] chars) {
        ArrayList<Tile> shuffledTiles = new ArrayList<>();
        for (String character : chars) {
            Tile tile = determineTileFromString(character);
            shuffledTiles.add(tile);
        }
        for (Tile tile: shuffledTiles) {
            tileBag.add(tile);
            serverPlayers[currentPlayer].getTray().remove(tile);
        }
    }

    protected Tile determineTileFromString(String character) {
        ArrayList<Tile> tray = serverPlayers[currentPlayer].getTray();
        for (Tile tile: tray){
            if (character.equals("-") && character.equals(Character.toString(tile.getLetter()))) {
                return tile;
            }
            else if (character.equals(Character.toString(tile.getLetter()))) {
                return tile;
            }
        }
        return null;
    }

    protected Board isValidMove(LinkedHashMap<String, String> moves) {
        Board copyBoard = board.clone();
        String direction = determineMoveDirection(moves);

        ArrayList<Square> initialWord = new ArrayList<>();
        ArrayList<Square> playSquares = new ArrayList<>();

        for (Map.Entry<String, String> move : moves.entrySet()) {
            String character = move.getValue();
            Square location = copyBoard.getSquare(move.getKey());
            Tile tile = determineTileFromString(character);
            if (tile == null) {
                return null;
            }
            location.setTile(tile);
            initialWord.add(location);
            playSquares.add(location);
        }

        //Check the player choice of square validation
        if (!isValidPlacement(playSquares, direction, copyBoard)) return null;

        ArrayList<ArrayList<Square>> wordCombinations =
                determinePossibleWordCombinations(initialWord, direction,copyBoard);

        if (wordCombinations == null || wordCombinations.size() == 0) {
            return null;
        }
        for (ArrayList<Square> wordCombination : wordCombinations) {
            String validWord = wordChecker(wordCombination);
            if (validWord == null) {
                return null;
            }
            turnScore += calculatePoints(wordCombination);
        }
        if (initialWord.size() == 7) {
            turnScore += 50;
        }
        serverPlayers[currentPlayer].addPoints(turnScore);
        for (Square square : initialWord) {
            ArrayList<Tile> tray = serverPlayers[currentPlayer].getTray();
            tray.remove(square.getTile());
            square.setType(SquareType.NORMAL);
        }

        updateValidSquares(playSquares, direction, copyBoard);
        return copyBoard;
    }

    protected boolean isEmptyTrayAndBag() {
        return serverPlayers[currentPlayer].getTray().isEmpty() && tileBag.isEmpty();
    }
}
