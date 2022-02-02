package Model;

import NetworkController.ServerPlayer;
import WordChecker.main.java.ScrabbleWordChecker;

import java.util.*;

public abstract class Game {
    private List<Tile> tileBag;
    private Board board;
    private int passCount;
    private int currentPlayer;
    private int numPlayer;
    private Player[] players;
    private ScrabbleWordChecker checker;
    private List<Square> nextValidSquares;
    private ArrayList<String> usedWords;

    private String wordChecker(ArrayList<Square> squares) {
        String word = "";
        for (Square square : squares) {
            word += Character.toString(square.getTile().getLetter());
        }
        for (String usedWord : usedWords) {
            if (usedWord.equals(word)) {
                return word;
            }
        }
        if (checker.isValidWord(word) != null) {
            usedWords.add(checker.isValidWord(word).toString());
//            UI.showMessage(checker.isValidWord(word).toString());
            return checker.isValidWord(word).toString();
        }
        else {
            return null;
        }
    }

    private int calculatePoints(ArrayList<Square> squares) {
        int score = 0;
        boolean doubleWord = false;
        boolean tripleWord = false;
        for (Square square : squares) {
            switch (square.getType()) {
                case CENTER:
                    doubleWord = true;
                    break;
                case DOUBLE_LETTER:
                    score += square.getTile().getPoint()*2;
                    break;
                case TRIPLE_LETTER:
                    score += square.getTile().getPoint()*3 ;
                    break;
                case DOUBLE_WORD:
                    doubleWord = true;
                    score += square.getTile().getPoint();
                    break;
                case TRIPLE_WORD:
                    tripleWord = true;
                    score += square.getTile().getPoint();
                    break;
                case NORMAL:
                    score += square.getTile().getPoint();
                    break;
            }
        }
        return  (doubleWord && !tripleWord ? score * 2 : tripleWord && !doubleWord ? score * 3 : doubleWord && tripleWord ? score * 6 : score);
    }

    protected void resetPassCount() {
        this.passCount = 0;
    }

    protected void incrementPassCount() {
        this.passCount++;
    }

    public int getPassCount() {
        return passCount;
    }


    private boolean isEmptyTrayAndBag() {
        return players[currentPlayer].getTray().isEmpty() && tileBag.isEmpty();
    }

    public boolean gameOver() {
        return isEmptyTrayAndBag() || passCount > 5 || isFullBoard();
    }

    public Player isWinner(){
        Map<Player, Integer> finalDeduct = new HashMap<>();

        ArrayList<Tile> tilesLeft = null;

        //Create a map of players with their deduct points
        for (Player currentLocalPlayer : players) {
            tilesLeft = currentLocalPlayer.getTray();
            int deductPoints = 0;
            for (Tile tile : tilesLeft) {
                deductPoints += tile.getPoint();
            }
            finalDeduct.put(currentLocalPlayer, deductPoints);
        }


        for (int i = 0; i < players.length; i++) {
            int finalPoints = 0;
            if (tilesLeft.size() == 0) {
                int totalDeductPoints = finalDeduct.get(players[0]) + finalDeduct.get(players[1])
                        + finalDeduct.get(players[2]) + finalDeduct.get(players[3]);
                finalPoints = players[i].getTotalPoints() + totalDeductPoints;
            } else {
                finalPoints = players[i].getTotalPoints() - finalDeduct.get(players[i]);
            }
            players[i].setFinalPoints(finalPoints);
        }

        Player winner = players[0];
        for (int i = 1; i < players.length;) {
            int compare = winner.compareTo(players[i]);
            if (compare < 0) {
                winner = players[i];
            } else if (compare == 0) {
                if (winner.getTotalPoints() + finalDeduct.get(winner) < players[i].getTotalPoints() + finalDeduct.get(players[i])) {
                    winner = players[i];
                } else if (winner.getTotalPoints() + finalDeduct.get(winner) == players[i].getTotalPoints() + finalDeduct.get(players[i])) {
                    return null;
                }
            }
            i++;
        }
        return winner;
    }

    public ArrayList<String> getLetterFromTray(ArrayList<Tile> tray) {
        ArrayList<String> letterTray = new ArrayList<String>();
        for (Tile tile : tray) {
            letterTray.add(Character.toString(tile.getLetter()));
        }
        return letterTray;
    }

    public int incrementCurrentPlayer() {
        currentPlayer = currentPlayer < (numPlayer-1) ? currentPlayer+1 : 0;
        return currentPlayer;
    }

    public Board getBoard() { return board; }

    public List<Tile> getTileBag() { return tileBag; };

    public Player getCurrentPlayer() { return players[currentPlayer]; }

