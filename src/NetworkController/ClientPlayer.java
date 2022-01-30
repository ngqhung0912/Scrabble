package NetworkController;

import Model.Player;
import View.NetworkView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ClientPlayer extends Player {

    public ClientPlayer(String name, int id) {
        super(name, id);
    }

    @Override
    public String[] determineMove() throws IOException {
        return new String[0];
    }

    public String[] determineMove(String[] move) throws IOException {
        String[] moveTiles = new String[move.length - 1];
        System.arraycopy(move, 1, moveTiles, 0, move.length - 1);

        return moveTiles;
    }

//    public String[] determineOpponentMove(String[] opponentMoves) throws IOException {
//
//
//    }



}
