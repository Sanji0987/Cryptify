import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

/**
 * File manager implementation.
 * 
 * OOP Principle: Interface Implementation
 * Benefit: Can be replaced with different implementation without changing
 * client code
 */
public class FileManager implements IFileManager {

    private ObservableList<String> fileList;

    /**
     * Constructor
     */
    public FileManager(ObservableList<String> fileList) {
        this.fileList = fileList;
    }

    /**
     * Adds a file to the list.
     */
    public void addFile(String filePath) {
        if (!fileList.contains(filePath)) {
            fileList.add(filePath);
        }
    }

    /**
     * Deletes the selected file from the list.
     */
    public void deleteSelected(ListView<String> listView) {
        String selectedItem = listView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            fileList.remove(selectedItem);
        } else {
            DialogHelper.showError("Selection Error", "Nothing selected to delete.");
        }
    }

    /**
     * Clears all files from the list.
     */
    public void clearAll() {
        fileList.clear();
    }

    /**
     * Opens a file chooser dialog and adds the selected file to the list.
     */
    public void openFileChooser(Stage owner) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Supported Files", "*.txt", "*.enc"));
        File selectedFile = fileChooser.showOpenDialog(owner);
        if (selectedFile != null) {
            addFile(selectedFile.getAbsolutePath());
        }
    }

    /**
     * Checks if a file path is in the list.
     */
    public boolean isFileInList(String path) {
        return fileList.contains(path);
    }
}
