package Game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * @author Nhat Tran, Hung Nguyen
 * @version 0.1
 */
public class Player implements Comparable<Player> {

     // -- Instance variables -----------------------------------------

     private String name;
     private int totalPoints;
     private ArrayList<Tile> tray;
     private static final String FORMAT = "Input format: If you want to put a words, for example DOG into the board, " +
             "in the square A1, A2 and A3, write your move as: DA1 OA2 GA3";
     //private static Scanner sc = new Scanner(System.in);
     private static BufferedReader bf;


     // -- Constructors -----------------------------------------------

     public Player(String name, ArrayList<Tile> tray){
          this.name = name;
          totalPoints = 0;
          this.tray = tray;
          bf = new BufferedReader(new InputStreamReader(System.in));

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
     public String[] determineMove() throws IOException {
          String[] move = null;
          String prompt = "It's player " + name + "'s turn. " + "\nInput format: If you want to put a words, " +
                  "\nfor example DOG into the board," +
                  "\nin the square A1, A2 and A3 , \nwrite your move as: MOVE D-A1 O-A2 G-A3" +
                  "\ntype SWAP to SWAP one more more letter(s) in your tray, and type PASS to end your turn immediately.";
          System.out.println(prompt);
          move = bf.readLine().split(" ");
          return move;
     }

     public LinkedHashMap<String, String> mapLetterToSquare(String[] move){
          LinkedHashMap<String , String > letterToSquare = new LinkedHashMap<>();
          for (int i = 0; i < move.length; i++) {
               String[] letterSquarePair = move[i].split("-");
               letterToSquare.put(letterSquarePair[1], letterSquarePair[0]);
          }
          return letterToSquare;
     }
     public ArrayList<Tile> determineTileToShuffle(char[] chars) {
          ArrayList<Tile> shuffledTile = new ArrayList<>();
          for (char character : chars) {
               Tile tile = determineTileFromChar(character);
               shuffledTile.add(tile);
          }
          return shuffledTile;
     }

     public Tile determineTileFromChar(char character) {
          for (Tile tile: tray){
               if (character == '#' && character == tile.getLetter() ) {
                    String prompt = "Please choose one of the letters below:\n"
                            + "A B C D E F G H I J K L M N O P Q R S T U V W X Y Z\n\n";
                    try{
                         System.out.println(prompt);
                         Scanner sc = new Scanner(System.in);
                         char input = sc.nextLine().charAt(0);
                         tile.setLetter(input);
                         return tile;

                    } catch (IllegalArgumentException e) {
                    }
               }
               else {
                    if (tile.getLetter() == character) {
                         return tile;
                    }
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

     }


     public int compareTo(Player o) {
          return this.totalPoints - o.totalPoints;
     }
}
