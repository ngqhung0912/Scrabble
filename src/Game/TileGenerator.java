package Game;

import java.util.ArrayList;
import java.util.List;

public class TileGenerator {
    public List<Tile> generateTiles(){
        /**
         * @invariant inititialCapacity == 26
         */
        List<TileDescription> input = new ArrayList<>(27);

        input.add(new TileDescription('0', 2, 0));
        input.add(new TileDescription('A', 9, 1));
        input.add(new TileDescription('B', 2, 3));
        input.add(new TileDescription('C', 2, 3));
        input.add(new TileDescription('D', 4, 2));
        input.add(new TileDescription('E', 12, 1));
        input.add(new TileDescription('F', 2, 4));
        input.add(new TileDescription('G', 2, 2));
        input.add(new TileDescription('H', 2, 4));
        input.add(new TileDescription('I', 8, 1));
        input.add(new TileDescription('J', 2, 8));
        input.add(new TileDescription('K', 2,5));
        input.add(new TileDescription('L', 4, 1));
        input.add(new TileDescription('M',2,3));
        input.add(new TileDescription('N', 6,1));
        input.add(new TileDescription('O',8,1));
        input.add(new TileDescription('P', 2,3));
        input.add(new TileDescription('Q',1,10));
        input.add(new TileDescription('R',6,1));
        input.add(new TileDescription('S',4,1));
        input.add(new TileDescription('T',6,1));
        input.add(new TileDescription('U',4,1));
        input.add(new TileDescription('V',2,4));
        input.add(new TileDescription('W',2,4));
        input.add(new TileDescription('X',1,8));
        input.add(new TileDescription('Y',2,4));
        input.add(new TileDescription('Z',1,10));

        /**
         * @invariant initialCapacity == 100
         */
        List<Tile> tiles = new ArrayList<>(100);
        for(TileDescription tileDescription: input){
            for(int i = 0; i < tileDescription.getQuantity(); i++){
                tiles.add(new Tile(tileDescription.getLetter(), tileDescription.getPoints()));
            }
        }

        return tiles;

    }


}
