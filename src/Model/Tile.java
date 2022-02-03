package Model;

/**
 * Tile for the Scrabble game - Module 2 Project.
 * Each tile represents a character with points.
 * @author Hung Nguyen
 * @version finale
 */

public class Tile {
    private char letter;
    private int point;
    private boolean isBlank;

    /**
     * Constructor for tiles
     * @param letter the letter the tile represents.
     * @param point the points that letter has.
     */
    public Tile(char letter, int point)   {
        this.letter = letter;
        this.point = point;
    }

    /**
     * getter for the letter that the tile represents.
     * @return the letter that the tile represents, in char.
     */
    public char getLetter() {
        return letter;
    }

    /**
     * gettter for the point of the tile.
     * @return how many points this letter generates should it be used.
     */
    public int getPoint() {
        return point;
    }

    /**
     * setter for the letter of the tile
     * @param letter the letter to set.
     */

    public void setLetter(char letter) {
        this.letter = letter;
    }

    /**
     * 0 represents a blank tile
     * @return if this tile is a blank tile or not.
     */
    public boolean isBlank() {
        return letter == '0' ? true : false;
    }
}