    public boolean makeMove(String[] moveTiles) {
        Board validBoard = isValidMove(mapLetterToSquare(moveTiles));
        if (validBoard != null) {
            board = validBoard.clone();
            return true;
        }
        resetPassCount();
        return false;
    }

    public abstract LinkedHashMap<String, String> mapLetterToSquare(String[] move);

    public abstract void swapTray(char[] chars);

    public boolean isFullBoard() {
        for (int i = 0; i < board.SIZE; i++) {
            if (!board.getSquare(i).hasTile()) return false;
        }
        return true;
    }

    public abstract Board isValidMove(LinkedHashMap<String, String> moves);

    private String determineMoveDirection(LinkedHashMap<String, String> moves) {
        if (moves.size() == 1) {
            return "H";
        }
        ArrayList<String> column = new ArrayList<>();

        for (Map.Entry<String, String> move : moves.entrySet()){
            column.add(move.getKey().split("")[0]);
        }
        if (!column.get(0).equals(column.get(1))) {
            return "H";
        }
        else {
            return "V";
        }

    }

    private  ArrayList<ArrayList<Square>> determinePossibleWordCombinations(ArrayList<Square> inputWord, String direction, Board copyBoard) {
        Square startingPosition = inputWord.get(0);

        ArrayList<ArrayList<Square>> wordCombinations = new ArrayList<>();
        ArrayList<Square> initialWord = new ArrayList<>();
        Square otherSideStartingPosition = null;

        if (startingPosition.getxPosition() > 0 && startingPosition.getyPosition() > 0 ) {
            if (inputWord.size() > 1) otherSideStartingPosition = direction.equals("H") ? copyBoard.getSquareLeft(startingPosition)
                    : copyBoard.getSquareAbove(startingPosition);
            else {
                if (copyBoard.getSquareLeft(startingPosition) == null) {
                    otherSideStartingPosition = copyBoard.getSquareAbove(startingPosition) == null ? null :
                            copyBoard.getSquareAbove(startingPosition);
                } else {
                    otherSideStartingPosition = copyBoard.getSquareLeft(startingPosition);
                }
            }
        }
        traversingRightandBelow : while (startingPosition != null
                && startingPosition.getxPosition() < 15
                && startingPosition.getyPosition() < 15
                && startingPosition.hasTile()) {

            Square currentPosition = startingPosition;
            initialWord.add(startingPosition);

            List<Square> occupiedSquares = getOccupiedSquare(copyBoard,inputWord);
            for (Square square : occupiedSquares) {
                if (currentPosition.getLocation().equals(square.getLocation())) {
                    startingPosition = direction.equals("H") ? copyBoard.getSquareRight(startingPosition)
                            : copyBoard.getSquareBelow(startingPosition);
                    continue traversingRightandBelow;
                }
            }


            if (direction.equals("H")) {
                ArrayList<Square> verticalWord = new ArrayList<>();
                verticalWord.add(currentPosition);
                Square nextAbovePosition = currentPosition;
                Square nextBelowPosition = currentPosition;


                AboveWhileH: while(nextAbovePosition.getyPosition() > 0 && copyBoard.getSquareAbove(nextAbovePosition).hasTile()) {
                    verticalWord.add(0,copyBoard.getSquareAbove(nextAbovePosition));
                    nextAbovePosition = copyBoard.getSquareAbove(nextAbovePosition);
                }

                belowWhileH: while(nextBelowPosition.getyPosition() < 15 && copyBoard.getSquareBelow(nextBelowPosition).hasTile()) {
                    verticalWord.add(copyBoard.getSquareBelow(nextBelowPosition));
                    nextBelowPosition = copyBoard.getSquareBelow(nextBelowPosition);
                }
                currentPosition = copyBoard.getSquareRight(currentPosition);


                if (verticalWord.size() > 1) wordCombinations.add(verticalWord);
            }
            else if (direction.equals("V")) {
                ArrayList<Square> horizontalWord = new ArrayList<>();
                horizontalWord.add(currentPosition);

                Square nextLeftPosition = currentPosition;
                Square nextRightPosition = currentPosition;

                leftWhileV: while(nextLeftPosition.getxPosition() > 0 && copyBoard.getSquareLeft(nextLeftPosition).hasTile()) {
                    horizontalWord.add(0,copyBoard.getSquareLeft(nextLeftPosition));
                    nextLeftPosition = copyBoard.getSquareLeft(nextLeftPosition);
                }

                rightWhileV: while(nextRightPosition.getxPosition() < 15 && copyBoard.getSquareRight(nextRightPosition).hasTile()) {
                    horizontalWord.add(copyBoard.getSquareRight(nextRightPosition));
                    nextRightPosition = copyBoard.getSquareRight(nextRightPosition);
                }
                currentPosition = copyBoard.getSquareBelow(currentPosition);

                if (horizontalWord.size() > 1) wordCombinations.add(horizontalWord);
            }

            startingPosition = currentPosition;


        }
        traversingLeftandAbove: while (otherSideStartingPosition != null
                && otherSideStartingPosition.getxPosition() >= 0
                && otherSideStartingPosition.getyPosition() >= 0
                && otherSideStartingPosition.hasTile()) {
            Square currentPosition = otherSideStartingPosition;
            if (direction.equals("H")) {
                currentPosition = copyBoard.getSquareLeft(currentPosition);
                initialWord.add(0,otherSideStartingPosition);
                otherSideStartingPosition = currentPosition;
            }
            else if (direction.equals("V")) {
                currentPosition = copyBoard.getSquareAbove(currentPosition);
                initialWord.add(0,otherSideStartingPosition);
                otherSideStartingPosition = currentPosition;
            }
//            if (startingPosition == null) {
//                break traversingLeftandAbove;
//            }
        }

        for (Square square : inputWord) {
            if (!initialWord.contains(square)) {
                return null;
            }
        }

        if (initialWord.size() > 1) wordCombinations.add(initialWord);
        return wordCombinations;
    }

