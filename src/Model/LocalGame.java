package Model;
/**
 * This class implements the basic game functions of Scrabble.
 * @author Hung Nguyen, Nhat Tran
 * @version 0.1
 */

import View.LocalView;
import WordChecker.main.java.InMemoryScrabbleWordChecker;
import WordChecker.main.java.ScrabbleWordChecker;

import java.io.IOException;
import java.util.*;

public class LocalGame {
    private Board board;
    private Player[] players;
    private List<Tile> tileBag;
    private ArrayList<String> usedWords;
    private ScrabbleWordChecker checker;
    private int numPlayer;
    private int currentPlayer;
    private int passCount;
    private LocalView UI;
    private List<Square> nextValidSquares;



    /**
     * Creates a new game
     * @param players number of players
     */

    public LocalGame(Player[] players) {
        board = new Board();
        this.numPlayer = players.length;
        this.players = players;
        tileBag = new TileGenerator().generateTiles();
        usedWords = new ArrayList<>();
        checker = new InMemoryScrabbleWordChecker();
        currentPlayer = 0;
        passCount = 0;
        UI = new LocalView();
        nextValidSquares = new ArrayList<>();

        for (Player player : players)
        {
            ArrayList<Tile> tray = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                int j = new Random().nextInt(tileBag.size());
                Tile tile = tileBag.get(j);
                tileBag.remove(tile);
                tray.add(tile);
            }
            player.setTray(tray);
        }
    }

    /**
     * determine the winner player.
     * @return the winner (player).
     */

    public Player isWinner(){
        Map<Player, Integer> finalDeduct = new HashMap<>();

        ArrayList<Tile> tilesLeft = null;

        //Create a map of players with their deduct points
        for (Player currentLocalPlayer : players) {
            tilesLeft = currentLocalPlayer.getTray();
            int deductPoints = 0;
            for (Tile tile : tilesLeft) {
                deductPoints += tile.getPoint();
            }
            finalDeduct.put(currentLocalPlayer, deductPoints);
        }


        for (Player player : players) {
            var finalPoints = 0;
            if (tilesLeft.size() == 0) {
                int totalDeductPoints = finalDeduct.get(players[0]) + finalDeduct.get(players[1])
                        + finalDeduct.get(players[2]) + finalDeduct.get(players[3]);
                finalPoints = player.getTotalPoints() + totalDeductPoints;
            } else {
                finalPoints = player.getTotalPoints() - finalDeduct.get(player);
            }
            player.setFinalPoints(finalPoints);
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
     * resetting the pass count.
     */

    public void resetPassCount() {
        this.passCount = 0;
    }

    /**
     * increment the pass count.
     */

    public void incrementPassCount() {
        this.passCount++;
    }

    /**
     * @return the current passCount
     */

    public int getPassCount() {
        return passCount;
    }

    /**
     * Return the description if the word exist, otherwise, return null
     * @param squares that contain words to check
     * @return null if the word does not exist, or the description of that word if it exists.
     */
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

    /**
     * Calculate the point for each new word made
     * @ensures squares.length > 0
     * @param squares the squares contain the words to calculate point
     * @return point.
     */
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

    /**
     * @requires player is not null
     * add Tile to each player's tray
     * @param player which player to add tile to.
     */
    private void addTileToTray(Player player) {
        ArrayList<Tile> tray = player.getTray();
        int bagSize = tileBag.size();
        int missingTile = bagSize == 0 ? 0 : Math.min(bagSize, (7 - tray.size()));

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
     * Method to determine if the bag is empty and the current player's tray is empty.
     * @return true if the current player's tray is empty and the tile bag is empty, false otherwise.
     */
    private boolean isEmptyTrayAndBag() {
        return players[currentPlayer].getTray().isEmpty() && tileBag.isEmpty();
    }


    /**
     * Check if game is over.
     * @return true is game already over, false otherwise.
     */

    public boolean gameOver() {
        return isEmptyTrayAndBag() || isFullBoard() || passCount > 5;
    }

    /**
     * To pass the play if the player decides to.
     */
    public int incrementCurrentPlayer() {
        currentPlayer = currentPlayer < (numPlayer-1) ? currentPlayer+1 : 0;
        return currentPlayer;
    }

    /**
     * Get letter from the player's tray.
     * @param tray player's tray.
     * @return tray, represents in String ArrayList format.
     */
    public ArrayList<String> getLetterFromTray(ArrayList<Tile> tray) {
        ArrayList<String> letterTray = new ArrayList<>();
        for (Tile tile : tray) {
            letterTray.add(Character.toString(tile.getLetter()));
        }
        return letterTray;
    }

    /**
     * getter for board
     * @return Board.
     */
    public Board getBoard() { return board; }

    /**
     * getter for Tile Bag
     * @return Tile Bag
     */

    public List<Tile> getTileBag() { return tileBag; }

    /**
     * getter for current player.
     * @return the current player.
     */

    public Player getCurrentPlayer() { return players[currentPlayer]; }

    /**
     * determine the move from the player's tile input.
     * @param moveTiles the String Array represents the move inputted by the player.
     * @return true if move valid, false otherwise.
     */

    public boolean makeMove(String[] moveTiles) {
        Board validBoard = isValidMove(mapLetterToSquare(moveTiles));
        if (validBoard != null) {
            board = validBoard.clone();
            return true;
        }
        resetPassCount();
        return false;
    }

    /**
     * Create a Linked HashMap, with key is the square location and value is the Tile's letter
     * representation.
     * @param move ArrayList representing the move make by player, i.e. D.A1 O.A2 G.A3
     * @return a map that map letter to square.
     */

    private LinkedHashMap<String, String> mapLetterToSquare(String[] move){
        LinkedHashMap<String , String > letterToSquare = new LinkedHashMap<>();
        for (int i = 0; i < move.length; i++) {
            String[] letterSquarePair = move[i].split("[.]");
            if (letterSquarePair.length < 2) {
                return null;
            }
            String[] coordinate = letterSquarePair[1].split("");
            if (coordinate.length > 3) {
                System.out.println("Coordinate's length should not be greater than 3.");
                return null;
            }
            for (int j = 1; j < coordinate.length; j++) {
                try {
                    Integer.parseInt(coordinate[j]);
                } catch (NumberFormatException e) {
                    System.out.println("Wrong input format. Move should be D-H7 O-H8 G-H9 and so on.");
                    return null;
                }
            }
            letterToSquare.put(letterSquarePair[1], letterSquarePair[0]);
        }
        return letterToSquare;
    }

    /**
     * Swap the tiles that the player chooses with new tiles from the tileBag,
     * if there is still tiles in the tileBag.
     * @param chars the chars Array representation of the tiles player want to
     *              swap.
     */

    public void swapTray(char[] chars) {
        if (tileBag.isEmpty()) incrementPassCount();
        else {
            ArrayList<Tile> shuffledTiles = new ArrayList<>();
            for (char character : chars) {
                Tile tile = determineTileFromChar(character);
                shuffledTiles.add(tile);
            }
            for (Tile tile: shuffledTiles) {
                tileBag.add(tile);
                players[currentPlayer].getTray().remove(tile);
            }
            addTileToTray(players[currentPlayer]);
            resetPassCount();

        }
    }

    /**
     * determine the tile from the player's input and whether the player has that tile.
     * @param character the player's input.
     * @return the Tile associated with the character if it's in the player's tray,
     * null otherwise.
     */

    private Tile determineTileFromChar(char character) {
        ArrayList<Tile> tray = players[currentPlayer].getTray();
        for (Tile tile: tray){
            if (character == '#' && character == tile.getLetter() ) {
                String prompt = "Please choose one of the letters below:\n"
                        + "A B C D E F G H I J K L M N O P Q R S T U V W X Y Z\n";
                try{
                    char input = UI.getChar(prompt);
                    tile.setLetter(input);
                    return tile;

                } catch (IllegalArgumentException | IOException e) {
                    return null;
                }
            }
            else if (tile.getLetter() == character) {
                return tile;
            }
        }
        return null;
    }

    /**
     * Check if the board is full.
     * @return true if there is no more free square left in the board, false otherwise.
     */

    private boolean isFullBoard() {
        for (int i = 0; i < board.SIZE; i++) {
            if (!board.getSquare(i).hasTile()) return false;
        }
        return true;
    }

    /**
     * determine possible word Combinations from the input word,
     * @param inputWord the Square contains the input word.
     * @param direction moving direction ("H" for horizontal, "V" for vertical). Call determineMoveDirection to determine.
     * @param copyBoard Board
     * @return the ArrayList contains the ArrayList of Squares that represents the valid word combination. Return null if there
     * is at least one wrong combination.
     */
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

            for (Square square : getOccupiedSquare(copyBoard, inputWord)) {
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

    /**
     * check if a move is valid or not, including dictionary check.
     * @param moves the Linked Hash Map with location at key and String tile representation as values.
     * @return null if invalid move, or the validated board since the validation is carried out in a
     * copy version of the main board, made right before validating the move.
     */

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
            UI.showMessage("Wrong input format.");
            return null;
        }
        int turnScore = 0;
        for (ArrayList<Square> wordCombination : wordCombinations) {
            String validWord = wordChecker(wordCombination);
            if (validWord == null) {
                UI.showMessage("The word " + getWordFromSquareList(wordCombination) + " is invalid. Skipping your turn...");
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

    /**
     * Get the word representing by the ArrayList of Squares.
     * @param squares the Squares containing the word/
     * @return the word.
     */

    public String getWordFromSquareList(ArrayList<Square> squares) {
        String word = "";
        for (Square square : squares)  word +=square.getTile().getLetter();
        return word;
    }

    /**
     * determine the move direction.
     * @param moves the player's input move.
     * @return "H" for horizontal, "V" for vertical.
     */

    private  String determineMoveDirection(LinkedHashMap<String, String> moves) {
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

    /**
     * Get the List of Square which is being occupied by a tile on the board.
     * @param copyBoard Board to check.
     * @param playSquares the square which has just recently been placed.
     * @return the List of Occupied Squares.
     */

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

    /**
     * get the valid squares before the move.
     * @param playSquares The List contains the squares which represents the moves of the player
     * @param direction the move direction. "H" for Horizontal, "V" for vertical.
     * @param copyBoard the board to check.
     */

    private  void getNextValidSquares(List<Square> playSquares, String direction, Board copyBoard) {

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

    /**
     * check if a placement is valid or not.
     * @param playSquares The List contains the squares which represents the moves of the player
     * @param direction the move direction. "H" for Horizontal, "V" for vertical.
     * @param copyBoard the board to check.
     * @return true if the placement adhere to the game rules, false otherwise.
     */

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
}
