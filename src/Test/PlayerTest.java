package Test;

import Model.LocalPlayer;
import Model.Tile;
import Model.TileDescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
/**
 * Test the local player's logic.
 * @author Nhat Tran.
 * @version finale.
 */
class LocalPlayerTest {
    private LocalPlayer player1;
    private LocalPlayer player2;

    @BeforeEach
    void setUp() {
        player1 = new LocalPlayer("nhat", 0);
        player2 = new LocalPlayer("hung", 50);
    }

    /**
     * test setting the tray of the player.
     */
    @Test
    void setTray() {
        assertTrue(player1.getTray().size() == 0);

        ArrayList<Tile> tray = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            Tile tile = new Tile('A', 1);
            tray.add(tile);
        }
        player1.setTray(tray);
        assertTrue(player1.getTray().size() == 7);
    }


    /**
     * Test adding points to the player.
     */
    @Test
    void addPoints() {
        player1.addPoints(100);
        assertEquals(player1.getTotalPoints(), 100);
        player1.addPoints(13);
        assertEquals(player1.getTotalPoints(), 113);
    }

    /**
     * Test comparing the player.
     */
    @Test
    void compareTo() {
        player1.setFinalPoints(100);
        player2.setFinalPoints(50);
        assertEquals(player1.compareTo(player2), 50);
    }
}