package Test;

import Model.Board;
import Model.Square;
import Model.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void testClone() {
        board.getSquare("A1").setTile(tileA);
        board.getSquare("B5").setTile(tileP);
        board.getSquare("C8").setTile(tileZ);
        board.getSquare("O12").setTile(tileA);
        Board clonedBoard = board.clone();

        //assertEquals(clonedBoard, board); //Not sure about
        assertEquals(clonedBoard.getSquare("A1").getTile(), board.getSquare("A1").getTile());
        assertEquals(clonedBoard.getSquare("B5").getTile(), board.getSquare("B5").getTile());
        assertEquals(clonedBoard.getSquare("C8").getTile(), board.getSquare("C8").getTile());
        assertEquals(clonedBoard.getSquare("O12").getTile(), board.getSquare("O12").getTile());
        assertEquals(clonedBoard.getSquare("H7").getType(), board.getSquare("H7").getType());

        assertNotEquals(clonedBoard.getSquare("A1"),board.getSquare("O12"));
    }

    @Test
    void getSquareIndex() {
        assertEquals(board.getSquare(10).getxPosition(), 10);
        assertEquals(board.getSquare(14).getyPosition(), 0);

        assertNull(board.getSquare(225), "Index out of bound. The method should return null");
    }

    @Test
    void testGetSquareCorrectXYCoordinate() {
        assertEquals(board.getSquare(14, 10).getxPosition(), 14);
        assertEquals(board.getSquare(14, 10).getyPosition(), 10);
    }

    @Test
    void testGetSquareIncorrectXYCoordinate() {
        assertNull(board.getSquare(225, 10), "Index out of bound. The method should return null");
        assertNull(board.getSquare(0, -1), "Index out of bound. The method should return null");
    }

    @Test
    void testGetSquareStringCoordinate() {
        assertEquals(board.getSquare("A1").getxPosition(), 0);
        assertEquals(board.getSquare("B14").getyPosition(), 14);
    }

    @Test
    void getSquareAbove() {
        //Square squareA1 = board.getSquare("A1");
        Square squareB5 = board.getSquare("B5");
        assertEquals(board.getSquareAbove(squareB5), board.getSquare("B4"));
    }

    @Test
    void getSquareBelow() {
        Square squareB5 = board.getSquare("B5");
        assertEquals(board.getSquareBelow(squareB5), board.getSquare("B6"));
    }

    @Test
    void getSquareLeft() {
        Square squareB5 = board.getSquare("B5");
        assertEquals(board.getSquareLeft(squareB5), board.getSquare("A5"));
    }

    @Test
    void getSquareRight() {
        Square squareB5 = board.getSquare("B5");
        assertEquals(board.getSquareRight(squareB5), board.getSquare("C5"));
    }
}