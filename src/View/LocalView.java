package View;

import Model.LocalGame;

import java.io.*;

/**
 * This class implements the View interface to broadcast message to the Local players
 * @author Hung Nguyen
 * @version finale
 */
public class LocalView implements View {
     private static BufferedReader bf;
     public LocalView() {
         bf = new BufferedReader(new InputStreamReader(System.in));
     }
     public void showMessage(String message) {
         System.out.println(message);
     }
     public String getString(String prompt) throws IOException {
         showMessage(prompt);
         return bf.readLine();
     }

    /**
     * Prompt to get the user's input as char from terminal.
     * @param prompt
     * @return char input arguments.
     * @throws IOException
     */
    public char getChar(String prompt) throws IOException {
         showMessage(prompt);
         return bf.readLine().charAt(0);
     }

    /**
     * update the local game.
     * @param localGame
     */
    public void update(LocalGame localGame) {
        showMessage("\n\n" + BoardConstructor.generateBoard(localGame.getBoard()) + "\n"
                + "Player: " + localGame.getCurrentPlayer().getName() + "\n"
                + "Tray: " + localGame.getLetterFromTray(localGame.getCurrentPlayer().getTray()) + "\n"
                + "Total Score: " + localGame.getCurrentPlayer().getTotalPoints() + "\n"
                + "Current bag count: " + localGame.getTileBag().size());
    }

    /**
     * Print result whenever there is a winner for local game.
     * @param localGame
     */
    public void printResult(LocalGame localGame) {
        if (localGame.isWinner() != null) showMessage("Congratulations! Player " + localGame.isWinner().getName() + " has won!");
        else showMessage("It's a draw!");
    }


}
