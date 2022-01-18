package Game;

import java.util.ArrayList;

/**
 * author Nhat Tran
 */
public class Player {

     // -- Instance variables -----------------------------------------

     private String name;
     private int totalPoints;
     private ArrayList<Tile> tray;

     // -- Constructors -----------------------------------------------

     public Player(String name, ArrayList<Tile> tray){
          this.name = name;
          totalPoints = 0;
          this.tray = tray;
     }

     /**
      * Get the name of the player
      * @return player's name
      */
     public String getName() {
          return name;
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
}
