package View;
import java.io.IOException;

/**
 * This class is the abstract implementation of the view.
 * @author Hung Nguyen
 * @version finale
 */
public interface View {
    /**
     * show the message to the terminal
     * @param message
     */
    void showMessage(String message);

    /**
     * Prompt to get the user's input as string from the terminal.
     * @param prompt
     * @return String input arguments.
     * @throws IOException
     */
    String getString(String prompt) throws IOException;
}
