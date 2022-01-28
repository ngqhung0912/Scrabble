package View;

import java.io.*;

public class LocalView extends View {
    private static BufferedReader bf;
     public LocalView() {
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
