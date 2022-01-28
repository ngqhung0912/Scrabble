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

     public NetworkPlayer(String name, int id, NetworkView networkUI) {
          super(name, id);
          this.networkUI = networkUI;
     }

     @Override
     public String[] determineMove() throws IOException {
          return new String[0];
     }
}