    private List<Square> getOccupiedSquare(Board copyBoard, List<Square> playSquares) {
        List<Square> occupiedSquares = new ArrayList<>();
        for (int i = 0; i < (Board.SIZE * Board.SIZE) ; i++) {
            if(copyBoard.getSquare(i).hasTile()) occupiedSquares.add(copyBoard.getSquare(i));
        }

        for (Square square : playSquares) {
            occupiedSquares.remove(square);
        }
        return occupiedSquares;
    }

    private  void getNextValidSquares(List<Square> playSquares, String direction, Board copyBoard) {
        List<Square> occupiedSquares = getOccupiedSquare(copyBoard,playSquares);
//        List<Square> nextValidSquares = new ArrayList<>();

//        if (occupiedSquares.size() == 0) return null;

        for (int i = 0; i < playSquares.size(); i++) {
            Square currentSquare = playSquares.get(i);
            try {
                if (currentSquare.getxPosition() >= 0 && currentSquare.getxPosition() < 15 &&
                        currentSquare.getyPosition() >= 0 && currentSquare.getyPosition() < 15) {
                    if(direction.equals("H")) {
                        if (i == 0 && copyBoard.getSquareLeft(currentSquare).getTile() == null)
                            nextValidSquares.add(copyBoard.getSquareLeft(currentSquare));

                        else if (i == playSquares.size() -1 && copyBoard.getSquareRight(currentSquare).getTile() == null)
                            nextValidSquares.add(copyBoard.getSquareRight(currentSquare));

                        if (copyBoard.getSquareAbove(currentSquare).getTile() == null)
                            nextValidSquares.add(copyBoard.getSquareAbove(currentSquare));
                        if (copyBoard.getSquareBelow(currentSquare).getTile() == null)
                            nextValidSquares.add(copyBoard.getSquareBelow(currentSquare));
                    }
                    else {
                        if (i == 0 && copyBoard.getSquareAbove(currentSquare).getTile() == null)
                            nextValidSquares.add(copyBoard.getSquareAbove(currentSquare));

                        else if (i == playSquares.size() -1 &&  copyBoard.getSquareBelow(currentSquare).getTile() == null)
                            nextValidSquares.add(copyBoard.getSquareBelow(currentSquare));

                        if (copyBoard.getSquareRight(currentSquare).getTile() == null)
                            nextValidSquares.add(copyBoard.getSquareRight(currentSquare));
                        if (copyBoard.getSquareLeft(currentSquare).getTile() == null)
                            nextValidSquares.add(copyBoard.getSquareLeft(currentSquare));
                    }

                }

            } catch (NullPointerException e) {
                continue;
            }

        }
//        return nextValidSquares;
    }

    private boolean isValidPlacement(List<Square> playSquares, String direction, Board copyBoard){
        List<Square> occupiedSquares = getOccupiedSquare(copyBoard,playSquares);
        getNextValidSquares(playSquares, direction, copyBoard);
        Square centralSquare = copyBoard.getSquare("H7");
        if (usedWords.size() == 0 && playSquares.contains(centralSquare)) return true;
        for (Square playSquare: playSquares){
            if (usedWords.size() != 0) {
                for (Square validSquare : nextValidSquares) {
                    if (validSquare.getLocation().equals(playSquare.getLocation())) return true;
                }
                if (occupiedSquares.contains(playSquare)) return false;
            }
        }
        return false;
    }

}
