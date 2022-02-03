package NetworkController;

/**
 * This class implements the basic game functions of Scrabble.
 * @author Hung Nguyen, Nhat Tran
 * @version 0.1
 */

import Model.*;

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

    protected void removeTiles(String[] swapTiles){
        if (getCurrentPlayer().getTray().size() > 0) {
            for (String tileAndSquare: swapTiles) {
                String tile = Character.toString(tileAndSquare.charAt(0));
                getCurrentPlayer().getTray().remove(determineTileFromInput(tile));
            }
        }
    }


    public Board getBoard() { return board; }

    public ClientPlayer getCurrentPlayer() { return players[currentPlayer]; }

    public void opponentMakeMove (String[] move, int points) {
        putTileToSquare(move);
        removeTiles(move);
        setOpponentPoints(points);
    }

    protected void putTileToSquare(String[] move) {
        LinkedHashMap<String, String > letterToSquare = new LinkedHashMap<>();
        for (int i = 0; i < move.length; i++) {
            String[] letterSquarePairs = move[i].split("");
            String charMove = "";
            String coordinateString = "";
            if (letterSquarePairs.toString().contains("-")) {
                charMove = letterSquarePairs[0] + letterSquarePairs[1];
                for (int j = 2; j < letterSquarePairs.length; j++ ) {
                    coordinateString += letterSquarePairs[j];
                }
            } else {
                charMove = letterSquarePairs[0];
                for (int j = 1; j < letterSquarePairs.length; j++ ) {
                    coordinateString += letterSquarePairs[j];
                }
            }
            letterToSquare.put(coordinateString,charMove);
        }

        for(Map.Entry<String, String> entry: letterToSquare.entrySet()) {
            Tile tile = determineTileFromInput(entry.getValue());
            String indexFormatSquare = entry.getKey();
            Square square = board.getSquare(Integer.parseInt(indexFormatSquare));
            square.setTile(tile);
        }
    }
    private String determineCoordinateFromSquareInt( int location ) {
        int xPosition = location % 15;
        int yPosition = location / 15;
        String[] alphaArr = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("");
        String xCoordinate = alphaArr[xPosition];
        return (xCoordinate + yPosition);
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
        ArrayList<String> letterTray = new ArrayList<>();
        for (Tile tile : tray) {
            letterTray.add(Character.toString(tile.getLetter()));
        }
        return letterTray;
    }


    private Tile determineTileFromInput(String letter) {
        ArrayList<Tile> tray = getCurrentPlayer().getTray();
        for (Tile tile: tray){
            if (letter.equals("-") && letter.equals(Character.toString(tile.getLetter()))) {
                return tile;
            }
            else if (letter.equals(Character.toString(tile.getLetter()))) {
                return tile;
            }
        }
        return null;
    }

    private Tile determineTileFromServer(String letter) {
        return new Tile(letter.charAt(0), 0);
    }

}

