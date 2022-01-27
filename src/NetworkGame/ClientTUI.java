package NetworkGame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ClientTUI {
    Client client;
    BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));

    public ClientTUI(Client client) {
        this.client = client;
    }


    public void showMessage(String message) {
        System.out.println(message);
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

    public String getString(String question) throws IOException {
        System.out.println(question);
        String line = bf.readLine();

        return line;
    }



}
