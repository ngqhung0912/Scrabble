package NetworkController;

import Model.*;
import WordChecker.main.java.InMemoryScrabbleWordChecker;
import WordChecker.main.java.ScrabbleWordChecker;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class ServerGame {

    private Server server;
    private List<Tile> tileBag;
    private Board board;
    private int passCount;
    private int currentPlayer;
    private int numPlayer;
    private ServerPlayer[] serverPlayers;
    private ScrabbleWordChecker checker;
    private List<Square> nextValidSquares;
    private ArrayList<String> usedWords;
    private int turnScore;
    private volatile String moveType;
    private volatile String move;

    /**
     * Creates a new game
     *
     * @param server The game server
     */
    public ServerGame(Server server) {
        this.server = server;
        tileBag = new TileGenerator().generateTiles();
        board = new Board();
        numPlayer = server.getClients().size();
        serverPlayers = new ServerPlayer[numPlayer];
        usedWords = new ArrayList<>();
        nextValidSquares = new ArrayList<>();
        currentPlayer = 0;
        passCount = 0;
        turnScore = 0;
        checker = new InMemoryScrabbleWordChecker();
        int i = 0;
        for (ClientHandler client : server.getClients().values()) {
            serverPlayers[i] = new ServerPlayer(client);
            i++;
        }
    }

    private String wordChecker(ArrayList<Square> squares) {
        String word = "";
        for (Square square : squares) {
            word += Character.toString(square.getTile().getLetter());
        }
        for (String usedWord : usedWords) {
            if (usedWord.equals(word)) {
                return word;
            }
        }
        if (checker.isValidWord(word) != null) {
            usedWords.add(checker.isValidWord(word).toString());
//            UI.showMessage(checker.isValidWord(word).toString());
            return checker.isValidWord(word).toString();
        }
        else {
            return null;
        }
    }

    private int calculatePoints(ArrayList<Square> squares) {
        int score = 0;
        boolean doubleWord = false;
        boolean tripleWord = false;
        for (Square square : squares) {
            switch (square.getType()) {
                case CENTER:
                    doubleWord = true;
                    break;
                case DOUBLE_LETTER:
                    score += square.getTile().getPoint()*2;
                    break;
                case TRIPLE_LETTER:
                    score += square.getTile().getPoint()*3 ;
                    break;
                case DOUBLE_WORD:
                    doubleWord = true;
                    score += square.getTile().getPoint();
                    break;
                case TRIPLE_WORD:
                    tripleWord = true;
                    score += square.getTile().getPoint();
                    break;
                case NORMAL:
                    score += square.getTile().getPoint();
                    break;
            }
        }
        return  (doubleWord && !tripleWord ? score * 2 : tripleWord && !doubleWord ? score * 3 : doubleWord && tripleWord ? score * 6 : score);
    }

    protected String addNewTilesToTray(int currentPlayerID) {
        ServerPlayer currentPlayer = getPlayerByID(currentPlayerID);
        ArrayList<Tile> tray = currentPlayer.getTray();
        int bagSize = tileBag.size();

        int missingTiles = bagSize == 0 ? 0 : bagSize < (7 - tray.size()) ? bagSize : 7 - tray.size();
        missingTiles = bagSize < missingTiles ? bagSize : missingTiles;

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

    private boolean isFullBoard() {
        for (int i = 0; i < board.SIZE; i++) {
            if (!board.getSquare(i).hasTile()) return false;
        }
        return true;
    }

    protected int setNextPlayer() {
        currentPlayer = currentPlayer < numPlayer-1 ? currentPlayer+1 : 0;
        return currentPlayer;
    }

    protected void resetPassCount() {
        this.passCount = 0;
    }

    protected void incrementPassCount() {
        this.passCount++;
    }

    private boolean isEmptyTrayAndBag() {
        return serverPlayers[currentPlayer].getTray().isEmpty() && tileBag.isEmpty();
    }

    protected boolean gameOver() {
        return isEmptyTrayAndBag() || passCount > 5 || isFullBoard();
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

    protected int getCurrentPlayerID() {
        return serverPlayers[currentPlayer].getId();
    }

    public ServerPlayer getCurrentPlayer() { return serverPlayers[currentPlayer];}

    protected ServerPlayer getPlayerByID(int id) {
        for (ServerPlayer serverPlayer : serverPlayers) {
            if (serverPlayer.getId() == id) return serverPlayer;
        }
        return null;
    }

    public ArrayList<String> getLetterFromTray(ArrayList<Tile> tray) {
        ArrayList<String> letterTray = new ArrayList<>();
        for (Tile tile : tray) {
            letterTray.add(Character.toString(tile.getLetter()));
        }
        return letterTray;
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

    private LinkedHashMap<String, String> mapLetterToSquare(String[] move){
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

    private String determineCoordinateFromSquareInt( int location ) {
        int xPosition = location % 15;
        int yPosition = location / 15;
        String[] alphaArr = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("");
        String xCoordinate = alphaArr[xPosition];
        return (xCoordinate + yPosition);
    }

    protected void swapTray(char[] chars) {
        ArrayList<Tile> shuffledTiles = new ArrayList<>();
        for (char character : chars) {
            Tile tile = determineTileFromChar(character);
            shuffledTiles.add(tile);
        }
        for (Tile tile: shuffledTiles) {
            tileBag.add(tile);
            serverPlayers[currentPlayer].getTray().remove(tile);
        }
    }

    private Tile determineTileFromChar(char character) {
        ArrayList<Tile> tray = serverPlayers[currentPlayer].getTray();
        for (Tile tile: tray){
            if (character == '-' && character == tile.getLetter() ) {
                return tile;
            }
            else if (tile.getLetter() == character) {
                return tile;
            }
        }
        return null;
    }

    private  ArrayList<ArrayList<Square>> determinePossibleWordCombinations(ArrayList<Square> inputWord, String direction, Board copyBoard) {
        Square startingPosition = inputWord.get(0);

        ArrayList<ArrayList<Square>> wordCombinations = new ArrayList<>();
        ArrayList<Square> initialWord = new ArrayList<>();
        Square otherSideStartingPosition = null;

        if (startingPosition.getxPosition() > 0 && startingPosition.getyPosition() > 0 ) {
            if (inputWord.size() > 1) otherSideStartingPosition = direction.equals("H") ? copyBoard.getSquareLeft(startingPosition)
                    : copyBoard.getSquareAbove(startingPosition);
            else {
                if (copyBoard.getSquareLeft(startingPosition) == null) {
                    otherSideStartingPosition = copyBoard.getSquareAbove(startingPosition) == null ? null :
                            copyBoard.getSquareAbove(startingPosition);
                } else {
                    otherSideStartingPosition = copyBoard.getSquareLeft(startingPosition);
                }
            }
        }
        traversingRightandBelow : while (startingPosition != null
                && startingPosition.getxPosition() < 15
                && startingPosition.getyPosition() < 15
                && startingPosition.hasTile()) {

            Square currentPosition = startingPosition;
            initialWord.add(startingPosition);

            List<Square> occupiedSquares = getOccupiedSquare(copyBoard,inputWord);
            for (Square square : occupiedSquares) {
                if (currentPosition.getLocation().equals(square.getLocation())) {
                    startingPosition = direction.equals("H") ? copyBoard.getSquareRight(startingPosition)
                            : copyBoard.getSquareBelow(startingPosition);
                    continue traversingRightandBelow;
                }
            }


            if (direction.equals("H")) {
                ArrayList<Square> verticalWord = new ArrayList<>();
                verticalWord.add(currentPosition);
                Square nextAbovePosition = currentPosition;
                Square nextBelowPosition = currentPosition;


                AboveWhileH: while(nextAbovePosition.getyPosition() > 0 && copyBoard.getSquareAbove(nextAbovePosition).hasTile()) {
                    verticalWord.add(0,copyBoard.getSquareAbove(nextAbovePosition));
                    nextAbovePosition = copyBoard.getSquareAbove(nextAbovePosition);
                }

                belowWhileH: while(nextBelowPosition.getyPosition() < 15 && copyBoard.getSquareBelow(nextBelowPosition).hasTile()) {
                    verticalWord.add(copyBoard.getSquareBelow(nextBelowPosition));
                    nextBelowPosition = copyBoard.getSquareBelow(nextBelowPosition);
                }
                currentPosition = copyBoard.getSquareRight(currentPosition);


                if (verticalWord.size() > 1) wordCombinations.add(verticalWord);
            }
            else if (direction.equals("V")) {
                ArrayList<Square> horizontalWord = new ArrayList<>();
                horizontalWord.add(currentPosition);

                Square nextLeftPosition = currentPosition;
                Square nextRightPosition = currentPosition;

                leftWhileV: while(nextLeftPosition.getxPosition() > 0 && copyBoard.getSquareLeft(nextLeftPosition).hasTile()) {
                    horizontalWord.add(0,copyBoard.getSquareLeft(nextLeftPosition));
                    nextLeftPosition = copyBoard.getSquareLeft(nextLeftPosition);
                }

                rightWhileV: while(nextRightPosition.getxPosition() < 15 && copyBoard.getSquareRight(nextRightPosition).hasTile()) {
                    horizontalWord.add(copyBoard.getSquareRight(nextRightPosition));
                    nextRightPosition = copyBoard.getSquareRight(nextRightPosition);
                }
                currentPosition = copyBoard.getSquareBelow(currentPosition);

                if (horizontalWord.size() > 1) wordCombinations.add(horizontalWord);
            }

            startingPosition = currentPosition;


        }
        traversingLeftandAbove: while (otherSideStartingPosition != null
                && otherSideStartingPosition.getxPosition() >= 0
                && otherSideStartingPosition.getyPosition() >= 0
                && otherSideStartingPosition.hasTile()) {
            Square currentPosition = otherSideStartingPosition;
            if (direction.equals("H")) {
                currentPosition = copyBoard.getSquareLeft(currentPosition);
                initialWord.add(0,otherSideStartingPosition);
                otherSideStartingPosition = currentPosition;
            }
            else if (direction.equals("V")) {
                currentPosition = copyBoard.getSquareAbove(currentPosition);
                initialWord.add(0,otherSideStartingPosition);
                otherSideStartingPosition = currentPosition;
            }
//            if (startingPosition == null) {
//                break traversingLeftandAbove;
//            }
        }

        for (Square square : inputWord) {
            if (!initialWord.contains(square)) {
                return null;
            }
        }

        if (initialWord.size() > 1) wordCombinations.add(initialWord);
        return wordCombinations;
    }

    private Board isValidMove(LinkedHashMap<String, String> moves) {
        Board copyBoard = board.clone();
        String direction = determineMoveDirection(moves);

        ArrayList<Square> initialWord = new ArrayList<>();
        ArrayList<Square> playSquares = new ArrayList<>();

        for (Map.Entry<String, String> move : moves.entrySet()) {
            char character = move.getValue().toCharArray()[0];
            Square location = copyBoard.getSquare(move.getKey());
            Tile tile = determineTileFromChar(character);
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

        getNextValidSquares(playSquares, direction, copyBoard);
        return copyBoard;
    }

    protected int getTurnScore() { return this.turnScore;}

    protected void resetTurnScore() {this.turnScore = 0;}

    private static String determineMoveDirection(LinkedHashMap<String, String> moves) {
        if (moves.size() == 1) {
            return "H";
        }
        ArrayList<String> column = new ArrayList<>();

        for (Map.Entry<String, String> move : moves.entrySet()){
            column.add(move.getKey().split("")[0]);
        }
        if (!column.get(0).equals(column.get(1))) {
            return "H";
        }
        else {
            return "V";
        }



    }

    private boolean isValidPlacement(List<Square> playSquares, String direction, Board copyBoard){
        List<Square> occupiedSquares = getOccupiedSquare(copyBoard,playSquares);
        getNextValidSquares(playSquares, direction, copyBoard);
        Square centralSquare = copyBoard.getSquare("H7");
        if (usedWords.size() == 0 && playSquares.contains(centralSquare)) return true;
        for (Square playSquare: playSquares){
            if (usedWords.size() != 0) {
                for (Square validSquare : nextValidSquares) {
                    if (validSquare.getLocation().equals(playSquare.getLocation())) return true;
                }
                if (occupiedSquares.contains(playSquare)) return false;
            }
        }
        return false;
    }

    private  void getNextValidSquares(List<Square> playSquares, String direction, Board copyBoard) {
        List<Square> occupiedSquares = getOccupiedSquare(copyBoard,playSquares);

        for (int i = 0; i < playSquares.size(); i++) {
            Square currentSquare = playSquares.get(i);
            try {
                if (currentSquare.getxPosition() >= 0 && currentSquare.getxPosition() < 15 &&
                        currentSquare.getyPosition() >= 0 && currentSquare.getyPosition() < 15) {
                    if(direction.equals("H")) {
                        if (i == 0 && copyBoard.getSquareLeft(currentSquare).getTile() == null)
                            nextValidSquares.add(copyBoard.getSquareLeft(currentSquare));

                        else if (i == playSquares.size() -1 && copyBoard.getSquareRight(currentSquare).getTile() == null)
                            nextValidSquares.add(copyBoard.getSquareRight(currentSquare));

                        if (copyBoard.getSquareAbove(currentSquare).getTile() == null)
                            nextValidSquares.add(copyBoard.getSquareAbove(currentSquare));
                        if (copyBoard.getSquareBelow(currentSquare).getTile() == null)
                            nextValidSquares.add(copyBoard.getSquareBelow(currentSquare));
                    }
                    else {
                        if (i == 0 && copyBoard.getSquareAbove(currentSquare).getTile() == null)
                            nextValidSquares.add(copyBoard.getSquareAbove(currentSquare));

                        else if (i == playSquares.size() -1 &&  copyBoard.getSquareBelow(currentSquare).getTile() == null)
                            nextValidSquares.add(copyBoard.getSquareBelow(currentSquare));

                        if (copyBoard.getSquareRight(currentSquare).getTile() == null)
                            nextValidSquares.add(copyBoard.getSquareRight(currentSquare));
                        if (copyBoard.getSquareLeft(currentSquare).getTile() == null)
                            nextValidSquares.add(copyBoard.getSquareLeft(currentSquare));
                    }

                }

            } catch (NullPointerException e) {
                continue;
            }

        }
//        return nextValidSquares;
    }

    private List<Square> getOccupiedSquare(Board copyBoard, List<Square> playSquares) {
        List<Square> occupiedSquares = new ArrayList<>();
        for (int i = 0; i < (Board.SIZE * Board.SIZE) ; i++) {
            if(copyBoard.getSquare(i).hasTile()) occupiedSquares.add(copyBoard.getSquare(i));
        }

        for (Square square : playSquares) {
            occupiedSquares.remove(square);
        }
        return occupiedSquares;
    }

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

    public Board getBoard() {return board;}

    public List<Tile> getTileBag() {
        return tileBag;
    }

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
                            continue;
                        }
                    }
                }

                switch (moveType) {
                    case ProtocolMessages.MOVE:
                        System.out.println("MOVE. " + moveType + move);
                        String[] moves = move.split(ProtocolMessages.AS);
                        boolean validMove = makeMove(moves);
                        if (validMove) {
                            System.out.println("Move validated.");
                            int turnScore = getTurnScore();
                            resetTurnScore();
                            resetPassCount();
                            server.broadcastMove(move, turnScore, getCurrentPlayerID());
                            break;
                        }
                        server.broadcastInvalidMove(currentClient);
                    case ProtocolMessages.PASS:
                        System.out.println("PASS. " + moveType + move);
                        if (move != null) swapTray(move.toCharArray());
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
}
