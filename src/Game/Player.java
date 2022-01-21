package Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
     private static Scanner sc = new Scanner(System.in);


     // -- Constructors -----------------------------------------------

     public Player(String name, ArrayList<Tile> tray){
          this.name = name;
          totalPoints = 0;
          this.tray = tray;
//          Scanner sc =  new Scanner(System.in);

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
                  "in the square A1, A2 and A3 (horizontally), write your move as: move DOG A1 H";
          System.out.println(prompt);
          move = sc.nextLine().split(" ");
          while (!move[0].equals("pass") && move.length !=4){
               System.out.println("Invalid Syntax. Please try again");
               System.out.println(prompt);
               move = sc.nextLine().split(" ");
          }
          if (move[0].equals("move") && !playerHasTile(move[1])) {
               move[0] = "wrongMove";
          }
//          for (Tile tile : tray) {
//               System.out.println("tile after detMove " + tile.getLetter());
//          }

          return move;
     }

     public Tile determineTileFromChar(char character) {
          for (Tile tile: tray){
//               if (character == 0 && character == tile.getLetter() ) {
//                    String prompt = "Please choose one of the letters below:\n"
//                            + "A B C D E F G H I K L M N O V Q R S T U V W X Y Z\n\n";
//                    try{
//                         Scanner sc = new Scanner(System.in);
//                         char input = sc.nextLine().toUpperCase().charAt(0);
//                         sc.close();
//                         return tile;
//
//                    } catch (IllegalArgumentException e) {
//                         //to be implement
//                              //Case 1: Player did not input an alphabetical letter
//                              //Case 2: input.length() > 1
//                              //Case 3: Player input a lower case letter (use toUpperCase)
//                    }
//               }
//               else
                    if (tile.getLetter() == character) {
//                         System.out.println("tile returned" + tile.getLetter());
                    return tile;
               }
          }
          return null;
     }

     private ArrayList<Tile> copyTray()  {
          ArrayList<Tile> copycat = new ArrayList<Tile>();
          for (Tile tile : tray) {
               copycat.add(new Tile(tile.getLetter(),tile.getPoint()));
          }
          return copycat;
     }

     /**
      * Check if the word input by the user has corresponding tiles
      * @param word
      * @return
      */

     public boolean playerHasTile(String word) {
          List<Tile> tempTray = new ArrayList<>();
          tempTray = copyTray();
          boolean validWord = true;
          //word > tempTray
          if (word.length() > tempTray.size()) return false;

          //Check character with corresponding tile in tray
          for(char character: word.toCharArray()) {
               forTile : for (Tile tile: tempTray){
                    if (character == tile.getLetter()){
                         validWord = true;
                         tempTray.remove(tile);
                         System.out.println(character);
                         break forTile;        //break out of the inner loop
                    }
                    else{
                         validWord = false;
                    }
               }
          }
          return validWord;

//          String[] trayWord = new String[tray.size()];
//          for (Tile tile : tray) {
//
//          }
//          for (char character : word.toCharArray())  {
//
////               if (!trayWord.contains(Character.toString(character))) return false;
//          }
//          return true;
//     }
//     public void reset() {
//          totalPoints = 0;
//     }
     }
}
