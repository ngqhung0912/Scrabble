package Game;
/**
 * This class implements the basic game functions of Scrabble.
 * @author Hung Nguyen, Nhat Tran
 * @version 0.1
 */

import TUI.BoardConstructor;
import WordChecker.main.java.FileStreamScrabbleWordChecker;
import WordChecker.main.java.InMemoryScrabbleWordChecker;
import WordChecker.main.java.ScrabbleWordChecker;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

/**
 * @author Hung Nguyen, Nhat Tran
 * @version 0.1
 */

public class Game {
    private Board board;
    private int currentPlayer;
    private Player[] players;
    private List<Tile> tileBag;
    private ArrayList<String> usedWords;
    private ScrabbleWordChecker checker;
    private int passCount;
    private List<Square> occupiedSquares;
    private List<Square> nextValidSquares;


    /**
     * Creates a new game
     * @param numPlayers number of players
     * @param playerList list of players' name.
     * @requires numPlayers > 0 && numPlayers < 5
     * @ensures creates a new board and all squares are empty
     * @ensures A new tileBag is created for the game
     * @ensures player.getTray() is not null
     * @invariant players.length == numPlayers
     */

    public Game(int numPlayers, String[] playerList) {
        //hashMap usedrds contains the words that has been played, among with their starting coordinate.

        board = new Board();
        currentPlayer = 0;
        players = new Player[numPlayers];
        tileBag = new TileGenerator().generateTiles();
        usedWords = new ArrayList<String>();
        checker = new InMemoryScrabbleWordChecker();
        passCount = 0;
        occupiedSquares = new ArrayList<>();
        nextValidSquares = new ArrayList<>();
        /**
         * create a tray for each player, then add them to the playerList.
         */
        for (int p = 0; p < numPlayers; p++) {
            ArrayList<Tile> tray = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                int j = new Random().nextInt(tileBag.size());
                Tile tile = tileBag.get(j);
                tileBag.remove(tile);
                tray.add(tile);
            }
            Player player = new Player(playerList[p], tray);
            players[p] = player;
        }

    }

    /**
     * Getter for Board
     * @return the current playing board
     */

    public Board getBoard() {
        return board;
    }

    /**
     * Setter for board
     * @param board
     */

    public void setBoard(Board board) {
        this.board = board;
    }

    /**
     * Get the currently playing player's index
     * @return an int representing the current player
     */

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Set who is playing
     * @param currentPlayer the index of the player
     */

    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    /**
     * Getter for list of players
     * @return list of players
     */

    public Player[] getPlayers() {
        return players;
    }

    /**
     * Setter for list of players
     * @param players list of players
     */

    public void setPlayers(Player[] players) {
        this.players = players;
    }

    /**
     * Getter for the current tile Bag.
     * @return tile bag.
     */

    public List<Tile> getTileBag() {
        return tileBag;
    }

    public void setTileBag(List<Tile> tileBag) {
        this.tileBag = tileBag;
    }

    /**
     * Return the description if the word exist, otherwise, return null
     * @param squares that contain words to check
     * @return null if the word does not exist, or the description of that word if it exists.
     */
    public String wordChecker(ArrayList<Square> squares) {
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
            System.out.println(checker.isValidWord(word).toString());
            return checker.isValidWord(word).toString();
        }
        else {
            return null;
        }
    }


    /**
     * Calculate the point for each new word made
     * @ensures squares.length > 0
     * @param squares the squares contain the words to calculate point
     * @return point.
     */
    public int calculatePoints(ArrayList<Square> squares) {
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

    /**
     * @requires player is not null
     * add Tile to each player's tray
     * @param player which player to add tile to.
     *
     */
    public void addTileToTray(Player player) {
        ArrayList<Tile> tray = player.getTray();
        int bagSize = tileBag.size();
        int missingTile = bagSize == 0 ? 0 : bagSize < (7 - tray.size()) ? bagSize : 7 - tray.size();

        for (int i = 0; i < missingTile; i++) {
            bagSize = tileBag.size();
            int j = new Random().nextInt(bagSize);
            Tile tile = tileBag.get(j);
            tileBag.remove(tile);
            tray.add(tile);
        }
        player.setTray(tray);
    }

    /**
     *
     * @return true if the current player's tray is empty and the tile bag is empty, false otherwise.
     */
    public boolean isEmptyTrayAndBag() {
        return players[currentPlayer].getTray().isEmpty() && tileBag.isEmpty();
    }

    /**
     * To be implemented:
     * @return true if there is no more space to put a meaningful word in, false otherwise.
     */

    public boolean isFullBoard() {
        return false;
    }

    /**
     *
     * @return true is game already over, false otherwise.
     */

    public boolean gameOver() {
        return isEmptyTrayAndBag() || isFullBoard() || passCount > 5;
    }

    /**
     * To pass the play if the player decides to.
     */
    public void nextPlayer() {
        switch (players.length) {
            case 2:
                currentPlayer = currentPlayer == 0 ? 1 : 0;
                break;
            case 3:
                currentPlayer = currentPlayer == 0 ? 1 : currentPlayer == 1 ? 2 : 0;
                break;
            case 4:
                currentPlayer = currentPlayer == 0 ? 1 : currentPlayer == 1 ? 2 : currentPlayer == 2 ? 3 : 0;
                break;

        }
    }

    /**
     * Determine who is the winner
     * @return the winner.
     */

    public Player isWinner(){
        Map<Player, Integer> finalDeduct = new HashMap<>();

        ArrayList<Tile> tilesLeft = null;

        //Create a map of players with their deduct points
        for (Player currentPlayer : players) {
            tilesLeft = currentPlayer.getTray();
            int deductPoints = 0;
            for (Tile tile : tilesLeft) {
                deductPoints += tile.getPoint();
            }
            finalDeduct.put(currentPlayer, deductPoints);
        }


        for (int i = 0; i < players.length; i++) {
            int finalPoints = 0;
            if (tilesLeft.size() == 0) {
                int totalDeductPoints = finalDeduct.get(players[0]) + finalDeduct.get(players[1])
                        + finalDeduct.get(players[2]) + finalDeduct.get(players[3]);
                finalPoints = players[i].getTotalPoints() + totalDeductPoints;
            } else {
                finalPoints = players[i].getTotalPoints() - finalDeduct.get(players[i]);
            }
            players[i].setFinalPoints(finalPoints);
        }

        Player winner = players[0];
        for (int i = 1; i < players.length;) {
            int compare = winner.compareTo(players[i]);
            if (compare < 0) {
                winner = players[i];
            } else if (compare == 0) {
                if (winner.getTotalPoints() + finalDeduct.get(winner) < players[i].getTotalPoints() + finalDeduct.get(players[i])) {
                    winner = players[i];
                } else if (winner.getTotalPoints() + finalDeduct.get(winner) == players[i].getTotalPoints() + finalDeduct.get(players[i])) {
                    return null;
                }
            }
            i++;
        }
    return winner;
    }

    /**
     * Update the current board with the new total points of the current player
     */
    public ArrayList<String> getLetterFromTray(ArrayList<Tile> tray) {
        ArrayList<String> letterTray = new ArrayList<String>();
        for (Tile tile : tray) {
            letterTray.add(Character.toString(tile.getLetter()));
        }
        return letterTray;
    }
    public void update(){
        System.out.println("\n\n" + BoardConstructor.generateBoard(board) + "\n"
        + "Player: " + players[this.currentPlayer].getName() + "\n"
        + "Tray: " + getLetterFromTray(players[this.currentPlayer].getTray()) + "\n"
        + "Total Score: " + players[this.currentPlayer].getTotalPoints() + "\n"
        + "Current bag count: " + tileBag.size());
    }

    //To be implemented (with network): An update method where all player can see the new board

    /**
     * Print the final result of the game
     */
    public void printResult(){
//        Player winner = isWinner() ;
        if (isWinner() != null) System.out.println("Congratulations! Player " + isWinner().getName() + " has won!");
        else System.out.println("It's a draw!");
    }


    public void play() throws IOException {
        System.out.println("Welcome to Scrabble!");
        System.out.println("Let's play!");
        loopingOverTheGame: for (currentPlayer = 0; currentPlayer < players.length;) {
            update();
            String[] moves = players[currentPlayer].determineMove();
            switch (moves[0]) {
                case "MOVE":
                    String[] moveTiles = new String[moves.length-1];
                    for (int i = 1; i < moves.length; i++) {
                        moveTiles[i-1] = moves[i];
                    }
                    Board validBoard = isValidMove(players[currentPlayer].mapLetterToSquare(moveTiles));
                    if (validBoard != null) {
                        board = validBoard.clone();
                    }
                    nextPlayer();
                    passCount = 0;
                    break;
                case "PASS":
                    nextPlayer();
                    passCount++;
                    System.out.println("This is the " + passCount + " consecutive pass move(s).");
                    break;
                case "SWAP":
                    char[] shuffledTilesChar = new char[moves.length-1];
                    for (int i = 1; i < moves.length; i++) {
                        shuffledTilesChar[i-1] = moves[i].charAt(0);
                    }
                    ArrayList<Tile> shuffledTiles = players[currentPlayer].determineTileToShuffle(shuffledTilesChar);
                    shuffleTray(shuffledTiles);
                    nextPlayer();
                    passCount = 0;
                    break;
                default:
                    System.out.println("Wrong input format. skipping turn...");
                    nextPlayer();
                    break;
            }
            if (gameOver()) {
                break loopingOverTheGame;
            }

        }
        printResult();
    }

    public String getWordFromSquareList(ArrayList<Square> squares) {
        String word = "";
        for (Square square : squares)  word +=square.getTile().getLetter();
        return word;
    }


    private void removeWordFromSquare(ArrayList<Square> squares) {
        for (Square square : squares ) {
            board.getSquare(square.getxPosition(),square.getyPosition()).setTile(null);
        }
    }


    private ArrayList<Square> putWordInSquares(String word, String direction, Square startingPosition) {
        char[] charArray = word.toCharArray();

        ArrayList<Square> squares = new ArrayList<Square>();
        for (char character : charArray) {
            squares.add(startingPosition);
            Square nextPosition = direction.equals("H") ? board.getSquareRight(startingPosition) :
                    board.getSquareBelow(startingPosition);
            if (nextPosition.hasTile()) {
                return null;
            }
            startingPosition.setTile(players[currentPlayer].determineTileFromChar(character));
            startingPosition = nextPosition;
        }
        return squares;
    }

    private ArrayList<ArrayList<Square>> determinePossibleWordCombinations(ArrayList<Square> inputWord, String direction, Board copyBoard) {
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
        if (moves == null) {
            return null;
        }
        Board copyBoard = board.clone();
        String direction = determineMoveDirection(moves);

        ArrayList<Square> initialWord = new ArrayList<>();
        ArrayList<Square> playSquares = new ArrayList<>();

        for (Map.Entry<String, String> move : moves.entrySet()) {
            char character = move.getValue().toCharArray()[0];
            Square location = copyBoard.getSquare(move.getKey());
            Tile tile = players[currentPlayer].determineTileFromChar(character);
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
            System.out.println("Wrong input format.");
            return null;
        }
        int turnScore = 0;
        for (ArrayList<Square> wordCombination : wordCombinations) {
            String validWord = wordChecker(wordCombination);
            if (validWord == null) {
                System.out.println("The word " + getWordFromSquareList(wordCombination) + " is invalid. Skipping your turn...");
                return null;
            }
            turnScore += calculatePoints(wordCombination);
        }
        if (initialWord.size() == 7) {
            turnScore += 50;
        }
        players[currentPlayer].addPoints(turnScore);
        for (Square square : initialWord) {
            ArrayList<Tile> tray = players[currentPlayer].getTray();
            tray.remove(square.getTile());
            square.setType(SquareType.NORMAL);
        }
        addTileToTray(players[currentPlayer]);
        getNextValidSquares(playSquares, direction, copyBoard);
        return copyBoard;
    }

    private String determineMoveDirection(LinkedHashMap<String, String> moves) {
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

    public void shuffleTray(ArrayList<Tile> shuffledTiles){

        for (Tile tile: shuffledTiles) {
            tileBag.add(tile);
            players[currentPlayer].getTray().remove(tile);
        }
        addTileToTray(players[currentPlayer]);
    }
    



    public List<Square> getNextValidSquares(List<Square> playSquares, String direction, Board copyBoard) {

        for (Square square : playSquares) {
            occupiedSquares.add(square);
            nextValidSquares.remove(square);
        }

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
        return nextValidSquares;

    }

    public boolean isValidPlacement(List<Square> playSquares, String direction, Board copyBoard){
        nextValidSquares = getNextValidSquares(playSquares, direction, copyBoard);
        Square centralSquare = copyBoard.getSquare("H7");
        if (usedWords.size() == 0 && playSquares.contains(centralSquare)) return true;
        for (Square playSquare: playSquares){
            if (usedWords.size() != 0) {
                for (Square validSquare : nextValidSquares) {
                    if (validSquare.getLocation().equals(playSquare.getLocation())) return true;
                }
            }
        }
        System.out.println("Invalid placement. In the first round, player has to put one tile on H7 square.\n" +
                "During the remaining game, at least one tile placed by the player has to connect to one of the tiles on the board.");
        return false;
    }

}
