package WordChecker.main.java;

public interface ScrabbleWordChecker {
    /**
     * Method can be used to check if a word is a valid Scrabble word
     * according to the Collins Scrabble Dictionary
     *
     * @param word The word you want to check
     * @return  a WordReponse containing the requested word and a description if the word can be found in dictionary;
     *          null when the word isn't valid during a game of scrabble
     */
    WordResponse isValidWord(String word);

    /**
     * Response object for when check a word.
     * Contains the actual word, together with a description of the word according to the collins scrabble Dicitonary
     */
    class WordResponse {
        public WordResponse(String word, String description){
            this.word = word;
            this.description = description;
        }

        public String getWord(){
            return this.word;
        }
        private String word;

        public String getDescription(){
            return this.description;
        }
        private String description;

        public String toString(){
            return this.getWord() + ": " + this.getDescription();
        }
    }
}
