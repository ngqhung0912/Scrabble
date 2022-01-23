package WordChecker.main.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A Scrabble Word Checker, which walks through the words file on every check.
 * This this uses less memory, but more IO
 */
public class FileStreamScrabbleWordChecker implements  ScrabbleWordChecker{

    @Override
    public WordResponse isValidWord(String word) {
        if(word == null || word.isBlank()) return null;
        word = word.toUpperCase();

        try {
            // Open the words files
            InputStream resourceStream = FileStreamScrabbleWordChecker.class.getResourceAsStream("collins_scrabble_words_2019.txt");
            try (BufferedReader br = new BufferedReader(new InputStreamReader(resourceStream))) {
                String line;
                // Walk through the file
                while ((line = br.readLine()) != null) {
                    String[] splitLine = line.split("\t");

                    // Return a WordResponse when the word is found in the file
                    if (splitLine.length == 2 && word.equals(splitLine[0])) {
                        return new WordResponse(splitLine[0], splitLine[1]);
                    }
                }
            }
        }
        catch(IOException exception){
            System.out.println("Could not load scrabble words: " + exception.getMessage());
            exception.printStackTrace();
        }

        return null;
    }
}
