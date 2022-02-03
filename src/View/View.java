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
     * @param message the message to send.
     */
    void showMessage(String message);

    /**
     * Prompt to get the user's input as string from the terminal.
     * @param prompt the prompt to ask the user.
     * @return String input arguments.
     * @throws IOException when there is input/output error.
     */
    String getString(String prompt) throws IOException;
}
