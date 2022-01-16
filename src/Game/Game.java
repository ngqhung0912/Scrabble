package Game;

public class Game {

    private Board board;
    private int numberPlayers;
    private int current;
    private Player[] players;

    /**
     *
     * @param numberPlayers
     */
    public Game(){

        board = new Board();
        current = 0;
    }


    public void start(){

    }

    public void update(){

    }

    public void printResult(){

    }

    public int calculatePoint(){
        return 0;
    }

    public String invalidWordMessage(){

        return null;
    }

    public boolean wordChecker(){
        return false;
    }



}
