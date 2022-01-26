package TUI;

import java.io.*;

 public class PlayerView {

    private static BufferedReader bf;

     public PlayerView() {
         bf = new BufferedReader(new InputStreamReader(System.in));
     }

     public void showMessage(String message) {
         System.out.println(message);
     }
     public String getString(String prompt) throws IOException {
         showMessage(prompt);
         return bf.readLine();
     }

     public char getChar(String prompt) throws IOException {
         showMessage(prompt);
         return bf.readLine().charAt(0);
     }
 }
