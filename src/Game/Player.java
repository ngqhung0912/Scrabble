package Game;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author Nhat Tran, Hung Nguyen
 * @version 0.1
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
     public String[] determineMove() {
          String[] move = null;
          String prompt = "It's player: " + name + "'s turn. " + "Input format: If you want to put a words, " +
                  "for example DOG into the board," +
                  "in the square A1, A2 and A3 (horizontally), write your move as: move dog A1 H";
          System.out.println(prompt);
          Scanner sc= new Scanner(System.in);
          move = sc.nextLine().split(" ");
          sc.close();
          if (move[0].equals("move") && !hasTile(move[1])) {
               move[0] = "wrongMove";
          }
          return move;
     }

     public Tile determineTileFromChar(char character) {
          for (Tile tile: this.getTray()){
               if (character == 0) {
                    String prompt = "Please choose one of the letters below\n"
                            + "A B C D E F G H I K L M N O V Q R S T U V W X Y Z";
                    try{
                         Scanner sc = new Scanner(System.in);
                         char input = sc.nextLine().charAt(0);
                    } catch (IllegalArgumentException e) {
                         //to be implement
                              //Case 1: Player did not input an alphabetical letter
                              //Case 2: input.length() > 1
                    }
               }
               else if (tile.getLetter() == character) {
                    return tile;
               }
          }
          return null;
     }


     public boolean hasTile(String word) {
          String trayWord = "";
          for (Tile tile : tray) {
               trayWord += tile.getLetter();
          }
          return trayWord.contains(word);
     }
//     public void reset() {
//          totalPoints = 0;
//     }

}
