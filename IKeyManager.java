import javafx.stage.Stage;
import javax.crypto.SecretKey;

public interface IKeyManager {
    SecretKey getKey();
    boolean hasKey();
    void showKeyWindow(Stage owner);
    SecretKey deriveKey(String password);
}
