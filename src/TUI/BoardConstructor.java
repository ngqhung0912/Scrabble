package TUI;
import Game.*;


public class BoardConstructor  {
    public static String generateBoard() {
        Board board = new Board();
        StringBuilder builder = new StringBuilder();
        builder.append("    ");
        for (int x = 0; x < board.SIZE; x++) {
            builder.append("  " + ((char)65+x) + "  ");
        }
        builder.append("\n");

        builder.append("┏");

        for (int x = 0; x < board.SIZE-1; x++) {
            builder.append("━━━━┳");
        }
        builder.append("━━━━┓");
        builder.append("\n");
        builder.append("┃");



        for (int y = 0; y < board.SIZE; y++) {
            builder.append("" + (y<9? "  " : " ") + (y+1) + " ");
            builder.append("┃");

            for (int x = 0; x < board.SIZE; x++) {
                //implement switch case for color-coding special
                Square square = board.get();

            }

        }



        return builder.toString();

    }
    public static void main(String[] args) {
        System.out.println(generateBoard());
    }







}
