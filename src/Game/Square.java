package Game;

/**
 * @author Hung Nguyen
 * @version 0.1
 */
public class Square {
    private SquareType type;
    private Tile tile;

    /**
     * Square Constructor
     * @param type of the square, indicated by SquareType enum
     *
     */
    public Square(SquareType type) {
        this.type = type;
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
     * @param type
     */
    public void setType(SquareType type) {
        this.type = type;
    }


    /**
     *
     * @return true if this square has a tile, false otherwise
     */
    public boolean hasTile() {
        return tile == null ? false : true;
    }

//    public void reset (){
//        tile = null;
//    }

    public Square getSquareAbove(Square currentSquare){
        
        return null;
    }

    public Square getSquareBelow(){
        return null;
    }

    public Square getSquareLeft(){
        return null;
    }

    public Square getSquareRight(){
        return null;
    }

}
