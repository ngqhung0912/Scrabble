package Game;

import java.util.ArrayList;

/**
 * author Nhat Tran
 */
public class Player {

     // -- Instance variables -----------------------------------------

     private String name;
     private int point;
     private ArrayList<Tile> tray;

     // -- Constructors -----------------------------------------------

     public Player(String name, ArrayList<Tile> tray){
          this.name = name;
          point = 0;
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


     /**
      * Get the current point of the player
      * @return
      */
     public int getPoint() {
          return point;
     }

     /**
      *
      * @param point
      */
     public void setPoint(int point){
          this.point = point;
     }
}
