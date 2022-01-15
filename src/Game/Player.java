package Game;

public abstract class Player {
     private String name;

     private int point;
     private Tile[] tray;

     public String getName() {
          return name;
     }

     public void setName(String name) {
          this.name = name;
     }


     public Tile[] getTray() {
          return tray;
     }

     public void setTray(Tile[] tray) {
          this.tray = tray;
     }

     public int getPoint() {
          return point;
     }

     public abstract void setPoint(int point);
}
