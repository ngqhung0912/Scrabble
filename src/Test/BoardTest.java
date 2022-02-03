package Test;

import Model.Board;
import Model.Square;
import Model.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Test for board logic
 * @author Nhat Tran
 * @version finale
 */

class BoardTest {
    private Board board;
    private Tile tileA;
    private Tile tileZ;
    private Tile tileP;


    @BeforeEach
    void setUp() {
        board = new Board();

        tileA = new Tile('A', 1);
        tileZ = new Tile('Z', 10);
        tileP = new Tile('P', 3);
    }

    /**
     * Test for clone method of the board.
     */
    @Test
    void testClone() {
        board.getSquare("A1").setTile(tileA);
        board.getSquare("B5").setTile(tileP);
        board.getSquare("C8").setTile(tileZ);
        board.getSquare("O12").setTile(tileA);
        Board clonedBoard = board.clone();

        for(int i = 0; i < board.SIZE * board.SIZE; i++) {
            assertEquals(board.getSquare(i).getLocation(), clonedBoard.getSquare(i).getLocation());
        }
    }

    /**
     * Test getting square index.
     */
    @Test
    void getSquareIndex() {
        assertNull(board.getSquare(225));
        assertEquals(board.getSquare(10).getxPosition(), 10);
        assertEquals(board.getSquare(14).getyPosition(), 0);

        assertNull(board.getSquare(225), "Index out of bound. The method should return null");
    }

    /**
     * Test getting square with correct X and Y coordinate.
     */
    @Test
    void testGetSquareCorrectXYCoordinate() {
        assertEquals(board.getSquare(14, 10).getxPosition(), 14);
        assertEquals(board.getSquare(14, 10).getyPosition(), 10);
    }

    /**
     * Test getting square with incorrect X and Y coordinate.
     */
    @Test
    void testGetSquareIncorrectXYCoordinate() {
        assertNull(board.getSquare(225, 10), "Index out of bound. The method should return null");
        assertNull(board.getSquare(0, -1), "Index out of bound. The method should return null");
    }

    /**
     * Test getting square with incorrect String coordinate.
     */
    @Test
    void testGetSquareStringCoordinate() {
        assertEquals(board.getSquare("A1").getxPosition(), 0);
        assertEquals(board.getSquare("B14").getyPosition(), 14);
    }

    /**
     * Test getting the above square.
     */
    @Test
    void getSquareAbove() {
        //Square squareA1 = board.getSquare("A1");
        Square squareB5 = board.getSquare("B5");
        assertEquals(board.getSquareAbove(squareB5), board.getSquare("B4"));
    }

    /**
     * Test getting the below square.
     */
    @Test
    void getSquareBelow() {
        Square squareB5 = board.getSquare("B5");
        assertEquals(board.getSquareBelow(squareB5), board.getSquare("B6"));
    }

    /**
     * Test getting the left square.
     */
    @Test
    void getSquareLeft() {
        Square squareB5 = board.getSquare("B5");
        assertEquals(board.getSquareLeft(squareB5), board.getSquare("A5"));
    }

    /**
     * Test getting the right square. 
     */
    @Test
    void getSquareRight() {
        Square squareB5 = board.getSquare("B5");
        assertEquals(board.getSquareRight(squareB5), board.getSquare("C5"));
    }
}