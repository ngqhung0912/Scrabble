package Test;

import Model.Square;
import Model.SquareType;
import Model.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
/**
 * Test the logic of the square inside the board.
 * @author Nhat Tran
 * @version finale.
 */
import static org.junit.jupiter.api.Assertions.*;

class SquareTest {
    private Square normalSquare;
    private Square centerSquare;
    private Tile tile;
    @BeforeEach
    void setUp() {
        normalSquare = new Square(SquareType.NORMAL, 10, 5);
        centerSquare = new Square(SquareType.CENTER, 7, 7);
        tile = new Tile('A', 1);
    }

    /**
     * Test getting the type of the square.
     */
    @Test
    void testGetType() {
        assertEquals(normalSquare.getType(), SquareType.NORMAL);
        assertEquals(centerSquare.getType(), SquareType.CENTER);
    }

    /**
     * Test setting and getting the tile of the square.
     */
    @Test
    void testSetandGetTile() {
        assertNull(normalSquare.getTile());
        normalSquare.setTile(tile);
        assertEquals(normalSquare.getTile(), tile);
    }

    /**
     * Test getting the x position.
     */
    @Test
    void testGetxPosition() {
        assertEquals(normalSquare.getxPosition(), 10);
        assertEquals(centerSquare.getxPosition(), 7);
    }

    /**
     * Test getting the y position.
     */
    @Test
    void testGetyPosition() {
        assertEquals(normalSquare.getyPosition(), 5);
        assertEquals(centerSquare.getyPosition(), 7);
    }

    /**
     * Test if the square has tile or not.
     */
    @Test
    void testHasTile() {
        assertFalse(normalSquare.hasTile());
        normalSquare.setTile(tile);
        assertTrue(normalSquare.hasTile());
    }
}