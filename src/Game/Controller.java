package Game;

import java.io.IOException;

public class Controller {
    private static final String HELP = "Wrong input format. Correct input format: <numPlayers> <P1name> <P2name> ... ";

    public void newGame(Game game, int numPlayers, String[] playerName) {
        game = new Game(numPlayers,playerName);
    }

    public static void main(String[] args) throws IOException {
        int numPlayers = 0;
        try {
            numPlayers = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println(HELP);
        }
        if (args.length > numPlayers + 1) {
            System.out.println(HELP);
        }
        String[] playerName = new String[numPlayers];

        for (int i = 0; i < numPlayers; i++) {
            playerName[i] = args[i+1];
        }

        Game game = new Game(numPlayers,playerName);
        game.play();
    }






}
