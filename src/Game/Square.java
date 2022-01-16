package Game;

public class Square {
    private SquareType type;
    private Tile tile;

    /**
     *
     * @param type
     *
     */
    public Square(SquareType type) {
        this.type = type;
    }

    /**
     *
     * @return
     */
    public SquareType getType() {
        return type;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

     /**
     * @return
     */
    public Tile getTile() {
        return tile;
    }

    public void setType(SquareType type) {
        this.type = type;
    }


    /**
     *
     * @return
     */
    public boolean hasTile() {
        return tile == null ? false : true;
    }

    public void reset (){
        tile = null;
    }

}
