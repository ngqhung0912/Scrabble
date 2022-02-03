package Exceptions;
/**
 * @author Nhat Tran
 */

public class ExitProgram extends Exception{
    /**
     * ExitProgram Exception throws when the client is unable to connect to the server.
     * @param message message to display.
     */
    public ExitProgram(String message) {
        super(message);
    }
}
