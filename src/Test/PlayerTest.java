package Test;

import Model.LocalPlayer;
import Model.Tile;
import Model.TileDescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class LocalPlayerTest {
    private LocalPlayer player1;
    private LocalPlayer player2;

    @BeforeEach
    void setUp() {
        player1 = new LocalPlayer("nhat", 0);
        player2 = new LocalPlayer("hung", 50);
    }

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


    @Test
    void addPoints() {
        player1.addPoints(100);
        assertEquals(player1.getTotalPoints(), 100);
        player1.addPoints(13);
        assertEquals(player1.getTotalPoints(), 113);
    }

    @Test
    void compareTo() {
        player1.setFinalPoints(100);
        player2.setFinalPoints(50);
        assertEquals(player1.compareTo(player2), 50);
    }
}