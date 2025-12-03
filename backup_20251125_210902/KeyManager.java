import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class KeyManager {

    private SecretKey secretKey;

    /**
     * Gets the current encryption key.
     */
    public SecretKey getSecretKey() {
        return secretKey;
    }

    /**
     * Checks if a key has been set.
     */
    public boolean hasKey() {
        return secretKey != null;
    }

    /**
     * Derives an AES key from a password using SHA-256.
     */
    public SecretKey deriveKey(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return new SecretKeySpec(hash, "AES");
        } catch (Exception e) {
            DialogHelper.showError("Key Error", "Could not derive key: " + e.getMessage());
            return null;
        }
    }

    /**
     * Shows the key entry window.
     */
    public void showKeyWindow(Stage owner) {
        Stage keyStage = new Stage();
        keyStage.setTitle("Enter Key");
        VBox keyBox = new VBox(10);
        keyBox.setAlignment(Pos.CENTER);

        Label keyLabel = new Label("Enter Encryption Key:");
        PasswordField keyField = new PasswordField();
        keyField.setMaxWidth(200);

        Button confirmBtn = new Button("Confirm");
        confirmBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String rawKey = keyField.getText().trim();
                if (rawKey.isEmpty()) {
                    DialogHelper.showError("Invalid Key", "Encryption key cannot be empty!");
                } else {
                    secretKey = deriveKey(rawKey);
                    if (secretKey != null) {
                        System.out.println("Key saved securely.");
                        keyStage.close();
                    }
                }
            }
        });

        keyBox.getChildren().addAll(keyLabel, keyField, confirmBtn);
        Scene keyScene = new Scene(keyBox, 300, 150);
        keyStage.setScene(keyScene);
        keyStage.setX(owner.getX() + owner.getWidth() + 10);
        keyStage.setY(owner.getY());
        keyStage.show();
    }
}
