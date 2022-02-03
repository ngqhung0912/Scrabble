package Model;

/**
 * There are 5 types of Square: Double letter and triple letter,
 * in which the tile places in that square will have its points doubled or tripled,
 * respectively. Double word and triple word doubled (or tripled) the word that came
 * across that square.
 * A center square functions like a double word square.
 * A normal square does not have any special abilities.
 */
public enum SquareType {
    DOUBLE_LETTER,
    TRIPLE_LETTER,
    DOUBLE_WORD,
    TRIPLE_WORD,
    CENTER,
    NORMAL;
}
