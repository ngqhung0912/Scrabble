package Game;

public class Square {
    private SquareType type;
    private Tile tile;

    /**
     *
     * @param type
     * @param tile
     */
    public Square(SquareType type, Tile tile) {
        this.tile = tile;
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

     /*
     * @return
     */
    public Tile getTile() {
        return tile;
    }

    /**
     *
     * @param type
     */
    public void setType(SquareType type) {
        this.type = type;
    }

    /**
     *
     * @return
     */
    public boolean hasTile() {
        return false;
    }

    /**
     *
     * @return
     */
    public boolean isEmpty() {
        return false;
    }
}
