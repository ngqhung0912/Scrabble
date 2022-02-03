package Exceptions;

/**
 * @author Nhat Tran
 */
public class ServerUnavailableException extends Exception{
    /**
     * ServerUnavailable Exception throws when the client tries to connet witht the offline
     * server.
     * @param message message to display.
     */

    public ServerUnavailableException(String message){
        super(message);
    }
}
