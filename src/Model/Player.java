package Model;

import java.io.IOException;
import java.util.ArrayList;

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
     *
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * Return the tray with the current tiles belongs to the player
     * @return tray - The tray with the current tiles belongs to the player
     */
    public ArrayList<Tile>getTray() {
        return tray;
    }

    public void setTray(ArrayList<Tile> tray) {
        this.tray = tray;
    }

    /**
     * Get the current point of the player
     * @return
     */
    public int getTotalPoints() {
        return totalPoints;
    }

    public void setFinalPoints(int finalPoints) {
        this.totalPoints = finalPoints;
    }

    /**
     *
     * @param point
     */
    public void addPoints(int point){
        this.totalPoints += point;
    }

    public int compareTo(Player o) {
        return this.totalPoints - o.totalPoints;
    }

    public abstract String[] determineMove() throws IOException;


}
