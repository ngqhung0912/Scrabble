package Model;

/**
 * Represent a square in the Scrabble Board.
 *
 * @author Hung Nguyen
 * @version 0.1
 */
public class Square {
    private SquareType type;
    private Tile tile;
    private int xPosition;
    private int yPosition;

    /**
     * Square Constructor.
     * @param type the type of square.
     * @param xPosition x position of the square.
     * @param yPosition y position of the square.
     */
    public Square(SquareType type, int xPosition, int yPosition) {
        this.type = type;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.tile = null;
    }

    /**
     * getter for the location
     * @return location.
     */

    public String getLocation() {
        return  xPosition + "" +  yPosition;
    }

    /**
     *
     * @return the type of this square
     */
    public SquareType getType() {
        return type;
    }

    /**
     *
     * @param tile tile to be assigned to the square by player
     */
    public void setTile(Tile tile) {
        this.tile = tile;

    }


    /**
     * @return the current tile on the square
     */
    public Tile getTile() {
        return tile;
    }

    /**
     * Set the type of this square, when initialize board.
     * @param type the type of the tile.
     */
    public void setType(SquareType type) {
        this.type = type;
    }

    public int getxPosition() {
        return xPosition;
    }

    public int getyPosition() {
        return yPosition;
    }

    /**
     *
     * @return true if this square has a tile, false otherwise
     */
    public boolean hasTile() {
        return tile == null ? false : true;
    }

}
