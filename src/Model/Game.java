package Model;
/**
 * This class implements the basic game functions of Scrabble.
 * @author Hung Nguyen, Nhat Tran
 * @version 0.1
 */

import View.View;
import WordChecker.main.java.InMemoryScrabbleWordChecker;
import WordChecker.main.java.ScrabbleWordChecker;

import java.io.IOException;
import java.util.*;

/**
 * @author Hung Nguyen, Nhat Tran
 * @version 0.1
 */

public class Game {
    private Board board;
    private Player[] players;
    private List<Tile> tileBag;
    private ArrayList<String> usedWords;
    private ScrabbleWordChecker checker;
    private List<Square> occupiedSquares;
    private List<Square> nextValidSquares;
    private int numPlayer;
    private int currentPlayer;
    private int passCount;


    /**
     * Creates a new game
     * @param players number of players
     * @requires numPlayers > 0 && numPlayers < 5
     * @ensures creates a new board and all squares are empty
     * @ensures A new tileBag is created for the game
     * @ensures player.getTray() is not null
     * @invariant players.length == numPlayers
     */

    public Game(Player[] players) {
        board = new Board();
        this.numPlayer = players.length;
        this.players = players;
        tileBag = new TileGenerator().generateTiles();
        usedWords = new ArrayList<>();
        checker = new InMemoryScrabbleWordChecker();
        occupiedSquares = new ArrayList<>();
        nextValidSquares = new ArrayList<>();
        currentPlayer = 0;
        passCount = 0;

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


    public void resetPassCount() {
        this.passCount = 0;
    }

    public void incrementPassCount() {
        this.passCount++;
    }

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
     *
     */
    private void addTileToTray(Player player) {
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
    private boolean isEmptyTrayAndBag() {
        return players[currentPlayer].getTray().isEmpty() && tileBag.isEmpty();
    }

    /**
     * To be implemented:
     * @return true if there is no more space to put a meaningful word in, false otherwise.
     */


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
    public int setCurrentPlayer() { return currentPlayer < numPlayer-1 ? currentPlayer++ : 0; }

    /**
     * Determine who is the winner
     * @return the winner.
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

    public Board getBoard() { return board; }

    public List<Tile> getTileBag() { return tileBag; };

    public Player getCurrentPlayer() { return players[currentPlayer]; }

    public void makeMove(String[] moveTiles) {
        Board validBoard = isValidMove(mapLetterToSquare(moveTiles));
        if (validBoard != null) {
            board = validBoard.clone();
        }
        resetPassCount();
    }

    private LinkedHashMap<String, String> mapLetterToSquare(String[] move){
        LinkedHashMap<String , String > letterToSquare = new LinkedHashMap<>();
        for (int i = 0; i < move.length; i++) {
            String[] letterSquarePair = move[i].split("-");
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

    private Tile determineTileFromChar(char character) {
        ArrayList<Tile> tray = players[currentPlayer].getTray();
        for (Tile tile: tray){
            if (character == '#' && character == tile.getLetter() ) {
                String prompt = "Please choose one of the letters below:\n"
                        + "A B C D E F G H I J K L M N O P Q R S T U V W X Y Z\n";
                try{
//                    char input = UI.getChar(prompt);
//                    tile.setLetter(input);
                    return tile;

                } catch (IllegalArgumentException e) {
                    return null;
//                } catch (IOException e) {
//                    return null;
                }
            }
            else if (tile.getLetter() == character) {
                return tile;
            }
        }
        return null;
    }

    private boolean isFullBoard() {
        for (int i = 0; i < board.SIZE; i++) {
            if (!board.getSquare(i).hasTile()) return false;
        }
        return true;
    }

    private String getWordFromSquareList(ArrayList<Square> squares) {
        String word = "";
        for (Square square : squares)  word +=square.getTile().getLetter();
        return word;
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
//            UI.showMessage("Wrong input format.");
            return null;
        }
        int turnScore = 0;
        for (ArrayList<Square> wordCombination : wordCombinations) {
            String validWord = wordChecker(wordCombination);
            if (validWord == null) {
//                UI.showMessage("The word " + getWordFromSquareList(wordCombination) + " is invalid. Skipping your turn...");
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

    private static List<Square> getNextValidSquares(List<Square> playSquares, String direction, Board copyBoard) {
        List<Square> occupiedSquares = new ArrayList<>();
        List<Square> nextValidSquares = new ArrayList<>();

        for (int i = 0; i < (copyBoard.SIZE * copyBoard.SIZE) ; i++) {
            if(copyBoard.getSquare(i).hasTile()) occupiedSquares.add(copyBoard.getSquare(i));
        }

        for (Square square : playSquares) {
            occupiedSquares.remove(square);
        }
        if (occupiedSquares.size() == 0) return null;
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

    private boolean isValidPlacement(List<Square> playSquares, String direction, Board copyBoard){
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
//        UI.showMessage("Invalid placement. In the first round, player has to put one tile on H7 square.\n" +
//                "During the remaining game, at least one tile placed by the player has to connect to one of the tiles on the board.");
        return false;
    }
}
