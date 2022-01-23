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
    List<Square> occupiedSquares = new ArrayList<>();
    List<Square> nextValidSquares = new ArrayList<>();
    private String previousMove;
    private int passCount;


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
        previousMove = "";
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
            System.out.println("square locator: " + square.getLocation());
        }
        for (Square square : squares) {
            word += Character.toString(square.getTile().getLetter());
        }
        for (String usedWord : usedWords) {
            if (usedWord.equals(word)) {
                return word;
            }
        }
        System.out.println("word:" + word);
//        usedWords.add(checker.isValidWord(word).toString());

        return checker.isValidWord(word) == null ?  null : checker.isValidWord(word).toString();
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
        System.out.println("Current player is " + currentPlayer);
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
        System.out.println("GAME IS: " + (passCount > 5));
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
        Player winner = players[0];
        Map<Player, Integer> finalDeduct = new HashMap<Player, Integer>();
        ArrayList<Tile> tilesLeft = null;

        if (gameOver()) {
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
                //Calculate final score for each player
                if (tilesLeft.size() == 0) {
                    int totalDeductPoints = finalDeduct.get(players[0]) + finalDeduct.get(players[1])
                            + finalDeduct.get(players[2]) + finalDeduct.get(players[3]);
                    finalPoints = players[i].getTotalPoints() + totalDeductPoints;
                } else {
                    finalPoints = players[i].getTotalPoints() - finalDeduct.get(players[i]);
                }
                players[i].setFinalPoints(finalPoints);
                //Find the final winner (up to this player)
                if (finalPoints > winner.getTotalPoints()){
                    winner = players[i];
                }

            }

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
        Player winner = isWinner();
        System.out.println("Congratulations! Player " + winner.getName() + "has won!"
        );
    }


    public void play() throws IOException {
        System.out.println("Welcome to Scrabble!");
//        while (!gameOver()) {
            System.out.println("Let's play!");
            loopingOverTheGame: for (currentPlayer = 0; currentPlayer < players.length;) {
                update();
                String[] moves = players[currentPlayer].determineMove();
                System.out.println("Current pass count is: " + (passCount+1));
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
                        break;
                    case "SHUFFLE":
                        // To be implemented.
                        shuffleTray();
                        nextPlayer();
                        passCount = 0;
                        break;
                }
                if (gameOver()) {
                    break loopingOverTheGame;
                }

            }
