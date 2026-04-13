import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

public class FileManager implements IFileManager {

    private final ObservableList<String> fileList;

    public FileManager(ObservableList<String> fileList) {
        this.fileList = fileList;
    }

    public void addFile(String filePath) {
        if (!fileList.contains(filePath)) {
            fileList.add(filePath);
        }
    }

    public void deleteSelected(ListView<String> listView) {
        String selectedItem = listView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            fileList.remove(selectedItem);
        } else {
            DialogHelper.showError("Selection Error", "Nothing selected to delete.");
        }
    }

    public void clearAll() {
        fileList.clear();
    }

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

}
