package Model;

import WordChecker.main.java.InMemoryScrabbleWordChecker;
import WordChecker.main.java.ScrabbleWordChecker;

import java.util.*;

/**
 * @author Hung Nguyen, Nhat Tran
 * @version finale
 */
public abstract class Game {
    protected volatile List<Tile> tileBag;
    protected Board board;
    protected int passCount;
    protected int currentPlayer;
    protected int numPlayer;
    protected ArrayList<String> usedWords;
    protected List<Square> nextValidSquares;
    protected ScrabbleWordChecker checker;

    public Game() {
        tileBag = new TileGenerator().generateTiles();
        board = new Board();
        usedWords = new ArrayList<>();
        nextValidSquares = new ArrayList<>();
        currentPlayer = 0;
        passCount = 0;
        checker = new InMemoryScrabbleWordChecker();

    }

    /**
     * Increment the current Player index by 1 if the current player index is less than the number
     * of players minus 1, or set it to 0 otherwise.
     * @return currentPlayer index.
     */
    public int setNextPlayer() {
        currentPlayer = currentPlayer < numPlayer-1 ? currentPlayer+1 : 0;
        return currentPlayer;
    }

    /**
     * Get an ArrayList String representation of all the tiles present in the tray.
     * @param tray player's tray.
     * @return an ArrayList contains of String character.
     */
    public ArrayList<String> getLetterFromTray(ArrayList<Tile> tray) {
        ArrayList<String> letterTray = new ArrayList<>();
        for (Tile tile : tray) {
            letterTray.add(Character.toString(tile.getLetter()));
        }
        return letterTray;
    }

