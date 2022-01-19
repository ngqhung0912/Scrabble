package Game;

public class TileDescription {
    private char letter;
    private int quantity;
    private int points;

    public TileDescription(char letter, int quantity, int points){
        this.letter = letter;
        this.quantity = quantity;
        this.points = points;
    }



    public char getLetter() {
        return letter;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getPoints() {
        return points;
    }
}
