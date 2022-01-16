package WordChecker.main.java;

import java.util.Scanner;

/**
 * A class to test the two WordChecker instances.
 * The application asks on the System.IN the checker to use, and the word to check.
 */
public class TestChecker {
    public static void main(String[] args) {

        // Initialize both checkers
        ScrabbleWordChecker fileStreamChecker = new FileStreamScrabbleWordChecker();
        ScrabbleWordChecker inMemoryChecker = new InMemoryScrabbleWordChecker();

        // Print instructions on the Screen
        System.out.println("Please enter a word to check it against the collins dictionary.");
        System.out.println("There are two checkers available:");
        System.out.println(" - memory: this loads all words in memory (more memory, but less IO)");
        System.out.println(" - file: this checks the words files everytime (less memory, but more IO)");
        System.out.println();
        System.out.println("Usage: [checker:stream/memory] [Word]");
        System.out.println();
        System.out.print("> ");

        // Process the System.in input
        Scanner in = new Scanner(System.in);
        while(in.hasNextLine()){

            // Process the input on the line
            String input = in.nextLine();
            String[] splittedInput = input.split(" ");

            // Close the application with "exit"
            if("exit".equals(input)){
                System.out.println("The application will be closed");
                break;
            }
            // If people give != 2 arguments, we should just inform them of the wrong input
            else if(splittedInput.length != 2){
                System.out.println("Invalid input, expects: [Checker:stream/memory] [Word]");
            }
            else {

                // Determine the checker to use
                ScrabbleWordChecker checker;
                switch (splittedInput[0]){
                    case "stream":
                        checker = fileStreamChecker;
                        break;
                    case "memory":
                        checker = inMemoryChecker;
                        break;
                    default:
                        checker = null;
                }

                // When an invalid checker input is given
                if(checker == null){
                    System.out.println("Invalid checker, expects: [Checker:stream/memory] [Word]");
                }
                // Use the chosen checker to validate the word.
                else{
                    ScrabbleWordChecker.WordResponse response = checker.isValidWord(splittedInput[1]);
                    if(response == null){
                        System.out.println("The word \"" + splittedInput[1] + "\" is not known in the dictionary!");
                    }
                    else{
                        System.out.println(response);
                    }
                }
            }

            // Indicate on the System.out we are waiting for input again
            System.out.print(System.lineSeparator() + "> ");
        }
    }
}
