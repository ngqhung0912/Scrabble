package LocalGame;

import GameLogic.Board;
import GameLogic.Game;
import GameLogic.Player;
import GameLogic.Tile;
import TUI.LocalView;

import java.io.IOException;
import java.util.ArrayList;

public class LocalController {
    private static final String HELP = "Wrong input format. Correct input format: <numPlayers> <P1name> <P2name> ... ";
    private static int currentPlayer = 0;
    private static Player[] localPlayers;
    private static Game game;
    private static LocalView textUI;






    public static void main(String[] args) throws IOException {
        textUI = new LocalView();
        int numPlayers = 0;
        try {
            numPlayers = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println(HELP);
        }
        if (args.length > numPlayers + 1) {
            System.out.println(HELP);
        }


        String[] playerName = new String[numPlayers];

        localPlayers = new LocalPlayer[numPlayers];

        for (int i = 0; i < numPlayers; i++) {
            playerName[i] = args[i+1];
            localPlayers[i] = new LocalPlayer(playerName[i], i);
        }


        game = new Game(localPlayers, textUI);

        loopingOverTheGame: for (currentPlayer = 0; currentPlayer < localPlayers.length;) {
            game.update();
            String[] moves = localPlayers[currentPlayer].determineMove();
            switch (moves[0]) {
                case "MOVE":
                    String[] moveTiles = new String[moves.length-1];
                    for (int i = 1; i < moves.length; i++) moveTiles[i-1] = moves[i];
                    game.makeMove(moveTiles);
                    break;
                case "PASS":
                    game.incrementPassCount();
                    textUI.showMessage("This is the " + game.getPassCount() + " consecutive pass move(s).");
                    break;
                case "SWAP":
                    char[] swapTilesChar = new char[moves.length-1];
                    for (int i = 1; i < moves.length; i++) {
                        swapTilesChar[i-1] = moves[i].charAt(0);
                    }
                    game.swapTray(swapTilesChar);
                    break;
                default:
                    textUI.showMessage("Wrong input format. skipping turn...");
                    break;
            }
            currentPlayer = game.setCurrentPlayer();
            if (game.gameOver()) {
                break loopingOverTheGame;
            }
        }
        game.printResult();
    }
}
