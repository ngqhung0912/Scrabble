package NetworkController;

import Model.Board;
import Model.Player;
import Model.Tile;
import Model.TileGenerator;

import java.util.*;

public class ServerGame {

    private Server server;
    private List<Tile> tileBag;
    private Board board;
    private int passCount;
    private int currentPlayer;
    private int numPlayer;
    private HashMap<Integer, ArrayList<Tile>> playersTiles;
    private HashMap<Integer, Integer> playersScoreCount;
    private ServerPlayer[] serverPlayers;


    /**
     * Creates a new game
     *
     * @param server The game server
     */
    public ServerGame(Server server) {
        this.server = server;
        tileBag = new TileGenerator().generateTiles();
        board = new Board();
        numPlayer = server.getClients().size();
        playersTiles = new HashMap<>();
        playersScoreCount = new HashMap<>();
        serverPlayers = new ServerPlayer[numPlayer];

        for (int i = 0; i < numPlayer; i++) {
            ClientHandler client = server.getClients().get(i);
            serverPlayers[i] = new ServerPlayer(client.toString(), client.getClientId());
        }
    }

    private void removeTile(String character) {
        if (character != null) {
            ArrayList<Tile> tray = serverPlayers[currentPlayer].getTray();
            for (Tile tile : tray) {
                if (Character.toString(tile.getLetter()).equals(character)) {
                    tray.remove(tile);
                }
            }
        }
    }

    protected String sendNewTiles(String[] usedTilesChar) {
        int bagSize = tileBag.size();
        int missingTiles = usedTilesChar.length;
        for (String usedTileChar : usedTilesChar) {
            removeTile(usedTileChar);
        }
        missingTiles = bagSize < missingTiles ? bagSize : missingTiles;

        String tileSend = "";
        for (int i = 0; i < missingTiles; i++) {
            bagSize = tileBag.size();
            int j = new Random().nextInt(bagSize);
            Tile tile = tileBag.get(j);
            tileBag.remove(tile);
            tileSend += tile.getLetter() + ProtocolMessages.AS;
            serverPlayers[currentPlayer].getTray().add(tile);
        }

        return tileSend;
    }

    private boolean isFullBoard() {
        for (int i = 0; i < board.SIZE; i++) {
            if (!board.getSquare(i).hasTile()) return false;
        }
        return true;
    }

    protected void updateScore(int score) {
        serverPlayers[currentPlayer].addPoints(score);
    }

    protected void setNextPlayer() {
        currentPlayer = currentPlayer < 3 ? currentPlayer++ : 0;
    }

    public void resetPassCount() {
        this.passCount = 0;
    }

    public void incrementPassCount() {
        this.passCount++;
    }

    private boolean isEmptyTrayAndBag() {
        return serverPlayers[currentPlayer].getTray().isEmpty() && tileBag.isEmpty();
    }

    public boolean gameOver() {
        return isEmptyTrayAndBag() || passCount > 5;
    }

    public ServerPlayer isWinner() {
        Map<ServerPlayer, Integer> finalDeduct = new HashMap<>();

        ArrayList<Tile> tilesLeft = null;

        //Create a map of players with their deduct points
        for (ServerPlayer serverPlayer : serverPlayers) {
            tilesLeft = serverPlayer.getTray();
            int deductPoints = 0;
            for (Tile tile : tilesLeft) {
                deductPoints += tile.getPoint();
            }
            finalDeduct.put(serverPlayer, deductPoints);
        }


        for (int i = 0; i < serverPlayers.length; i++) {
            int finalPoints = 0;
            if (tilesLeft.size() == 0) {
                int totalDeductPoints = finalDeduct.get(serverPlayers[0]) + finalDeduct.get(serverPlayers[1])
                        + finalDeduct.get(serverPlayers[2]) + finalDeduct.get(serverPlayers[3]);
                finalPoints = serverPlayers[i].getTotalPoints() + totalDeductPoints;
            } else {
                finalPoints = serverPlayers[i].getTotalPoints() - finalDeduct.get(serverPlayers[i]);
            }
            serverPlayers[i].setFinalPoints(finalPoints);
        }

        ServerPlayer winner = serverPlayers[0];
        for (int i = 1; i < serverPlayers.length; ) {
            int compare = winner.compareTo(serverPlayers[i]);
            if (compare < 0) {
                winner = serverPlayers[i];
            } else if (compare == 0) {
                if (winner.getTotalPoints() + finalDeduct.get(winner) < serverPlayers[i].getTotalPoints() + finalDeduct.get(serverPlayers[i])) {
                    winner = serverPlayers[i];
                } else if (winner.getTotalPoints() + finalDeduct.get(winner) == serverPlayers[i].getTotalPoints() + finalDeduct.get(serverPlayers[i])) {
                    return null;
                }
            }
            i++;
        }
        return winner;
    }

    protected void doMove(String playerMove) {
        String[] moves = playerMove.split(ProtocolMessages.AS);
//        for (String move : moves) {
//            String[] temp = move.split("");


    }

    protected void doPass(String move) {

    }

    protected int getCurrentPlayerID() {
        return currentPlayer;
    }

    protected ServerPlayer getCurrentPlayer() { return serverPlayers[currentPlayer];}

    protected ServerPlayer getPlayerByID(int id) {
        return serverPlayers[id];
    }

}
