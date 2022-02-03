package Test;

import Model.Tile;
import Model.TileDescription;
import Model.TileGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Nhat Tran
 * @version finale.
 */
class TileGeneratorTest {
    private TileGenerator tileGenerator;
    private TileDescription tileDescriptionA;
    private TileDescription tileDescriptionZ;
    private Tile tileA;
    private Tile tileZ;

    @BeforeEach
    void setUp() {
        tileGenerator = new TileGenerator();
        tileDescriptionA = new TileDescription('A', 9, 1);
        tileDescriptionZ = new TileDescription('Z', 6,10);
        tileA = new Tile('A', 1);
        tileZ = new Tile('Z', 10);
    }

    /**
     * Test generating the tiles.
     */
    @Test
    void testGenerateTiles() {
        assertEquals(tileGenerator.generateTiles().size(), 100);
        assertEquals(tileDescriptionA.getLetter(), tileA.getLetter());
        assertEquals(tileDescriptionZ.getPoints(), tileZ.getPoint());
    }
}