package LocalController;

import Model.LocalGame;
import Model.LocalPlayer;
import Model.Player;
import View.LocalView;

import java.io.IOException;
/**
 * Controls the local game.
 * @author Hung Nguyen
 */

public class LocalController {

    private static final String HELP = "Wrong input format. Correct input format: <numPlayers> <P1name> <P2name> ... ";
    private static int currentPlayer = 0;
    private static Player[] localPlayers;
    private static LocalGame localGame;
    private static LocalView textUI;

    public static void main(String[] args)  {
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
        }

        localGame = new LocalGame(playerName,textUI);

        loopingOverTheGame:
        do {
            textUI.update(localGame);
            String[] moves = new String[0];
            try {
                moves = localGame.getCurrentPlayer().determineMove();
            } catch (IOException e) {
                e.printStackTrace();
            }
            switch (moves[0]) {
                case "MOVE":
                    String[] moveTiles = new String[moves.length - 1];
                    System.arraycopy(moves, 1, moveTiles, 0, moves.length - 1);
                    localGame.makeMove(moveTiles);
                    break;
                case "PASS":
                    localGame.incrementPassCount();
                    textUI.showMessage("This is the " + localGame.getPassCount() + " consecutive pass move(s).");
                    break;
                case "SWAP":
                    String[] swapTilesChar = new String[moves.length - 1];
                    for (int i = 1; i < moves.length; i++) {
                        swapTilesChar[i - 1] = moves[i];
                    }
                    localGame.swapTray(swapTilesChar);
                    break;
                default:
                    textUI.showMessage("Wrong input format. skipping turn...");
                    break;
            }
            textUI.showMessage("current player before set is: " + currentPlayer);
            currentPlayer = localGame.setNextPlayer();
            textUI.showMessage("current player after set is: " + currentPlayer);
        } while (!localGame.gameOver());

        textUI.printResult(localGame);
    }
}
