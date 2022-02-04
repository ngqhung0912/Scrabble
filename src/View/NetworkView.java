package View;

import Model.ClientGame;
import Model.ServerGame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * The View for network game.
 * @author Hung Nguyen
 * @version finale
 */
public class NetworkView implements View {

   private static BufferedReader bf;

    public NetworkView() {

        bf = new BufferedReader(new InputStreamReader(System.in));
    }

    /**
     * Update for the server game.
     * @param game serverGame
     */
    public void update(ServerGame game) {
        showMessage("\n\n" + BoardConstructor.generateBoard(game.getBoard()) + "\n"
                + "Player: " + game.getCurrentPlayer().getName() + "\n"
                + "Tray: " + game.getLetterFromTray(game.getCurrentPlayer().getTray()) + "\n"
                + "Total Score: " + game.getCurrentPlayer().getTotalPoints() + "\n"
                + "Current bag count: " + game.getTileBag().size());
    }

    /**
     * Update for the client game.
     * @param game clientGame
     */
    public void update(ClientGame game) {
        ArrayList<String> tray = game.getLetterFromTray(game.getCurrentPlayer().getTray());
        String trayMessage =  (!tray.isEmpty() ? "Tray: " + tray  +  "\n" : "");
        showMessage("\n\n" + BoardConstructor.generateBoard(game.getBoard()) + "\n"
                + "Player: " + game.getCurrentPlayer().getName() + "\n"
                + trayMessage
                + "Total Score: " + game.getCurrentPlayer().getTotalPoints() + "\n");
    }

    public void showMessage(String message) {
        System.out.println(message);
    }

    /**
     * prompt the user to input a String
     * @param prompt the prompt to ask the user.
     * @return the user inputs.
     * @throws IOException when there is input/output error.
     */
    public String getString(String prompt) throws IOException {
        showMessage(prompt);
        return bf.readLine();
    }

    /**
     * prompt the user to input a boolean.
     * @param prompt the prompt to ask the user.
     * @return true if user enter Y, false otherwise.
     * @throws IOException when there is input/output error.
     */
    public boolean getBoolean(String prompt) throws IOException{
        showMessage(prompt);
        return bf.readLine().equals("Y") || bf.readLine().equals("y");
    }

    /**
     * Get the Ip address to connect to server
     * @return the IP address.
     * @throws IOException when there is input/output error.
     */
    public InetAddress getIp() throws IOException {
        InetAddress addr = null;
        while (addr == null) {
            String host = getString("Insert IP address");
            try {
                addr = InetAddress.getByName(host);
            } catch (UnknownHostException e) {
                showMessage("Invalid IP address");
            }
        }
        return addr;
    }

    /**
     * Print the result for client player
     * @param winner name of the winner
     */
    public void printResult(String winner) {
        showMessage("---GAMEOVER---" + "\n********************" +
                "\nThe winner is " + winner + ". Better luck next time!");
    }

    public void congratulations() {
        showMessage("---GAMEOVER---" + "\n********************" +
                "\nCongratulations! You are the winner!!!!");

    }
}
