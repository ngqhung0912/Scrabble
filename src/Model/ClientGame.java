package Model;
import java.util.*;

/**
 * @author Hung Nguyen, Nhat Tran
 * @version finale
 */

public class ClientGame {
    private Board board;
    private ClientPlayer[] players;
    private int currentPlayer;

    /**
     * Creates a new game to handle the logic on the client's side.
     * @param playersNames List of players' names
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

    /**
     * setter for the current player
     * @param currentPlayerName the index of the current player.
     */
    public void setCurrentPlayer(String currentPlayerName) {
        for(ClientPlayer player: players) {
            currentPlayer = (player.getName().equals(currentPlayerName)) ? player.getId() : currentPlayer;
        }
    }

    /**
     * put assigned Tiles to each player's tray
     * @param stringTileList The list of new tiles assigned to the currentPlayer
     */
    public void putTilesToTray(String[] stringTileList) {
        for(String stringTile: stringTileList) {
            Tile tile = determineTileFromString(stringTile);
            players[currentPlayer].getTray().add(tile);
        }
    }

    /**
     * remove tiles from the tray after the move has been validated by the server.
     * @param swapTiles tiles to remove, represents by a String Array.
     */

    public void removeTiles(String[] swapTiles){
        if (getCurrentPlayer().getTray().size() > 0) {
            for (String tileAndSquare: swapTiles) {
                String tile = Character.toString(tileAndSquare.charAt(0));
                getCurrentPlayer().getTray().remove(determineTileFromInput(tile));
            }
        }
    }

    /**
     * getter for board.
     * @return board.
     */
    public Board getBoard() { return board; }

    /**
     * getter for the current player.
     * @return current player.
     */

    public ClientPlayer getCurrentPlayer() { return players[currentPlayer]; }

    /**
     * Making move for the opponents (and also including the clients) after the move has been sent back
     * by the server.
     * @param move The moves made, represents by a string Array.
     * @param points the points of the move.
     */

    public void makeMove(String[] move, int points) {
        putTileToSquare(move);
        removeTiles(move);
        setPoints(points);
    }

    /**
     * put the tiles to their respective square as sent by the server.
     * @param move The moves made, represents by a string Array.
     */

    protected void putTileToSquare(String[] move) {
        LinkedHashMap<String, String > letterToSquare = new LinkedHashMap<>();
        for (int i = 0; i < move.length; i++) {
            String[] letterSquarePairs = move[i].split("");
            String charMove = "";
            String coordinateString = "";
            if (letterSquarePairs.toString().contains("-")) {
                charMove = letterSquarePairs[1];
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
            Tile tile = determineTileFromString(entry.getValue());
            String indexFormatSquare = entry.getKey();
            Square square = board.getSquare(Integer.parseInt(indexFormatSquare));
            square.setTile(tile);
        }
    }


//    private String determineCoordinateFromSquareInt( int location ) {
//        int xPosition = location % 15;
//        int yPosition = location / 15;
//        String[] alphaArr = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("");
//        String xCoordinate = alphaArr[xPosition];
//        return (xCoordinate + yPosition);
//    }

    /**
     * set the point sent by the server to their respective players.
     * @param points the point to set.
     */
    protected void setPoints(int points) {
        getCurrentPlayer().addPoints(points);
    }

    /**
     * format the client's move to send the server.
     * @param clientMoves the String Array represents the client's move.
     * @return a String to send to the server.
     */

    public String formatMoveToServer(String[] clientMoves) {
        String move = "";
        for (String clientMove: clientMoves) {
            String[] letterAndSquareCoordinate = clientMove.split("[.]");
            String letter = letterAndSquareCoordinate[0];
            String squarePosition = getStringSquareIndex(letterAndSquareCoordinate[1]);
            move += (letter+squarePosition + " ");
        }

        return move;
    }

    /**
     * get a square's index from its coordinate in form of (A1, A2, A3,... )
     * @param coordinate the coordinate of the square.
     * @return the index of the square.
     */

    public String getStringSquareIndex(String coordinate) {
        Square square = board.getSquare(coordinate);
        int xPosition = square.getxPosition();
        int yPosition = square.getyPosition();
        return Integer.toString((yPosition * 15) + xPosition);
    }

    /**
     * Getting a string array representation of a tray.
     *  @param tray player's tray.
     * @return return the String array represents the tray.
     *
     */
    public ArrayList<String> getLetterFromTray(ArrayList<Tile> tray) {
        ArrayList<String> letterTray = new ArrayList<>();
        for (Tile tile : tray) {
            letterTray.add(Character.toString(tile.getLetter()));
        }
        return letterTray;
    }

    /**
     * get a player's tile corresponds to the client's input.
     * @param letter client's input
     * @return a tile represents that letter if it exists on the player's tray, null otherwise.
     */
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

    /**
     * Determine the tile from the letter String sent by the server, then create a dummy tile without a
     * score in it.
     * @param letter the letter sent by the server.
     * @return a new tile containing the letter with 0 points.
     */

    private Tile determineTileFromString(String letter) {
        return new Tile(letter.charAt(0), 0);
    }

}