//        }
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
            System.out.println("where to put " + startingPosition.getLocation() + " what to put " + startingPosition.getTile().getLetter());
            startingPosition = nextPosition;
        }
        return squares;
    }

    private ArrayList<ArrayList<Square>> determinePossibleWordCombinations(Square startingPosition, String direction, Board copyBoard) {
        ArrayList<ArrayList<Square>> wordCombinations = new ArrayList<>();
        ArrayList<Square> initialWord = new ArrayList<>();
        Square otherSideStartingPosition = null;

        if (startingPosition.getxPosition() > 0 && startingPosition.getyPosition() > 0) {
            otherSideStartingPosition = direction.equals("H") ? copyBoard.getSquareLeft(startingPosition)
                    : copyBoard.getSquareAbove(startingPosition);
        }


        traversingRightandBelow : while (startingPosition != null
                && startingPosition.getxPosition() < 15
                && startingPosition.getyPosition() < 15
                && startingPosition.hasTile()) {

            Square currentPosition = startingPosition;


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
                initialWord.add(startingPosition);
                startingPosition = currentPosition;


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
                initialWord.add(startingPosition);
                startingPosition = currentPosition;

                if (horizontalWord.size() > 1) wordCombinations.add(horizontalWord);
            }



        }
        traversingLeftandAbove: while (otherSideStartingPosition != null
                && otherSideStartingPosition.getxPosition() >= 0
                && otherSideStartingPosition.getyPosition() >= 0
                && otherSideStartingPosition.hasTile()) {
            Square currentPosition = otherSideStartingPosition;
            System.out.println("got into traversing left and above: ");
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
            if (startingPosition == null) {
                break traversingLeftandAbove;
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
            location.setTile(players[currentPlayer].determineTileFromChar(character));
            initialWord.add(location);
            playSquares.add(location);
        }

        //Check the player choice of square validation
        if (!isValidPlacement(playSquares, copyBoard)) return null;

        ArrayList<ArrayList<Square>> wordCombinations =
                determinePossibleWordCombinations(initialWord.get(0), direction,copyBoard);

        if (wordCombinations.size() == 0) {
            System.out.println("nothing to check");
            return null;
        }
        int turnScore = 0;
        for (ArrayList<Square> wordCombination : wordCombinations) {
            for (Square square : wordCombination) {
                System.out.println("check all combi " + square.getLocation() + ": " + (square.getTile() == null ? "" : square.getTile().getLetter()) ) ;
            }
        }
        for (ArrayList<Square> wordCombination : wordCombinations) {
            String validWord = wordChecker(wordCombination);
            if (validWord == null) {
                System.out.println("The word: " + getWordFromSquareList(wordCombination) + " is invalid. Skipping your turn...");
                return null;
            }

            turnScore += calculatePoints(wordCombination);
        }
        players[currentPlayer].addPoints(turnScore);
        for (Square square : initialWord) {
            ArrayList<Tile> tray = players[currentPlayer].getTray();
            tray.remove(square.getTile());
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

    public void shuffleTray(){
        ArrayList<Tile> tray = players[currentPlayer].getTray();
        for (Tile tile: tray) {
            tileBag.add(tile);
        }
        tray.removeAll(tray);
        addTileToTray(players[currentPlayer]);
    }
    



    public List<Square> getNextValidSquares(List<Square> playSquares, String direction, Board copyBoard) {

        for (Square square : playSquares) {
            occupiedSquares.add(square);
            nextValidSquares.remove(square);
        }

        for (int i = 0; i < playSquares.size(); i++) {
            if(direction.equals("H")) {
                if (i == 0 && copyBoard.getSquareLeft(playSquares.get(i)).getTile() == null)
                    nextValidSquares.add(copyBoard.getSquareLeft(playSquares.get(i)));

                else if (i == playSquares.size() -1 && copyBoard.getSquareRight(playSquares.get(i)).getTile() == null)
                    nextValidSquares.add(copyBoard.getSquareRight(playSquares.get(i)));

                if (copyBoard.getSquareAbove(playSquares.get(i)).getTile() == null)
                    nextValidSquares.add(copyBoard.getSquareAbove(playSquares.get(i)));
                if (board.getSquareBelow(playSquares.get(i)).getTile() == null)
                    nextValidSquares.add(copyBoard.getSquareBelow(playSquares.get(i)));
            }

            else {
                if (i == 0 && copyBoard.getSquareAbove(playSquares.get(i)).getTile() == null)
                    nextValidSquares.add(copyBoard.getSquareAbove(playSquares.get(i)));

                else if (i == playSquares.size() -1 && copyBoard.getSquareBelow(playSquares.get(i)).getTile() == null)
                    nextValidSquares.add(copyBoard.getSquareBelow(playSquares.get(i)));

                if (copyBoard.getSquareRight(playSquares.get(i)).getTile() == null)
                    nextValidSquares.add(copyBoard.getSquareRight(playSquares.get(i)));
                if (copyBoard.getSquareLeft(playSquares.get(i)).getTile() == null)
                    nextValidSquares.add(copyBoard.getSquareLeft(playSquares.get(i)));
            }
        }
        System.out.println("Next valid squares: " + nextValidSquares.toString());
        return nextValidSquares;

    }

    public boolean isValidPlacement(List<Square> playSquares, Board copyBoard){
        Square centralSquare = copyBoard.getSquare("H7");
        if (tileBag.size() == 86 && playSquares.contains(centralSquare)) return true;
        for (Square playSquare: playSquares){
            if (tileBag.size() != 86 && nextValidSquares.contains(playSquare)) return true;
        }
        System.out.println("Invalid placement. In the first round, player has to put one tile on H7 square.\n" +
                "During the remaining game, at least one tile placed by the player has to connect to one of the tiles on the board.");
        return false;
    }

}
