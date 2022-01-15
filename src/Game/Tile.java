package Game;

public class Tile {
    private char letter;
    private int point;

    /**
     *
     * @return
     */
    public char getLetter() {
        return letter;
    }

    /**
     *
     * @return
     */
    public int getPoint() {
        return point;
    }

    /**
     *
     */
    public Tile(char letter, int point)   {
        this.letter = letter;
        this.point = point;
    }

    /**
     * 0 represents a blank tile
     * @return
     */
    public boolean isBlank() {
        return letter == '0' ? true : false;
    }
}
