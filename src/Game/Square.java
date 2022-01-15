package Game;

public class Square {
    private TileType type;
    private Tile tile;

    /**
     *
     * @param type
     * @param tile
     */
    public Square(TileType type, Tile tile) {
        this.tile = tile;
        this.type = type;
    }

    /**
     *
     * @return
     */
    public TileType getType() {
        return type;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

    /**
     *
     * @return
     */
    public Tile getTile() {
        return tile;
    }

    /**
     *
     * @param type
     */
    public void setType(TileType type) {
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
