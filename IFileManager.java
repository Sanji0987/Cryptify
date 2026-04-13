import javafx.scene.control.ListView;
import javafx.stage.Stage;

public interface IFileManager {
    void addFile(String filePath);
    void deleteSelected(ListView<String> listView);
    void clearAll();
    void openFileChooser(Stage owner);
}
