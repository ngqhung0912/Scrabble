package Test;

import Model.Tile;
import Model.TileDescription;
import Model.TileGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TileGeneratorTest {
    private TileGenerator tileGenerator;
    private Tile tile1;
    private Tile tile2;

    @BeforeEach
    void setUp() {
        tileGenerator = new TileGenerator();
        tile1 = new Tile('A', 1);
        tile2 = new Tile('Z', 10);
    }

    @Test
    void testGenerateTiles() {
        assertEquals(tileGenerator.generateTiles().size(), 100);
        assertEquals(tile1.getLetter(), 'A');
        assertNotEquals(tile2.getPoint(), 5);
    }
}