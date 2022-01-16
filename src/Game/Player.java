package Game;

/**
 * author Nhat Tran
 */
public class Player {

     // -- Instance variables -----------------------------------------

     private String name;
     private int point;
     private Tile[] tray;

     // -- Constructors -----------------------------------------------

     public Player(String name, Tile[] tray){
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
     public Tile[] getTray() {
          return tray;
     }

     /**
      * Set the tray of the player with new tile(s)
      * @param tray
      */
     public void setTray(Tile[] tray) {
          this.tray = tray;
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
