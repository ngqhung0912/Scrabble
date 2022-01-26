package Networking;

import Exceptions.ExitProgram;
import Exceptions.ServerUnavailableException;
import Game.Player;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    private Socket serverSock;
    private BufferedReader in;
    private BufferedWriter out;

    public Client() {
        ClientTUI view = new ClientTUI(this);
    }

    public void start(){

    }

    public void ClientSideConnection() throws ExitProgram {
        clearConnection();
        while (serverSock == null) {
            String host = "localhost";
            int port = 0;

            try {
                InetAddress address = InetAddress.getByName(host);
                System.out.println("Attempting to connect to " + address + ":"
                        + port + "...");
                serverSock = new Socket(address, port);
                in = new BufferedReader(new InputStreamReader(serverSock.getInputStream()));
                out = new BufferedWriter(new OutputStreamWriter(serverSock.getOutputStream()));

            } catch (IOException e) {
                System.out.println("ERROR: could not create a socket on "
                        + host + " and port " + port + ".");
            }


        }
    }

    public void clearConnection() {
        serverSock = null;
        in = null;
        out = null;
    }

    public synchronized void sendMessage(String msg) throws ServerUnavailableException {
        if (out != null) {
            try {
                out.write(msg);
                out.newLine();
                out.flush();
            } catch (IOException e) {
                System.out.println("Cannot read input. Please try again");
            }
        }
        else {
            throw new ServerUnavailableException("Server not detected.");
        }
    }

    public String readFromServer() throws ServerUnavailableException{
        if (in != null) {
            try {
                // Read and return answer from Server
                String answer = in.readLine();
                if (answer == null) {
                    throw new ServerUnavailableException("Server not detected.");
                }
                return answer;
            } catch (IOException e) {
                throw new ServerUnavailableException("Server not detected.");
            }
        } else {
            throw new ServerUnavailableException("Server not detected.");
        }
    }

//    public String readMultipleLinesFromServer()
//            throws ServerUnavailableException {
//        if (in != null) {
//            try {
//                // Read and return answer from Server
//                StringBuilder sb = new StringBuilder();
//                for (String line = in.readLine(); line != null
//                        && !line.equals(ProtocolMessages.EOT);
//                     line = in.readLine()) {
//                    sb.append(line + System.lineSeparator());
//                }
//                return sb.toString();
//            } catch (IOException e) {
//                throw new ServerUnavailableException("Could not read "
//                        + "from server.");
//            }
//        } else {
//            throw new ServerUnavailableException("Could not read "
//                    + "from server.");
//        }
//    }

    public void closeConnection(){
        System.out.println("Closing the connection...");
        try {
            serverSock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void doMove(){

    }

    public void doPass(){

    }

    public void doShuffle(){

    }

    public void doAbort(){

    }
}
