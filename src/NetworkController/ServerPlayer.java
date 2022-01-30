package NetworkController;

import Model.Player;


import java.io.IOException;

/**
 * @author  Hung Nguyen
 * @version 0.1
 */
public class ServerPlayer extends Player {

     private boolean aborted;

     public ServerPlayer(String name, int id) {
          super(name, id);
          aborted = false;
     }

     @Override
     public String[] determineMove() throws IOException {
          //decode user's move from client handler.
          return new String[0];
     }

     public boolean isAborted() {
          return aborted;
     }

     public void setAborted(boolean aborted) {
          this.aborted = aborted;
     }
}