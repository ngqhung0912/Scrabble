package Game;

import WordChecker.main.java.InMemoryScrabbleWordChecker;
import WordChecker.main.java.ScrabbleWordChecker;

import java.util.HashMap;

public class Controller  {
    private Board board;
    private int currentPlayer;
    private Player[] players;

    public Controller(int numPlayers, String[] playerList) {
        board = new Board();
        currentPlayer = 0;
        players = new Player[numPlayers];
//        for (int i = 0; i < numPlayers; i++) {
//            player = new Player(playerList[i]);
//        }

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
     * @param squares the squares contains the words to calculate point
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

    public boolean isEmptyTray() {
        return false;
    }

    public boolean isFullBoard() {
        return false;
    }

}
