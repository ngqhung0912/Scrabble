package Game;

import java.util.*;

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
          String prompt = "It's player " + name + "'s turn. " + "\nInput format: If you want to put a words, " +
                  "\nfor example DOG into the board," +
                  "\nin the square A1, A2 and A3 , \nwrite your move as: MOVE D-A1 O-A2 G-A3";
          System.out.println(prompt);
          move = sc.nextLine().split(" ");
//          while (!move[0].equals("PASS") || !move[0].equals("MOVE") || !move[0].equals("SHUFFLE")){
//               System.out.println("Invalid Syntax. Please try again");
//               System.out.println(prompt);
//               move = sc.nextLine().split(" ");
//          }
          return move;
     }

     public LinkedHashMap<String, String> mapLetterToSquare(String[] move){
          LinkedHashMap<String , String > letterToSquare = new LinkedHashMap<>();
          for (int i = 0; i < move.length; i++) {
               String[] letterSquarePair = move[i].split("-");
               letterToSquare.put(letterSquarePair[0], letterSquarePair[1]);
          }
          return letterToSquare;
     }

     public Tile determineTileFromChar(char character) {
          for (Tile tile: tray){
//               if (character == 0 && character == tile.getLetter() ) {
//                    String prompt = "Please choose one of the letters below:\n"
//                            + "A B C D E F G H I K L M N O V Q R S T U V W X Y Z\n\n";
//                    try{
//                         Scanner sc = new Scanner(System.in);
//                         char input = sc.nextLine().toUpperCase().charAt(0);
//                         //sc.close();
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
      * @param
      * @return
      */

     public boolean playerHasTile(LinkedHashMap<String, String> letterToSquare) {
          List<Tile> tempTray = copyTray();
          boolean validWord = true;
          //word > tempTray
          if (letterToSquare.size() > tempTray.size()) return false;

          //Check character with corresponding tile in tray
          for(String letter: letterToSquare.keySet()) {
               char character = letter.charAt(0);
               forTile : for (Tile tile: tempTray){
                    if (character == tile.getLetter()){
                         validWord = true;
                         tempTray.remove(tile);
                         System.out.println("character in playerHasTile " + character);
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
