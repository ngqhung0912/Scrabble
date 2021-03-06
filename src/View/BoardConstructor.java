package View;
import Model.*;

/**
 * BoardConstructor class to construct a TUI representation of a Scrabble board.
 * @author Hung Nguyen
 * @version finale
 */

public class BoardConstructor  {
    /**
     * Generate the TUI representation of a Board
     * @param board to generate
     * @return the string representation.
     */
    public static String generateBoard(Board board) {
        StringBuilder builder = new StringBuilder();
        builder.append("    ");
        for (int x = 0; x < board.SIZE; x++) {
            builder.append("  " + ((char)(65+x)) + (x) +  (x < 10 ?  " " : ""));
        }
        builder.append("\n");
        builder.append("    ┏");
        for (int x = 0; x < board.SIZE-1; x++) {
            builder.append("━━━━┳");
        }
        builder.append("━━━━┓");
        builder.append("\n");

        for (int y = 0; y < board.SIZE; y++) {
            builder.append("" + (y<10? "  " : " ") + (y) + " ");
            builder.append("┃");

            for (int x = 0; x < board.SIZE; x++) {
                Square square = board.getSquare(x,y);
                switch (square.getType()) {
                    case CENTER:
                        builder.append(ANSI.PURPLE_BACKGROUND);
                        break;
                    case TRIPLE_LETTER:
                        builder.append(ANSI.BLUE_BACKGROUND_BRIGHT);
                        break;
                    case TRIPLE_WORD:
                        builder.append(ANSI.RED_BACKGROUND);
                        break;
                    case DOUBLE_LETTER:
                        builder.append(ANSI.GREEN_BACKGROUND);
                        break;
                    case DOUBLE_WORD:
                        builder.append(ANSI.YELLOW_BACKGROUND);
                        break;
                }


                builder.append("  " + (square.hasTile() ? square.getTile().getLetter() : " ") + " ");
                builder.append(ANSI.RESET);
                builder.append("┃");
            }
            builder.append("\n    ");
            if (y < board.SIZE-1) {
                builder.append("┣");
            }
            else {
            builder.append("┗");
            }

            for (int x = 0; x < board.SIZE; x++ ) {
                if (y == board.SIZE - 1 && x < board.SIZE - 1) {
                    builder.append("━━━━┻");
                } else if (y == board.SIZE - 1 && x == board.SIZE - 1) {
                    builder.append("━━━━┛");
                }
                else if (y < board.SIZE -1 && x == board.SIZE-1) {
                    builder.append("━━━━┫");
                }
                else {
                    builder.append("━━━━╋");
                }
            }
            builder.append("\n");
            builder.append(ANSI.RESET);
        }
        return builder.toString();
    }
}
