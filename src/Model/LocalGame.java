package Model;

/**
 * This class implements the basic game functions of Scrabble.
 * @author Hung Nguyen, Nhat Tran
 * @version 0.1
 */

import View.LocalView;

import java.io.IOException;
import java.util.*;

public class LocalGame extends Game {
    private Player[] players;
    private LocalView UI;

    /**
     * Creates a new game
     * @param players number of players
     * @param UI the local view.
     */
    public LocalGame(Player[] players, LocalView UI) {
        super();
        this.numPlayer = players.length;
        this.UI = UI;
        nextValidSquares = new ArrayList<>();
        this.players = players;


        for (Player player : players)
        {
            ArrayList<Tile> tray = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                int j = new Random().nextInt(tileBag.size());
                Tile tile = tileBag.get(j);
                tileBag.remove(tile);
                tray.add(tile);
            }
            player.setTray(tray);
        }
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


        for (Player player : players) {
            var finalPoints = 0;
            if (tilesLeft.size() == 0) {
                int totalDeductPoints = finalDeduct.get(players[0]) + finalDeduct.get(players[1])
                        + finalDeduct.get(players[2]) + finalDeduct.get(players[3]);
                finalPoints = player.getTotalPoints() + totalDeductPoints;
            } else {
                finalPoints = player.getTotalPoints() - finalDeduct.get(player);
            }
            player.setFinalPoints(finalPoints);
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

    /**
     * add Tile to each player's tray
     * @param player which player to add tile to.
     */
    private void addTileToTray(Player player) {
        ArrayList<Tile> tray = player.getTray();
        int bagSize = tileBag.size();
        int missingTile = bagSize == 0 ? 0 : Math.min(bagSize, (7 - tray.size()));

        for (int i = 0; i < missingTile; i++) {
            bagSize = tileBag.size();
            int j = new Random().nextInt(bagSize);
            Tile tile = tileBag.get(j);
            tileBag.remove(tile);
            tray.add(tile);
        }
        player.setTray(tray);
    }

    protected boolean isEmptyTrayAndBag() {
        return players[currentPlayer].getTray().isEmpty() && tileBag.isEmpty();
    }

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

    protected LinkedHashMap<String, String> mapLetterToSquare(String[] move){
        LinkedHashMap<String , String > letterToSquare = new LinkedHashMap<>();
        for (int i = 0; i < move.length; i++) {
            String[] letterSquarePair = move[i].split("[.]");
            if (letterSquarePair.length < 2) {
                return null;
            }
            String[] coordinate = letterSquarePair[1].split("");
            if (coordinate.length > 3) {
                System.out.println("Coordinate's length should not be greater than 3.");
                return null;
            }
            for (int j = 1; j < coordinate.length; j++) {
                try {
                    Integer.parseInt(coordinate[j]);
                } catch (NumberFormatException e) {
                    System.out.println("Wrong input format. Move should be D-H7 O-H8 G-H9 and so on.");
                    return null;
                }
            }
            letterToSquare.put(letterSquarePair[1], letterSquarePair[0]);
        }
        return letterToSquare;
    }

    public void swapTray(String[] chars) {
        if (tileBag.isEmpty()) incrementPassCount();
        else {
            ArrayList<Tile> shuffledTiles = new ArrayList<>();
            for (String character : chars) {
                Tile tile = determineTileFromString(character);
                shuffledTiles.add(tile);
            }
            for (Tile tile: shuffledTiles) {
                tileBag.add(tile);
                players[currentPlayer].getTray().remove(tile);
            }
            addTileToTray(players[currentPlayer]);
            resetPassCount();

        }
    }

    protected Tile determineTileFromString(String character) {
        ArrayList<Tile> tray = players[currentPlayer].getTray();
        for (Tile tile: tray){
            if (character.equals("-") && character.equals(Character.toString(tile.getLetter())) ) {
                String prompt = "Please choose one of the letters below:\n"
                        + "A B C D E F G H I J K L M N O P Q R S T U V W X Y Z\n";
                try{
                    char input = UI.getChar(prompt);
                    tile.setLetter(input);
                    return tile;

                } catch (IllegalArgumentException | IOException e) {
                    return null;
                }
            }
            else if (character.equals(Character.toString(tile.getLetter()))) {
                return tile;
            }
        }
        return null;
    }

    protected Board isValidMove(LinkedHashMap<String, String> moves) {
        if (moves == null) {
            return null;
        }
        Board copyBoard = board.clone();
        String direction = determineMoveDirection(moves);

        ArrayList<Square> initialWord = new ArrayList<>();
        ArrayList<Square> playSquares = new ArrayList<>();

        for (Map.Entry<String, String> move : moves.entrySet()) {
            String character = move.getValue();
            Square location = copyBoard.getSquare(move.getKey());
            Tile tile = determineTileFromString(character);
            if (tile == null) {
                return null;
            }
            location.setTile(tile);
            initialWord.add(location);
            playSquares.add(location);
        }

        //Check the player choice of square validation
        if (!isValidPlacement(playSquares, direction, copyBoard)) return null;

        ArrayList<ArrayList<Square>> wordCombinations =
                determinePossibleWordCombinations(initialWord, direction,copyBoard);

        if (wordCombinations == null || wordCombinations.size() == 0) {
            UI.showMessage("Wrong input format.");
            return null;
        }
        int turnScore = 0;
        for (ArrayList<Square> wordCombination : wordCombinations) {
            String validWord = wordChecker(wordCombination);
            if (validWord == null) {
                UI.showMessage("The word " + getWordFromSquareList(wordCombination) + " is invalid. Skipping your turn...");
                return null;
            }
            turnScore += calculatePoints(wordCombination);
        }
        if (initialWord.size() == 7) {
            turnScore += 50;
        }
        players[currentPlayer].addPoints(turnScore);
        for (Square square : initialWord) {
            ArrayList<Tile> tray = players[currentPlayer].getTray();
            tray.remove(square.getTile());
            square.setType(SquareType.NORMAL);
        }
        addTileToTray(players[currentPlayer]);
        updateValidSquares(playSquares, direction, copyBoard);
        return copyBoard;
    }

    /**
     * Get the word representing by the ArrayList of Squares.
     * @param squares the Squares containing the word/
     * @return the word.
     */
    public String getWordFromSquareList(ArrayList<Square> squares) {
        String word = "";
        for (Square square : squares)  word +=square.getTile().getLetter();
        return word;
    }
}
