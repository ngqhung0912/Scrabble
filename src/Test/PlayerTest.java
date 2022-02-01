package Test;

import LocalController.LocalPlayer;
import Model.Player;
import NetworkController.ClientPlayer;
import NetworkController.ServerPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LocalPlayerTest {
    private LocalPlayer player1;
//    private ClientPlayer player2;
//    private ServerPlayer player3;

    @BeforeEach
    void setUp() {
        player1 = new LocalPlayer("nhat", 0);
        player1.setFinalPoints(100);

    }

    @Test
    void setTray() {

    }

//    @Test
//    void setFinalPoints() {
//
//    }

    @Test
    void addPoints() {
        assertEquals(player1.getTotalPoints(), 100);
        player1.addPoints(13);
        assertEquals(player1.getTotalPoints(), 113);
    }

    @Test
    void compareTo() {
        LocalPlayer localPlayer2 = new LocalPlayer("hung", 50);
        localPlayer2.setFinalPoints(50);
        assertEquals(player1.compareTo(localPlayer2), 50);

    }

    @Test
    void testDetermineMove() {

    }
}