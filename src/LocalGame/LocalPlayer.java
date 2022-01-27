package LocalGame;

import java.io.IOException;

import GameLogic.Player;
import TUI.LocalView;

/**
 * @author Nhat Tran, Hung Nguyen
 * @version 0.1
 */
public class LocalPlayer extends Player  {

     // -- Instance variables -----------------------------------------

     protected static LocalView textUI;
     // -- Constructors -----------------------------------------------
     public LocalPlayer(String name, int id){
          super(name, id);
          textUI = new LocalView();
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

}
