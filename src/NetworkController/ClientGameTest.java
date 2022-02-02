package NetworkController;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClientGameTest {
    private ClientGame game;
    private String[] move = {"D111", "O112", "G113"};
    @BeforeEach
    void setUp(){
        ClientPlayer player1 = new ClientPlayer("nhat", 0);
        ClientPlayer player2 = new ClientPlayer("bot", 1);
        String[] playerNames = {"nhat", "bot"};
        game = new ClientGame(playerNames);

    }

    @Test
    void voidPutTileToSquare() {

        assertEquals(game.putTileToSquare(move));
    }
}