package Test;

import Model.LocalPlayer;
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
//    private ClientPlayer player2;
//    private ServerPlayer player3;

    @BeforeEach
    void setUp() {
        player1 = new LocalPlayer("nhat", 0);
        player1.setFinalPoints(100);
    }

    /**
     * test setting the tray of the player.
     */
    @Test
    void setTray() {

    }

    /**
     * Test setting the final point.
     */
    @Test
    void setFinalPoints() {

    }

    /**
     * Test adding points to the player.
     */
    @Test
    void testAddPoints() {
        assertEquals(player1.getTotalPoints(), 100);
        player1.addPoints(13);
        assertEquals(player1.getTotalPoints(), 113);
    }

    /**
     * Test comparing the player.
     */
    @Test
    void testCompareTo() {
        LocalPlayer localPlayer2 = new LocalPlayer("hung", 50);
        localPlayer2.setFinalPoints(50);
        assertEquals(player1.compareTo(localPlayer2), 50);

    }
}