    /**
     * Return the description if the word exist, otherwise, return null
     * @param squares that contain words to check
     * @return null if the word does not exist, or the description of that word if it exists.
     */
    protected String wordChecker(ArrayList<Square> squares) {
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
     * @param squares the squares contain the words to calculate point
     * @return point.
     */
    protected int calculatePoints(ArrayList<Square> squares) {
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
     * Check if the board is full.
     * @return true if there is no more free square left in the board, false otherwise.
     */
    protected boolean isFullBoard() {
        for (int i = 0; i < board.SIZE; i++) {
            if (!board.getSquare(i).hasTile()) return false;
        }
        return true;
    }

    /**
     * resetting the pass count.
     */
    protected void resetPassCount() {
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
     * Check if game is over.
     * @return true is game already over, false otherwise.
     */
    public boolean gameOver() {
        return isEmptyTrayAndBag() || passCount > 5 || isFullBoard();
    }

    /**
     * Method to determine if the bag is empty and the current player's tray is empty.
     * @return true if the current player's tray is empty and the tile bag is empty, false otherwise.
     */
    abstract boolean isEmptyTrayAndBag();

    /**
     * Create a Linked HashMap, with key is the square location and value is the Tile's letter
     * representation.
     * @param move ArrayList representing the move make by player, i.e. D.A1 O.A2 G.A3
     * @return a map that map letter to square.
     */
    abstract LinkedHashMap<String, String> mapLetterToSquare(String[] move);

    /**
     * determine possible word Combinations from the input word,
     * @param inputWord the Square contains the input word.
     * @param direction moving direction ("H" for horizontal, "V" for vertical). Call determineMoveDirection to determine.
     * @param copyBoard Board
     * @return the ArrayList contains the ArrayList of Squares that represents the valid word combination. Return null if there
     * is at least one wrong combination.
     */
    protected  ArrayList<ArrayList<Square>> determinePossibleWordCombinations(ArrayList<Square> inputWord, String direction, Board copyBoard) {
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

    /**
     * determine the tile from the player's input and whether the player has that tile.
     * @param character the player's input.
     * @return the Tile associated with the character if it's in the player's tray,
     * null otherwise.
     */
    abstract Tile determineTileFromString(String character);

    /**
     * check if a move is valid or not, including dictionary check.
     * @param moves the Linked Hash Map with location at key and String tile representation as values.
     * @return null if invalid move, or the validated board since the validation is carried out in a
     * copy version of the main board, made right before validating the move.
     */
    abstract Board isValidMove(LinkedHashMap<String, String> moves);

    /**
     * determine the move direction.
     * @param moves the player's input move.
     * @return "H" for horizontal, "V" for vertical.
     */
    protected static String determineMoveDirection(LinkedHashMap<String, String> moves) {
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
     * check if a placement is valid or not.
     * @param playSquares The List contains the squares which represents the moves of the player
     * @param direction the move direction. "H" for Horizontal, "V" for vertical.
     * @param copyBoard the board to check.
     * @return true if the placement adhere to the game rules, false otherwise.
     */
    protected boolean isValidPlacement(List<Square> playSquares, String direction, Board copyBoard){
        List<Square> occupiedSquares = getOccupiedSquare(copyBoard,playSquares);
        updateValidSquares(playSquares, direction, copyBoard);
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

    /**
     * get the valid squares before the move.
     * @param playSquares The List contains the squares which represents the moves of the player
     * @param direction the move direction. "H" for Horizontal, "V" for vertical.
     * @param copyBoard the board to check.
     */
    protected void updateValidSquares(List<Square> playSquares, String direction, Board copyBoard) {

        for (int i = 0; i < playSquares.size(); i++) {
            Square currentSquare = playSquares.get(i);
            try {
                if (currentSquare.getxPosition() >= 0 && currentSquare.getxPosition() < 15 &&
                        currentSquare.getyPosition() >= 0 && currentSquare.getyPosition() < 15) {
                    if (direction.equals("H")) {
                        if (i == 0 && !copyBoard.getSquareLeft(currentSquare).hasTile())
                            nextValidSquares.add(copyBoard.getSquareLeft(currentSquare));

                        else if (i == playSquares.size() - 1 && !copyBoard.getSquareRight(currentSquare).hasTile())
                            nextValidSquares.add(copyBoard.getSquareRight(currentSquare));

                        if (!copyBoard.getSquareAbove(currentSquare).hasTile())
                            nextValidSquares.add(copyBoard.getSquareAbove(currentSquare));
                        if (!copyBoard.getSquareBelow(currentSquare).hasTile())
                            nextValidSquares.add(copyBoard.getSquareBelow(currentSquare));
                    } else {
                        if (i == 0 && !copyBoard.getSquareAbove(currentSquare).hasTile())
                            nextValidSquares.add(copyBoard.getSquareAbove(currentSquare));

                        else if (i == playSquares.size() - 1 && !copyBoard.getSquareBelow(currentSquare).hasTile())
                            nextValidSquares.add(copyBoard.getSquareBelow(currentSquare));

                        if (!copyBoard.getSquareRight(currentSquare).hasTile())
                            nextValidSquares.add(copyBoard.getSquareRight(currentSquare));
                        if (!copyBoard.getSquareLeft(currentSquare).hasTile())
                            nextValidSquares.add(copyBoard.getSquareLeft(currentSquare));
                    }

                }
            } catch (NullPointerException e) {
                System.out.println("Catch the null pointer exception here is intentional.");
                continue;
            }
        }
    }

    /**
     * getter for board
     * @return Board.
     */
    public Board getBoard() {return board;}

    /**
     * getter for Tile Bag
     * @return Tile Bag
     */
    public List<Tile> getTileBag() {
        return tileBag;
    }

    /**
     * Get the List of Square which is being occupied by a tile on the board.
     * @param copyBoard Board to check.
     * @param playSquares the square which has just recently been placed.
     * @return the List of Occupied Squares.
     */
    protected List<Square> getOccupiedSquare(Board copyBoard, List<Square> playSquares) {
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
     * Swap the tiles that the player chooses with new tiles from the tileBag,
     * if there is still tiles in the tileBag.
     * @param chars the chars Array representation of the tiles player want to
     *              swap.
     */
    abstract void swapTray(String[] chars);

    /**
     * determine the move from the player's tile input.
     * @param moveTiles the String Array represents the move inputted by the player.
     * @return true if move valid, false otherwise.
     */
    abstract boolean makeMove(String[] moveTiles);

    /**
     * getter for current player.
     * @return the current player.
     */
    abstract Player getCurrentPlayer();

    /**
     * determine the winner player.
     * @return the winner (player).
     */
    abstract Player isWinner();
}

