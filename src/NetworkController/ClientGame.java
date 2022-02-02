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
        currentPlayer = 0;
        players = new ClientPlayer[playersNames.length];

        for (int id = 0; id < playersNames.length; id++) {
            ClientPlayer player = new ClientPlayer(playersNames[id], id);
            players[id] = player;
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
            Tile tile = determineTileFromServer(stringTile);
            players[currentPlayer].getTray().add(tile);
        }
    }


    public Board getBoard() { return board; }

    public ClientPlayer getCurrentPlayer() { return players[currentPlayer]; }

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
            Tile tile = determineTileFromInput(entry.getValue());
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
            String[] letterAndSquareCoordinate = clientMove.split("[.]");
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

    private Tile determineTileFromInput(String letter) {
        //To be re-implemented
        if (letter.contains("-")) {
           char blankLetter = letter.charAt(1);
           Tile tile = new Tile(blankLetter, 0);
           return tile;
        }
        return new Tile(letter.charAt(0), 0);
    }

    private Tile determineTileFromServer(String letter) {
        return new Tile(letter.charAt(0), 0);
    }



}

