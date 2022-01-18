package Game;
/**
 * @author Hung Nguyen
 * @version 0.1
 */

import TUI.BoardConstructor;
import WordChecker.main.java.InMemoryScrabbleWordChecker;
import WordChecker.main.java.ScrabbleWordChecker;

import java.util.*;


public class Game {
    private Board board;
    private int currentPlayer;
    private Player[] players;
    private List<Tile> tileBag;

    public Game(int numPlayers, String[] playerList) {
        board = new Board();
        currentPlayer = 0;
        players = new Player[numPlayers];
        tileBag = new TileGenerator().generateTiles();
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

    public Board getBoard() {
        return board;
    }



    public void setBoard(Board board) {
        this.board = board;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public Player[] getPlayers() {
        return players;
    }

    public void setPlayers(Player[] players) {
        this.players = players;
    }

    public List<Tile> getTileBag() {
        return tileBag;
    }

    public void setTileBag(List<Tile> tileBag) {
        this.tileBag = tileBag;
    }

    /**
     *
     * @param tiles the tiles that contain words to check
     * @return null if the word does not exist, or the description of that word if it exists.
     */
    public String wordChecker(Tile[] tiles) {
        String word = "";
        for (Tile tile : tiles) {
            word += tile.getLetter();
        }
        ScrabbleWordChecker checker = new InMemoryScrabbleWordChecker();
        return checker.isValidWord(word).toString();
    }

    /**
     *
     * @param squares the squares contain the words to calculate point
     * @return point.
     */
    public int calculatePoints(Square[] squares) {
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
            }
        }
        return  (doubleWord ? score * 2 : tripleWord ? score * 3 : score);
    }

    /**
     * add Tile to each player's tray
     * @param player which player to add tile to.
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

    public boolean isFullBoard() {
        return false;
    }

    public boolean gameOver() {
        return isEmptyTrayAndBag() || isFullBoard();
    }

    public void pass() {
        currentPlayer = currentPlayer == 0 ? 1 : currentPlayer == 1 ? 2 : currentPlayer == 2 ? 3 : 0;
    }


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
                    players[i].setFinalPoints(finalPoints);
                } else {
                    finalPoints = players[i].getTotalPoints() - finalDeduct.get(players[i]);
                    players[i].setFinalPoints(finalPoints);
                }
                //Find the final winner (up to this player)
                if (finalPoints > winner.getTotalPoints()){
                    winner = players[i];
                }
            }

        }
        return winner;
    }

    public void update(){
        System.out.println("\n\n" + BoardConstructor.generateBoard(this.board) + "\n"
        + "Player: " + players[this.currentPlayer].getName() + "\n"
        + "Tray: " + players[this.currentPlayer].getTray()+ "\n"
        + "New Total Point: " + players[this.currentPlayer].getTotalPoints()) ;
    }

    public void printResult(){
        Player winner = isWinner();
        System.out.println("Congratulation! Player " + winner.getName() + "has won!");
    }


}
