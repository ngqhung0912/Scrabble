package View;

import Exceptions.ExitProgram;
import Exceptions.ServerUnavailableException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

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

    public boolean getBoolean(String prompt) throws IOException{
        showMessage(prompt);
        return bf.readLine().equals("Y") || bf.readLine().equals("y");
    }

    public InetAddress getIp() throws IOException {
        InetAddress addr = null;
        while (addr == null) {
            String host = getString("Insert IP address");
            try {
                addr = InetAddress.getByName(host);
            } catch (UnknownHostException e) {
                showMessage("Invalid IP address");
            }
        }
        return addr;
    }
}
