package View;

import Model.ClientGame;
import NetworkController.ServerGame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
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
        String trayMessage = "Tray: " +  (!tray.isEmpty() ? tray : "");
        showMessage("\n\n" + BoardConstructor.generateBoard(game.getBoard()) + "\n"
                + "Player: " + game.getCurrentPlayer().getName() + "\n"
                + trayMessage + "\n"
                + "Total Score: " + game.getCurrentPlayer().getTotalPoints() + "\n");
    }

    public void showMessage(String message) {
        System.out.println(message);
    }
    public String getString(String prompt) throws IOException {
        showMessage(prompt);
        return bf.readLine();
    }

    /**
     * prompt the user to input a boolean.
     * @param prompt
     * @return true if user enter Y, false otherwise.
     * @throws IOException
     */
    public boolean getBoolean(String prompt) throws IOException{
        showMessage(prompt);
        return bf.readLine().equals("Y") || bf.readLine().equals("y");
    }

    /**
     * Get the Ip address to connect to server
     * @return the IP address.
     * @throws IOException
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
                "\nThe winner is " + winner);
    }
}
