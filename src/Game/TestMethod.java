package Game;

import java.util.ArrayList;

public class TestMethod {
    public ArrayList<ArrayList<Square>> determinePossibleWordCombinations(Square startingPosition, String direction) {
        ArrayList<ArrayList<Square>> wordCombinations = new ArrayList<>();
        ArrayList<Square> initialWord = new ArrayList<>();

        Square currentPosition = startingPosition;

        loopOverAllInsertedTiles: while(currentPosition.hasTile() &&
                currentPosition.getxPosition() < 15 &&
                currentPosition.getyPosition() < 15 ) {

            if (direction.equals("H")) {
                // Traverse to the right:
                // while this square has a tile and this square's not out of bound {
                // add this square to initial word's right.
                // change the pointer to next square
                //
                //
                //
                //
                //
                //
                //

                // }
                //
                //Traverse to the left:
                // same as above.


            }
            else {

            }






        }







        return wordCombinations;
    }

}
