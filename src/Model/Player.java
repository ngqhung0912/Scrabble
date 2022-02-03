package Model;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Hung Nguyen
 * @version finale
 */
public abstract class Player implements Comparable<Player>, PlayerInterface {
    protected String name;
    protected int id;
    protected int totalPoints;
    protected ArrayList<Tile> tray;

    public Player(String name, int id){
        this.name = name;
        totalPoints = 0;
        this.id = id;
        this.tray = new ArrayList<>();
    }


    /**
     * Get the name of the player
     * @return player's name
     */
    public String getName() {
        return name;
    }

    /**
     * getter for the player's ID.
     * @return player's ID.
     */
    public int getId() {
        return id;
    }

    /**
     * getter for tray.
     * @return tray - The tray with the current tiles belongs to the player
     */
    public ArrayList<Tile>getTray() {
        return tray;
    }

    /**
     * setter for tray
     * @param tray the tray to be set.
     */

    public void setTray(ArrayList<Tile> tray) {
        this.tray = tray;
    }

    /**
     * getter for total points of the player.
     * @return current point of the player
     */
    public int getTotalPoints() {
        return totalPoints;
    }

    /**
     * setter for the final points of the player, after deduction at endgame.
     * @param finalPoints to set.
     */

    public void setFinalPoints(int finalPoints) {
        this.totalPoints = finalPoints;
    }

    /**
     * Add the point to the player's point, after each move.
     * @param point to add.
     */
    public void addPoints(int point){
        this.totalPoints += point;
    }

    /**
     * comparable Method to compare the final score with other player.
     * @param o other player to compare.
     * @return this player's point minus the other player's point.
     */

    public int compareTo(Player o) {
        return this.totalPoints - o.totalPoints;
    }

    /**
     * abstract method to determine Move of each player.
     * @return Move in String Array format.
     * @throws IOException when the buffered reader has a problem.
     */

    public abstract String[] determineMove() throws IOException;


}
