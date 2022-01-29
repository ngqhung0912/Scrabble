package NetworkController;

import Model.Player;
import View.NetworkView;

import java.io.IOException;

/**
 * @author  Hung Nguyen
 * @version 0.1
 */
public class NetworkPlayer extends Player {
     private NetworkView networkUI;

     public NetworkPlayer(String name, int id, ClientHandler handler) {
          super(name, id);
     }

     @Override
     public String[] determineMove() throws IOException {
          //decode user's move from client handler.
          return new String[0];
     }
}