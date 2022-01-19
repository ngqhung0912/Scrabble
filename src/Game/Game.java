package Game;
/**
 * This class implements the basic game functions of Scrabble.
 * @author Hung Nguyen, Nhat Tran
 * @version 0.1
 */

import TUI.BoardConstructor;
import WordChecker.main.java.InMemoryScrabbleWordChecker;
import WordChecker.main.java.ScrabbleWordChecker;

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
        //hashMap usedWords contains the words that has been played, among with their starting coordinate.

        board = new Board();
        currentPlayer = 0;
        players = new Player[numPlayers];
        tileBag = new TileGenerator().generateTiles();
        usedWords = new ArrayList<String>();
        checker = new InMemoryScrabbleWordChecker();
        /**
         * create a tray for each player, then add them to the playerList.
         */
        for (int p = 0; p < numPlayers; p++) {

            ArrayList<Tile> tray = new ArrayList<>(7);

            for (int i = 0; i < tray.size(); i++) {
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
            word += square.getTile().getLetter();
        }
        for (String usedWord : usedWords) {
            if (usedWord.equals(word)) {
                return word;
            }
        }
        usedWords.add(checker.isValidWord(word).toString());
        return checker.isValidWord(word).toString();
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
                    break;
                case TRIPLE_WORD:
                    tripleWord = true;
                    break;
                case NORMAL:
                    score += square.getTile().getPoint();
                    break;
            }
        }
        return  (doubleWord ? score * 2 : tripleWord ? score * 3 : score);
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
        return isEmptyTrayAndBag() || isFullBoard();
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
    public void update(){
        System.out.println("\n\n" + BoardConstructor.generateBoard(this.board) + "\n"
        + "Player: " + players[this.currentPlayer].getName() + "\n"
        + "Tray: " + players[this.currentPlayer].getTray()+ "\n"
        + "Total Score: " + players[this.currentPlayer].getTotalPoints()) ;
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

    public void start() {

    }

    public void play() {
        while (!gameOver()) {
            for (currentPlayer = 0; currentPlayer < players.length; currentPlayer++) {
                update();
                String[] move = players[currentPlayer].determineMove();
                if (move[0].equals("move") && (move[3].equals("H") || move[3].equals("V"))) {
                    String direction = move[3];
                    Square startingPosition = board.getSquare(move[2]);
                    ArrayList<Square> initialWord = putWordInSquares(move[1], direction, startingPosition);
                    ArrayList<ArrayList<Square>> wordCombinations =
                            determinePossibleWordCombinations(startingPosition,direction);
                    wordCombinations.add(initialWord);
                    int turnScore = 0;
                    whileLoop : while(true) {
                        for (ArrayList<Square> wordCombination : wordCombinations) {
                            String validWord = wordChecker(wordCombination);
                            if (validWord.equals(null)) {
                                nextPlayer();
                                removeWordFromSquare(initialWord);
                                System.out.println("The word: " + getWordFromSquareList(wordCombination) + "is invalid. Skipping your turn...");
                                break whileLoop;
                            }
                            turnScore += calculatePoints(wordCombination);
                        }
                        break whileLoop;
                    }
                    players[currentPlayer].addPoints(turnScore);
                    for (Square square : initialWord) {
                        ArrayList<Tile> tray = players[currentPlayer].getTray();
                        tray.remove(square.getTile());
                    }
                    addTileToTray(players[currentPlayer]);
                    update();

                }
                nextPlayer();
            }
        }
    }

    public String getWordFromSquareList(ArrayList<Square> squares) {
        String word = "";
        for (Square square : squares)  word +=square.getTile().getLetter();
        return word;
    }


    public void removeWordFromSquare(ArrayList<Square> squares) {
        for (Square square : squares ) {
            board.getSquare(square.getxPosition(),square.getyPosition()).setTile(null);
        }
    }

    public ArrayList<Square> putWordInSquares(String word, String direction, Square startingPosition) {
        char[] charArray = word.toCharArray();
        ArrayList<Square> squares = new ArrayList<Square>();
        for (char character : charArray) {
            squares.add(startingPosition);
            Square nextPosition = direction.equals("H") ? board.getSquareRight(startingPosition) :
                    board.getSquareBelow(startingPosition);
            nextPosition.setTile(players[currentPlayer].determineTileFromChar(character));
            startingPosition = nextPosition;
        }
        return squares;
    }

    public ArrayList<ArrayList<Square>> determinePossibleWordCombinations(Square startingPosition, String direction) {
        ArrayList<ArrayList<Square>> wordCombinations = new ArrayList<ArrayList<Square>>();
        ArrayList<Square> initialWord = new ArrayList<Square>();


        initialWord.add(startingPosition);
        Square currentPosition = startingPosition;

        while (startingPosition.hasTile() || startingPosition.getxPosition() < 15 || startingPosition.getyPosition() < 15) {

            initialWord.add(currentPosition);

            if (direction.equals("H")) {
                currentPosition = board.getSquareRight(currentPosition);
                initialWord.add(currentPosition);
                startingPosition = currentPosition;

                Square tempCurrentPosition = currentPosition;
                Square nextLeftPosition;
                Square nextRightPosition;

                ArrayList<Square> horizontalWord = new ArrayList<Square>();
                horizontalWord.add(currentPosition);

                while (currentPosition.hasTile() || currentPosition.getxPosition() > 0) {
                    nextLeftPosition = board.getSquareLeft(currentPosition);
                    horizontalWord.add(0, nextLeftPosition);
                    currentPosition = nextLeftPosition;
                }
                currentPosition = tempCurrentPosition;

                while (currentPosition.hasTile() || currentPosition.getxPosition() < 15) {
                    nextRightPosition = board.getSquareRight(currentPosition);
                    horizontalWord.add(nextRightPosition);
                    currentPosition = nextRightPosition;
                }
                if (horizontalWord.size() > 1) wordCombinations.add(horizontalWord);

            }
            else {
                currentPosition = board.getSquareBelow(currentPosition);

                Square tempCurrentPosition = currentPosition;
                Square nextAbovePosition;
                Square nextBelowPosition;

                ArrayList<Square> verticalWord = new ArrayList<Square>();
                verticalWord.add(currentPosition);

                while (currentPosition.hasTile() || currentPosition.getyPosition() > 0) {
                    nextAbovePosition = board.getSquareAbove(currentPosition);
                    verticalWord.add(0, nextAbovePosition);
                    currentPosition = nextAbovePosition;
                }
                currentPosition = tempCurrentPosition;

                while (currentPosition.hasTile() || currentPosition.getyPosition() < 15) {
                    nextBelowPosition = board.getSquareBelow(currentPosition);
                    verticalWord.add(nextBelowPosition);
                    currentPosition = nextBelowPosition;
                }
                if (verticalWord.size() > 1) wordCombinations.add(verticalWord);
            }
        }
        wordCombinations.add(initialWord);
        return wordCombinations;
    }


    public boolean isValidMove(String move) {
        return false;
    }

//    public void reset() {
//        board.reset();
//        setTileBag(new TileGenerator().generateTiles());
//        currentPlayer = 0;
//        for (Player player : players) {
//            player.reset();
//            player.setTray();
//        }
//    }
}
