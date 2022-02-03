package Model;

/**
 * @author Nhat Tran.
 * @version finale
 */

public class TileDescription {
    private char letter;
    private int quantity;
    private int points;

    /**
     * constructor for the tile Description class.
     * @param letter the letter the tile represents.
     * @param quantity how many tiles of this kind are there?
     * @param points the points that the tile represents.
     */
    public TileDescription(char letter, int quantity, int points){
        this.letter = letter;
        this.quantity = quantity;
        this.points = points;
    }

    /**
     * getter for letter
     * @return the letter that the tileDescription represents.
     */

    public char getLetter() {
        return letter;
    }

    /**
     * getter for quantity
     * @return how many tiles of this kind are there?
     *
     */

    public int getQuantity() {
        return quantity;
    }

    /**
     * getter for points
     * @return the ponints that the tileDescription represents.
     */

    public int getPoints() {
        return points;
    }
}
