package NetworkController;

import Exceptions.ExitProgram;
import Exceptions.ServerUnavailableException;
import View.NetworkView;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    private Socket serverSock;
    private BufferedReader in;
    private static BufferedWriter out;
    private NetworkView view;

    public Client() {
        view = new NetworkView();
    }

    public void start() throws ExitProgram, IOException, ServerUnavailableException {
        clientSideConnection();
        while (true) {
            sendMessage(handleUserInput());
        }
    }

    public void clientSideConnection() throws ExitProgram {
        clearConnection();
        while (serverSock == null) {
            String host = "localhost";
            int port = 8888;

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

    public String handleUserInput() throws IOException {
        String prompt = "Say hello!";
        return view.getString(prompt);
    }


    public static synchronized void sendMessage(String msg) throws ServerUnavailableException {
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

    public static void main(String[] args)  {
        Client client = new Client();
        try {
            client.start();
        } catch (ExitProgram e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServerUnavailableException e) {
            e.printStackTrace();
        }
    }
}
