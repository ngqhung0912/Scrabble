package View;

import Model.Game;

import java.io.IOException;

public abstract class View {
    public abstract void showMessage(String message);
    public abstract String getString(String prompt) throws IOException;
    public abstract char getChar(String prompt) throws IOException;

    public void update(Game game) {
        showMessage("\n\n" + BoardConstructor.generateBoard(game.getBoard()) + "\n"
                + "Player: " + game.getCurrentPlayer().getName() + "\n"
                + "Tray: " + game.getLetterFromTray(game.getCurrentPlayer().getTray()) + "\n"
                + "Total Score: " + game.getCurrentPlayer().getTotalPoints() + "\n"
                + "Current bag count: " + game.getTileBag().size());
    }


    public void printResult(Game game) {
        if (game.isWinner() != null) showMessage("Congratulations! Player " + game.isWinner().getName() + " has won!");
        else showMessage("It's a draw!");
    }
}
