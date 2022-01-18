package Game;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * author Nhat Tran, Hung Nguyen
 */
public class Player {

     // -- Instance variables -----------------------------------------

     private String name;
     private int totalPoints;
     private ArrayList<Tile> tray;
     private static final String FORMAT = "Input format: If you want to put a words, for example DOG into the board, " +
             "in the square A1, A2 and A3, write your move as: DA1 OA2 GA3";


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

     /**
      * to be thinking: The handler of "validmove" should be in Player or in Board?
      *
      * @return
      */
     public String determineMove() {
          String move = null;
          System.out.println("It's player: " + name + "'s turn. " + "Input format: If you want to put a words, " +
                  "for example DOG into the board," +
                  "in the square A1, A2 and A3, write your move as: DA1 OA2 GA3");
          Scanner sc= new Scanner(System.in); 
          move = sc.nextLine();
          return move;
     }

}
