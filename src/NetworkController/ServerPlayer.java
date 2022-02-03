package NetworkController;

import Model.Player;


import java.io.IOException;

/**
 * This class represents a clients in a serverGame.
 * @author  Hung Nguyen
 * @version finale
 */
public class ServerPlayer extends Player {

     private boolean aborted;
     private ClientHandler client;

     /**
      * Constructor to the server Player.
      * @param client
      */
     public ServerPlayer(ClientHandler client) {
          super(client.toString(), client.getClientId());
          this.aborted = false;
          this.client = client;
     }

     @Override
     public String[] determineMove() throws IOException {
          //decode user's move from client handler.
          return new String[0];
     }

     /**
      * Check if this player has aborted or not.
      * @return true if aborted, false otherwise.
      */
     public boolean isAborted() {
          return aborted;
     }


     /**
      * set the aborted state for this player
      * @param aborted
      */
     public void setAborted(boolean aborted) {
          this.aborted = aborted;
     }

     /**
      * getter for the client belong to this player
      * @return client.
      */
     public ClientHandler getClient() { return client;}
}