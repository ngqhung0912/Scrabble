package Model;
/**
 * This class represents a Scrabble's board.
 * @author Nhat Tran, Hung Nguyen
 * @version finale
 */

public class Board {
    // -- Instance variables -----------------------------------------

    public static final int SIZE = 15;

    private Square[][] squaresBoard;

    // -- Constructors -----------------------------------------------

    /**
     * Board constructor.
     */
    public Board(){
        squaresBoard = new Square[SIZE][SIZE];

        for (int x = 0; x < SIZE; x++)
            for (int y = 0; y < SIZE; y++) squaresBoard[x][y] = new Square(SquareType.NORMAL, x, y);

        squaresBoard[7][7].setType(SquareType.CENTER);
        squaresBoard[0][0].setType(SquareType.TRIPLE_WORD);
        squaresBoard[0][7].setType(SquareType.TRIPLE_WORD);
        squaresBoard[0][14].setType(SquareType.TRIPLE_WORD);
        squaresBoard[7][0].setType(SquareType.TRIPLE_WORD);
        squaresBoard[7][14].setType(SquareType.TRIPLE_WORD);
        squaresBoard[14][0].setType(SquareType.TRIPLE_WORD);
        squaresBoard[14][7].setType(SquareType.TRIPLE_WORD);
        squaresBoard[14][14].setType(SquareType.TRIPLE_WORD);

        squaresBoard[0][3].setType(SquareType.DOUBLE_LETTER);
        squaresBoard[0][11].setType(SquareType.DOUBLE_LETTER);
        squaresBoard[2][6].setType(SquareType.DOUBLE_LETTER);
        squaresBoard[2][8].setType(SquareType.DOUBLE_LETTER);
        squaresBoard[3][0].setType(SquareType.DOUBLE_LETTER);
        squaresBoard[3][7].setType(SquareType.DOUBLE_LETTER);
        squaresBoard[3][14].setType(SquareType.DOUBLE_LETTER);
        squaresBoard[6][2].setType(SquareType.DOUBLE_LETTER);
        squaresBoard[6][6].setType(SquareType.DOUBLE_LETTER);
        squaresBoard[6][8].setType(SquareType.DOUBLE_LETTER);
        squaresBoard[6][12].setType(SquareType.DOUBLE_LETTER);
        squaresBoard[7][3].setType(SquareType.DOUBLE_LETTER);
        squaresBoard[7][11].setType(SquareType.DOUBLE_LETTER);
        squaresBoard[8][2].setType(SquareType.DOUBLE_LETTER);
        squaresBoard[8][6].setType(SquareType.DOUBLE_LETTER);
        squaresBoard[8][8].setType(SquareType.DOUBLE_LETTER);
        squaresBoard[8][12].setType(SquareType.DOUBLE_LETTER);
        squaresBoard[11][0].setType(SquareType.DOUBLE_LETTER);
        squaresBoard[11][7].setType(SquareType.DOUBLE_LETTER);
        squaresBoard[11][14].setType(SquareType.DOUBLE_LETTER);
        squaresBoard[12][6].setType(SquareType.DOUBLE_LETTER);
        squaresBoard[12][8].setType(SquareType.DOUBLE_LETTER);
        squaresBoard[14][3].setType(SquareType.DOUBLE_LETTER);
        squaresBoard[14][11].setType(SquareType.DOUBLE_LETTER);



        for (int i = 1; i < 5; i++) {
            squaresBoard[i][i].setType(SquareType.DOUBLE_WORD);
            squaresBoard[i][SIZE-1-i].setType(SquareType.DOUBLE_WORD);
            squaresBoard[SIZE-1-i][i].setType(SquareType.DOUBLE_WORD);
            squaresBoard[SIZE-1-i][SIZE-1-i].setType(SquareType.DOUBLE_WORD);
        }

        squaresBoard[1][5].setType(SquareType.TRIPLE_LETTER);
        squaresBoard[1][9].setType(SquareType.TRIPLE_LETTER);
        squaresBoard[5][1].setType(SquareType.TRIPLE_LETTER);
        squaresBoard[5][5].setType(SquareType.TRIPLE_LETTER);
        squaresBoard[5][9].setType(SquareType.TRIPLE_LETTER);
        squaresBoard[5][13].setType(SquareType.TRIPLE_LETTER);
        squaresBoard[9][1].setType(SquareType.TRIPLE_LETTER);
        squaresBoard[9][5].setType(SquareType.TRIPLE_LETTER);
        squaresBoard[9][9].setType(SquareType.TRIPLE_LETTER);
        squaresBoard[9][13].setType(SquareType.TRIPLE_LETTER);
        squaresBoard[13][5].setType(SquareType.TRIPLE_LETTER);
        squaresBoard[13][9].setType(SquareType.TRIPLE_LETTER);

    }

