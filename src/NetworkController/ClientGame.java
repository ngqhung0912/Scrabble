package NetworkController;

/**
 * This class implements the basic game functions of Scrabble.
 * @author Hung Nguyen, Nhat Tran
 * @version 0.1
 */

import Model.*;
import View.View;
import WordChecker.main.java.InMemoryScrabbleWordChecker;
import WordChecker.main.java.ScrabbleWordChecker;

import java.io.IOException;
import java.util.*;

/**
 * @author Hung Nguyen, Nhat Tran
 * @version 0.1
 */

public class ClientGame {
    private Board board;
    private ClientPlayer[] players;
    private List<Tile> tileBag;
    private ArrayList<String> usedWords;
    private ScrabbleWordChecker checker;
    private List<Square> occupiedSquares;
    private List<Square> nextValidSquares;
    private int numPlayer;
    private int currentPlayer;


    /**
     * Creates a new game
     * @param playersNames List of players' names
     * @requires numPlayers > 0 && numPlayers < 5
     * @ensures creates a new board and all squares are empty
     * @ensures A new tileBag is created for the game
     * @ensures player.getTray() is not null
     * @invariant players.length == numPlayers
     */

    public ClientGame(String[] playersNames) {
        board = new Board();
        this.numPlayer = playersNames.length;
        tileBag = new TileGenerator().generateTiles();
        usedWords = new ArrayList<>();
        checker = new InMemoryScrabbleWordChecker();
        occupiedSquares = new ArrayList<>();
        nextValidSquares = new ArrayList<>();
        currentPlayer = 0;
        players = new ClientPlayer[playersNames.length];

        for (int id = 0; id < playersNames.length; id++) {
            ClientPlayer player = new ClientPlayer(playersNames[id], id);
            players[id] = player;
        }

        for (ClientPlayer player : players) {
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

    public void setCurrentPlayer(String currentPlayerName) {
        for(ClientPlayer player: players) {
            currentPlayer = (player.getName().equals(currentPlayerName)) ? player.getId() : currentPlayer;
        }
    }

    /**
     * @requires player is not null
     * put assigned Tiles to each player's tray
     * @param stringTileList The list of new tiles assigned to the currentPlayer
     *
     */
    protected void putTilesToTray(String[] stringTileList) {
        for(String stringTile: stringTileList) {
            Tile tile = determineTileFromString(stringTile);
            players[currentPlayer].getTray().add(tile);
        }
    }

    /**
     * To pass the play if the player decides to.
     */
    //public int setCurrentPlayer() { return currentPlayer < numPlayer-1 ? currentPlayer++ : 0; }

    public Board getBoard() { return board; }

    public ClientPlayer getCurrentPlayer() { return players[currentPlayer];}

    public void opponentMakeMove (String[] move, int points) {
        putTileToSquare(move);
        setOpponentPoints(points);
    }

    protected void putTileToSquare(String[] move) {
        //Map<String, String> letterToSquare = mapLetterToSquare(move);
        Map<String, String> letterToSquare = new HashMap<>();
        for (String letterAndSquareIndex: move) {
            int length = letterAndSquareIndex.length();
            String letter = (letterAndSquareIndex.charAt(0) == '-') ? Character.toString(letterAndSquareIndex.charAt(1)) :
                    Character.toString(letterAndSquareIndex.charAt(0));
            int index = (length == 2) ? letterAndSquareIndex.charAt(1)
                    : (length == 3) ? letterAndSquareIndex.charAt(1) + letterAndSquareIndex.charAt(2)
                    : letterAndSquareIndex.charAt(1) + letterAndSquareIndex.charAt(2) + letterAndSquareIndex.charAt(3);

            String indexSquare = Integer.toString(index);
            letterToSquare.put(indexSquare, letter);
        }

        for(Map.Entry<String, String> entry: letterToSquare.entrySet()) {
            Tile tile = determineTileFromString(entry.getValue());
            String indexFormatSquare = entry.getKey();
            Square square = board.getSquare(Integer.parseInt(indexFormatSquare));
            square.setTile(tile);
        }
    }

    protected void setOpponentPoints (int points) {
        getCurrentPlayer().addPoints(points);
    }

    public String sendMoveToServer(String[] clientMoves) {
        String move = "";
        for (String clientMove: clientMoves) {
            String[] letterAndSquareCoordinate = clientMove.split(".");
            String letter = letterAndSquareCoordinate[0];
            String squarePosition = getStringSquareIndex(letterAndSquareCoordinate[1]);
            move += (letter+squarePosition + " ");
        }

        return move;
    }

    public String getStringSquareIndex(String coordinate) {
        Square square = board.getSquare(coordinate);
        int xPosition = square.getxPosition();
        int yPosition = square.getyPosition();
        return Integer.toString((yPosition * 15) + xPosition);
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


//    private LinkedHashMap<String, String> mapLetterToSquare(String[] move){
//        LinkedHashMap<String , String > letterToSquare = new LinkedHashMap<>();
//        for (int i = 0; i < move.length; i++) {
//            String[] letterSquarePair = move[i].split("-");
//            if (letterSquarePair.length < 2) {
//                return null;
//            }
//            String[] coordinate = letterSquarePair[1].split("");
//            if (coordinate.length > 3) {
//                System.out.println("Coordinate's length should not be greater than 3.");
//                return null;
//            }
//            for (int j = 1; j < coordinate.length; j++) {
//                try {
//                    Integer.parseInt(coordinate[j]);
//                } catch (NumberFormatException e) {
//                    System.out.println("Wrong input format. Move should be D-H7 O-H8 G-H9 and so on.");
//                    return null;
//                }
//            }
//            letterToSquare.put(letterSquarePair[1], letterSquarePair[0]);
//        }
//        return letterToSquare;
//    }

    private Tile determineTileFromString(String letter) {
        //To be re-implemented
        if (letter.contains("-")) {
           char blankLetter = letter.charAt(1);
           Tile tile = new Tile(blankLetter, 0);
           return tile;
        }

        return new Tile(letter.charAt(0), 0);
    }

//    private  ArrayList<ArrayList<Square>> determinePossibleWordCombinations(ArrayList<Square> inputWord, String direction, Board copyBoard) {
//        Square startingPosition = inputWord.get(0);
//
//        ArrayList<ArrayList<Square>> wordCombinations = new ArrayList<>();
//        ArrayList<Square> initialWord = new ArrayList<>();
//        Square otherSideStartingPosition = null;
//
//        if (startingPosition.getxPosition() > 0 && startingPosition.getyPosition() > 0 ) {
//            if (inputWord.size() > 1) otherSideStartingPosition = direction.equals("H") ? copyBoard.getSquareLeft(startingPosition)
//                    : copyBoard.getSquareAbove(startingPosition);
//            else {
//                if (copyBoard.getSquareLeft(startingPosition) == null) {
//                    otherSideStartingPosition = copyBoard.getSquareAbove(startingPosition) == null ? null :
//                            copyBoard.getSquareAbove(startingPosition);
//                } else {
//                    otherSideStartingPosition = copyBoard.getSquareLeft(startingPosition);
//                }
//            }
//        }
//        traversingRightandBelow : while (startingPosition != null
//                && startingPosition.getxPosition() < 15
//                && startingPosition.getyPosition() < 15
//                && startingPosition.hasTile()) {
//
//            Square currentPosition = startingPosition;
//            initialWord.add(startingPosition);
//
//            for (Square square : occupiedSquares) {
//                if (currentPosition.getLocation().equals(square.getLocation())) {
//                    startingPosition = direction.equals("H") ? copyBoard.getSquareRight(startingPosition)
//                            : copyBoard.getSquareBelow(startingPosition);
//                    continue traversingRightandBelow;
//                }
//            }
//
//
//            if (direction.equals("H")) {
//                ArrayList<Square> verticalWord = new ArrayList<>();
//                verticalWord.add(currentPosition);
//                Square nextAbovePosition = currentPosition;
//                Square nextBelowPosition = currentPosition;
//
//
//                AboveWhileH: while(nextAbovePosition.getyPosition() > 0 && copyBoard.getSquareAbove(nextAbovePosition).hasTile()) {
//                    verticalWord.add(0,copyBoard.getSquareAbove(nextAbovePosition));
//                    nextAbovePosition = copyBoard.getSquareAbove(nextAbovePosition);
//                }
//
//                belowWhileH: while(nextBelowPosition.getyPosition() < 15 && copyBoard.getSquareBelow(nextBelowPosition).hasTile()) {
//                    verticalWord.add(copyBoard.getSquareBelow(nextBelowPosition));
//                    nextBelowPosition = copyBoard.getSquareBelow(nextBelowPosition);
//                }
//                currentPosition = copyBoard.getSquareRight(currentPosition);
//
//
//                if (verticalWord.size() > 1) wordCombinations.add(verticalWord);
//            }
//            else if (direction.equals("V")) {
//                ArrayList<Square> horizontalWord = new ArrayList<>();
//                horizontalWord.add(currentPosition);
//
//                Square nextLeftPosition = currentPosition;
//                Square nextRightPosition = currentPosition;
//
//                leftWhileV: while(nextLeftPosition.getxPosition() > 0 && copyBoard.getSquareLeft(nextLeftPosition).hasTile()) {
//                    horizontalWord.add(0,copyBoard.getSquareLeft(nextLeftPosition));
//                    nextLeftPosition = copyBoard.getSquareLeft(nextLeftPosition);
//                }
//
//                rightWhileV: while(nextRightPosition.getxPosition() < 15 && copyBoard.getSquareRight(nextRightPosition).hasTile()) {
//                    horizontalWord.add(copyBoard.getSquareRight(nextRightPosition));
//                    nextRightPosition = copyBoard.getSquareRight(nextRightPosition);
//                }
//                currentPosition = copyBoard.getSquareBelow(currentPosition);
//
//                if (horizontalWord.size() > 1) wordCombinations.add(horizontalWord);
//            }
//
//            startingPosition = currentPosition;
//
//
//        }
//        traversingLeftandAbove: while (otherSideStartingPosition != null
//                && otherSideStartingPosition.getxPosition() >= 0
//                && otherSideStartingPosition.getyPosition() >= 0
//                && otherSideStartingPosition.hasTile()) {
//            Square currentPosition = otherSideStartingPosition;
//            if (direction.equals("H")) {
//                currentPosition = copyBoard.getSquareLeft(currentPosition);
//                initialWord.add(0,otherSideStartingPosition);
//                otherSideStartingPosition = currentPosition;
//            }
//            else if (direction.equals("V")) {
//                currentPosition = copyBoard.getSquareAbove(currentPosition);
//                initialWord.add(0,otherSideStartingPosition);
//                otherSideStartingPosition = currentPosition;
//            }
////            if (startingPosition == null) {
////                break traversingLeftandAbove;
////            }
//        }
//
//        for (Square square : inputWord) {
//            if (!initialWord.contains(square)) {
//                return null;
//            }
//        }
//
//        if (initialWord.size() > 1) wordCombinations.add(initialWord);
//        return wordCombinations;
//    }

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

//    private static List<Square> getNextValidSquares(List<Square> playSquares, String direction, Board copyBoard) {
//        List<Square> occupiedSquares = new ArrayList<>();
//        List<Square> nextValidSquares = new ArrayList<>();
//
//        for (int i = 0; i < (copyBoard.SIZE * copyBoard.SIZE) ; i++) {
//            if(copyBoard.getSquare(i).hasTile()) occupiedSquares.add(copyBoard.getSquare(i));
//        }
//
//        for (Square square : playSquares) {
//            occupiedSquares.remove(square);
//        }
//        if (occupiedSquares.size() == 0) return null;
//        for (int i = 0; i < playSquares.size(); i++) {
//            Square currentSquare = playSquares.get(i);
//            try {
//                if (currentSquare.getxPosition() >= 0 && currentSquare.getxPosition() < 15 &&
//                        currentSquare.getyPosition() >= 0 && currentSquare.getyPosition() < 15) {
//                    if(direction.equals("H")) {
//                        if (i == 0 && copyBoard.getSquareLeft(currentSquare).getTile() == null)
//                            nextValidSquares.add(copyBoard.getSquareLeft(currentSquare));
//
//                        else if (i == playSquares.size() -1 && copyBoard.getSquareRight(currentSquare).getTile() == null)
//                            nextValidSquares.add(copyBoard.getSquareRight(currentSquare));
//
//                        if (copyBoard.getSquareAbove(currentSquare).getTile() == null)
//                            nextValidSquares.add(copyBoard.getSquareAbove(currentSquare));
//                        if (copyBoard.getSquareBelow(currentSquare).getTile() == null)
//                            nextValidSquares.add(copyBoard.getSquareBelow(currentSquare));
//                    }
//                    else {
//                        if (i == 0 && copyBoard.getSquareAbove(currentSquare).getTile() == null)
//                            nextValidSquares.add(copyBoard.getSquareAbove(currentSquare));
//
//                        else if (i == playSquares.size() -1 &&  copyBoard.getSquareBelow(currentSquare).getTile() == null)
//                            nextValidSquares.add(copyBoard.getSquareBelow(currentSquare));
//
//                        if (copyBoard.getSquareRight(currentSquare).getTile() == null)
//                            nextValidSquares.add(copyBoard.getSquareRight(currentSquare));
//                        if (copyBoard.getSquareLeft(currentSquare).getTile() == null)
//                            nextValidSquares.add(copyBoard.getSquareLeft(currentSquare));
//                    }
//
//                }
//
//            } catch (NullPointerException e) {
//                continue;
//            }
//
//        }
//        return nextValidSquares;
//
//    }

//    private boolean isValidPlacement(List<Square> playSquares, String direction, Board copyBoard){
//        nextValidSquares = getNextValidSquares(playSquares, direction, copyBoard);
//        Square centralSquare = copyBoard.getSquare("H7");
//        if (usedWords.size() == 0 && playSquares.contains(centralSquare)) return true;
//        for (Square playSquare: playSquares){
//            if (usedWords.size() != 0) {
//                for (Square validSquare : nextValidSquares) {
//                    if (validSquare.getLocation().equals(playSquare.getLocation())) return true;
//                }
//            }
//        }
////        UI.showMessage("Invalid placement. In the first round, player has to put one tile on H7 square.\n" +
////                "During the remaining game, at least one tile placed by the player has to connect to one of the tiles on the board.");
//        return false;
//    }
}

