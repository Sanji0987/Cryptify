import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

public class FileManager {

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
            System.out.println("File added: " + filePath);
        }
    }

    /**
     * Deletes the selected file from the list.
     */
    public void deleteSelected(ListView<String> listView) {
        String selectedItem = listView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            fileList.remove(selectedItem);
            System.out.println("Removed file from list: " + selectedItem);
        } else {
            DialogHelper.showError("Selection Error", "Nothing selected to delete.");
        }
    }

    /**
     * Clears all files from the list.
     */
    public void clearAll() {
        fileList.clear();
        System.out.println("File list cleared.");
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