    /**
     * clone: method for duplicating the board.
     * @return a clone board.
     */
    public Board clone(){
        Board copyCat = new Board();
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                copyCat.squaresBoard[x][y].setTile(this.squaresBoard[x][y].getTile());
                copyCat.squaresBoard[x][y].setType(this.squaresBoard[x][y].getType());
            }
        }
        return copyCat;
    }


    /**
     * Method to get square by square index, from 1 to 225.
     * @param i index of the desired square
     * @return the desired square if index is less than 225, null otherwise.
     */
    public Square getSquare(int i) {
        return i < 225 ? getSquare(i % 15,i / 15 ) : null;
    }

    /**
     * Overload method to get square from x and y coordinate.
     * @param x horizontal coordinate of the square.
     * @param y vertical coordinate of the square.
     * @return the desired square.
     */

    public Square getSquare(int x, int y){
        return (x >= 0) && (x <= SIZE-1) && (y >= 0) && (y <= SIZE-1) ? squaresBoard[x][y] : null;
    }

    /**
     * Overload method to get square from String coordinate, i.e. H7, A13, etc.
     * @param coordinate the coordinate of the square.
     * @return the desired square.
     */

    public Square getSquare(String coordinate) {
        String[] dimension = coordinate.split("");
        int y = Integer.parseInt(dimension.length == 3 ? dimension[1] + dimension[2] : dimension[1] );       //The vertical coordinate
        char c = coordinate.charAt(0) ;          //The horizontal coordinate  (letter format)
        int x = -1;        //The horizontal coordinate (number format)
        char[] alphaArr = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

        for (int j = 0; j < alphaArr.length; j++){         // int j - index of alphaArr
            if (c == alphaArr[j]) {
                x = j;
            }
        }
        return getSquare(x, y);
    }

    /**
     * method to get the above square of the current square.
     * @param currentSquare the current square
     * @return the above square.
     */

    public Square getSquareAbove(Square currentSquare){
        int x = currentSquare.getxPosition();
        int y = currentSquare.getyPosition();
        return getSquare(x, y-1);
    }

    /**
     * method to get the below square of the current square.
     * @param currentSquare the current square
     * @return the below square.
     */

    public Square getSquareBelow(Square currentSquare){
        int x = currentSquare.getxPosition();
        int y = currentSquare.getyPosition();
        return getSquare(x, y+1);
    }

    /**
     * method to get the left square of the current square.
     * @param currentSquare the current square
     * @return the left square.
     */

    public Square getSquareLeft(Square currentSquare){
        int x = currentSquare.getxPosition();
        int y = currentSquare.getyPosition();
        return getSquare(x-1, y);
    }

    /**
     * method to get the right square of the current square.
     * @param currentSquare the current square
     * @return the right square.
     */

    public Square getSquareRight(Square currentSquare){
        int x = currentSquare.getxPosition();
        int y = currentSquare.getyPosition();
        return getSquare(x+1, y);
    }



}
