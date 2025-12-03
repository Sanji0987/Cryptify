import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javax.crypto.SecretKey;
import java.io.File;

public class CipherSelector {

    /**
     * Shows the cipher selection window.
     */
    public static void show(Stage owner, File file, SecretKey key,
            ObservableList<String> fileList, boolean isEncrypting) {
        Stage cipherStage = new Stage();
        cipherStage.setTitle(isEncrypting ? "Select Encryption Cipher" : "Select Decryption Cipher");

        VBox cipherBox = new VBox(15);
        cipherBox.setAlignment(Pos.CENTER);
        cipherBox.setStyle("-fx-padding: 20;");

        Label titleLabel = new Label(isEncrypting ? "Choose encryption method:" : "Choose decryption method:");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Button caesarBtn = new Button("Caesar Cipher");
        caesarBtn.setPrefWidth(200);
        caesarBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (isEncrypting) {
                    CryptoUtils.encryptCaesar(file, key, fileList);
                } else {
                    CryptoUtils.decryptCaesar(file, key, fileList);
                }
                cipherStage.close();
            }
        });

        Button xorBtn = new Button("XOR Cipher");
        xorBtn.setPrefWidth(200);
        xorBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (isEncrypting) {
                    CryptoUtils.encryptXOR(file, key, fileList);
                } else {
                    CryptoUtils.decryptXOR(file, key, fileList);
                }
                cipherStage.close();
            }
        });

        Button aesBtn = new Button("AES Encryption");
        aesBtn.setPrefWidth(200);
        aesBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (isEncrypting) {
                    CryptoUtils.encryptAES(file, key, fileList);
                } else {
                    CryptoUtils.decryptAES(file, key, fileList);
                }
                cipherStage.close();
            }
        });

        cipherBox.getChildren().addAll(titleLabel, caesarBtn, xorBtn, aesBtn);

        Scene cipherScene = new Scene(cipherBox, 300, 250);
        cipherStage.setScene(cipherScene);
        cipherStage.setX(owner.getX() + owner.getWidth() + 10);
        cipherStage.setY(owner.getY());
        cipherStage.show();
    }
}
