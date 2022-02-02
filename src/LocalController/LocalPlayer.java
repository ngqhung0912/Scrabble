package LocalController;

import java.io.IOException;

import Model.Player;
import View.LocalView;

/**
 * @author Nhat Tran, Hung Nguyen
 * @version 0.1
 */
public class LocalPlayer extends Player  {

     // -- Instance variables -----------------------------------------

     private static LocalView textUI;
     // -- Constructors -----------------------------------------------
     public LocalPlayer(String name, int id){
          super(name, id);
          this.textUI = new LocalView();
     }
     /**
      *
      *
      * @return
      */
     @Override
     public String[] determineMove() throws IOException {
          String prompt = "It's your turn. " + "\nInput format: If you want to put a words, " +
                  "for example DOG into the board," +
                  "\nin the square A1, A2 and A3 , write your move as: MOVE D.A1 O.A2 G.A3" +
                  "\nThe symbol \"-\" represents a blank tile, to determine a letter for the blank tile," +
                  "\nchoose one of the letters below: " +
                  "\nA B C D E F G H I J K L M N O P Q R S T U V W X Y Z\n" +
                  "\nthen write: MOVE -D.A1 O.A2 G.A3" +
                  "\nType SWAP to SWAP one or more letter(s) in your tray." +
                  "or SWAP with no argument to pass your turn.";
          return textUI.getString(prompt).split(" ");
     }

     public static void main(String[] args) throws IOException {
          LocalPlayer local1 = new LocalPlayer("nhat", 0);
          LocalPlayer local2 = new LocalPlayer("xon", 1);

          String[] moves = local1.determineMove();
          String move = "";

          for (int i = 0; i <moves.length; i++) {
               move += moves[i] + " ";
          }

          System.out.println(move);
     }

}
