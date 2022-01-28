package View;

import Exceptions.ExitProgram;
import Exceptions.ServerUnavailableException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class NetworkView extends View {

   private static BufferedReader bf;

    public NetworkView() {

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
    public void start() throws ServerUnavailableException {

    }

    public void handleUserInput(String input) throws ExitProgram, ServerUnavailableException {

    }

}
