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
          String prompt = "It's player " + name + "'s turn. " + "\nInput format: If you want to put a words, " +
                  "\nfor example DOG into the board," +
                  "\nin the square A1, A2 and A3 , write your move as: MOVE D-A1 O-A2 G-A3" +
                  "\ntype SWAP to SWAP one more more letter(s) in your tray.";
          return textUI.getString(prompt).split(" ");
     }

}
