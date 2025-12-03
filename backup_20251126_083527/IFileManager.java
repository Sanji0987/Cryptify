import javafx.scene.control.ListView;
import javafx.stage.Stage;

/**
 * Interface for file management operations.
 * 
 * Design Pattern: Interface Segregation Principle
 * OOP Benefits:
 * - Abstraction: Defines what, not how
 * - Flexibility: Multiple implementations possible
 * - Testability: Easy to create mock implementations
 * - Decoupling: Clients depend on interface, not implementation
 */
public interface IFileManager {

    /**
     * Adds a file to the managed list.
     * 
     * @param filePath The absolute path of the file
     */
    void addFile(String filePath);

    /**
     * Deletes the selected file from the list.
     * 
     * @param listView The ListView containing the selection
     */
    void deleteSelected(ListView<String> listView);

    /**
     * Clears all files from the list.
     */
    void clearAll();

    /**
     * Opens a file chooser dialog.
     * 
     * @param owner The parent window
     */
    void openFileChooser(Stage owner);

    /**
     * Checks if a file is in the list.
     * 
     * @param path The file path to check
     * @return true if file is in list
     */
    boolean isFileInList(String path);
}
