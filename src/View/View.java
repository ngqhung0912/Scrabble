package View;

import Model.LocalGame;
import NetworkController.ClientGame;
import NetworkController.ServerGame;

import java.io.IOException;

public abstract class View {
    public abstract void showMessage(String message);
    public abstract String getString(String prompt) throws IOException;
    public abstract char getChar(String prompt) throws IOException;

    public void update(LocalGame localGame) {
        showMessage("\n\n" + BoardConstructor.generateBoard(localGame.getBoard()) + "\n"
                + "Player: " + localGame.getCurrentPlayer().getName() + "\n"
                + "Tray: " + localGame.getLetterFromTray(localGame.getCurrentPlayer().getTray()) + "\n"
                + "Total Score: " + localGame.getCurrentPlayer().getTotalPoints() + "\n"
                + "Current bag count: " + localGame.getTileBag().size());
    }

    public void update(ServerGame game) {
        showMessage("\n\n" + BoardConstructor.generateBoard(game.getBoard()) + "\n"
                + "Player: " + game.getCurrentPlayer().getName() + "\n"
                + "Tray: " + game.getLetterFromTray(game.getCurrentPlayer().getTray()) + "\n"
                + "Total Score: " + game.getCurrentPlayer().getTotalPoints() + "\n"
                + "Current bag count: " + game.getTileBag().size());
    }


    public void update(ClientGame game) {
        showMessage("\n\n" + BoardConstructor.generateBoard(game.getBoard()) + "\n"
                + "Player: " + game.getCurrentPlayer().getName() + "\n"
                + "Tray: " + game.getLetterFromTray(game.getCurrentPlayer().getTray()) + "\n"
                + "Total Score: " + game.getCurrentPlayer().getTotalPoints() + "\n");
    }


    public void printResult(LocalGame localGame) {
        if (localGame.isWinner() != null) showMessage("Congratulations! Player " + localGame.isWinner().getName() + " has won!");
        else showMessage("It's a draw!");
    }

    public void printResult(ServerGame game) {
        if (game.isWinner() != null) showMessage("Congratulations! Player " + game.isWinner().getName() + " has won!");
        else showMessage("It's a draw!");
    }


    public void printResult(ClientGame clientGame, String winner) {
        showMessage("---GAMEOVER---" + "\n********************" +
                "\nThe winner is " + winner);
    }
}
