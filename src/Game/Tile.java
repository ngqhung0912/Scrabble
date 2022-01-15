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
     * @param letter
     */
    public void setLetter(char letter) {
        this.letter = letter;
    }

    /**
     *
     */
    public Tile()   {

    }

    public boolean isBlank(){
        return false;
    }


}
