package NetworkGame;

import GameLogic.Tile;
import TUI.LocalView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * @author Nhat Tran, Hung Nguyen
 * @version 0.1
 */
public class NetworkPlayer implements Comparable<NetworkPlayer> {

     // -- Instance variables -----------------------------------------

     private String name;
     private int totalPoints;
     private ArrayList<Tile> tray;
     private static final String FORMAT = "Input format: If you want to put a words, for example DOG into the board, " +
             "in the square A1, A2 and A3, write your move as: DA1 OA2 GA3";
     //private static Scanner sc = new Scanner(System.in);
     private static BufferedReader bf;
     private static LocalView textUI;


     // -- Constructors -----------------------------------------------

     public NetworkPlayer(String name, ArrayList<Tile> tray){
          this.name = name;
          totalPoints = 0;
          this.tray = tray;
          bf = new BufferedReader(new InputStreamReader(System.in));
          textUI = new LocalView();
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
          String prompt = "It's player " + name + "'s turn. " + "\nInput format: If you want to put a words, " +
                  "\nfor example DOG into the board," +
                  "\nin the square A1, A2 and A3 , write your move as: MOVE D-A1 O-A2 G-A3" +
                  "\ntype SWAP to SWAP one more more letter(s) in your tray, and type PASS to end your turn immediately.";
          return textUI.getString(prompt).split(" ");
     }

     public LinkedHashMap<String, String> mapLetterToSquare(String[] move){
          LinkedHashMap<String , String > letterToSquare = new LinkedHashMap<>();
          for (int i = 0; i < move.length; i++) {
               String[] letterSquarePair = move[i].split("-");
               if (letterSquarePair.length < 2) {
                    return null;
               }
               String[] coordinate = letterSquarePair[1].split("");
               if (coordinate.length > 3) {
                    System.out.println("Coordinate's length should not be greater than 3.");
                    return null;
               }
               for (int j = 1; j < coordinate.length; j++) {
                    try {
                         Integer.parseInt(coordinate[j]);
                    } catch (NumberFormatException e) {
                         System.out.println("Wrong input format. Move should be D-H7 O-H8 G-H9 and so on.");
                         return null;
                    }
               }
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
                            + "A B C D E F G H I J K L M N O P Q R S T U V W X Y Z\n";
                    try{
                         char input = textUI.getChar(prompt);
                         tile.setLetter(input);
                         return tile;

                    } catch (IllegalArgumentException e) {
                         return null;
                    } catch (IOException e) {
                         return null;
                    }
               }
               else if (tile.getLetter() == character) {
                    return tile;
               }
          }
          return null;
     }

     public int compareTo(NetworkPlayer o) {
          return this.totalPoints - o.totalPoints;
     }
}
