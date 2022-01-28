package NetworkController;

import java.io.PrintWriter;

public class ServerTUI {
    private PrintWriter console;

    public ServerTUI(){
        console = new PrintWriter(System.out, true);
    }

    public void showMessage(String msg){
        console.println(msg);
    }
}
