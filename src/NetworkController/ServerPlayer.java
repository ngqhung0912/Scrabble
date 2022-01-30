package NetworkController;

import Model.Player;
import View.NetworkView;

import java.io.IOException;

/**
 * @author  Hung Nguyen
 * @version 0.1
 */
public class ServerPlayer extends Player {

     public ServerPlayer(String name, int id) {
          super(name, id);
     }

     @Override
     public String[] determineMove() throws IOException {
          //decode user's move from client handler.
          return new String[0];
     }
}