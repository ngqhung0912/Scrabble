package TUI;

import Exceptions.ExitProgram;
import Exceptions.ServerUnavailableException;
import GameLogic.Board;
import GameLogic.Tile;
import LocalGame.LocalPlayer;

import java.io.*;
import java.util.ArrayList;

public class LocalView {

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
     public void start() throws ServerUnavailableException {

     }

     public void handleUserInput(String input) throws ExitProgram, ServerUnavailableException {

     }

 }
