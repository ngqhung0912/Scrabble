package Model;

import java.io.IOException;
import java.util.ArrayList;

public interface PlayerInterface {
    /**
     * Get the name of the player
     * @return player's name
     */
    String getName();

    /**
     * Return the tray with the current tiles belongs to the player
     * @return tray - The tray with the current tiles belongs to the player
     */
    ArrayList<Tile> getTray();

    void setTray(ArrayList<Tile> tray);

    /**
     * Get the current point of the player
     * @return
     */
    int getTotalPoints();

    void setFinalPoints(int finalPoints);
    /**
     *
     * @param point
     */
    void addPoints(int point);


   String[] determineMove() throws IOException;


}
