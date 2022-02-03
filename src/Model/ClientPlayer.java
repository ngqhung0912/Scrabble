package Model;


import java.io.IOException;

/**
 * This class represents a player in a client's version of game "ClientGame"
 * @author Nhat Tran
 * @version finale
 *
 */
public class ClientPlayer extends Player {
    /**
     * Client Player's constructor
     * @param name of the client.
     * @param id of the client.
     */
    public ClientPlayer(String name, int id) {
        super(name, id);
    }



    @Override
    public String[] determineMove() throws IOException {
        return new String[0];
    }

    /**
     * Overload of determineMove version which separates the command from the move.
     * @param move moves sent by the clients including the command.
     * @return the tiles sent together with the move, represents by a String Array.
     */
    public String[] determineMove(String[] move) {
        String[] moveTiles = new String[move.length - 1];
        System.arraycopy(move, 1, moveTiles, 0, move.length - 1);
        return moveTiles;
    }
}
