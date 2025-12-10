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
        cipherBox.setStyle(Styles.DIALOG_PADDING);

        Label titleLabel = new Label(isEncrypting ? "Choose encryption method:" : "Choose decryption method:");
        titleLabel.setStyle(Styles.DIALOG_TITLE);

        Button caesarBtn = new Button("Caesar Cipher");
        caesarBtn.setPrefWidth(200);
        caesarBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (isEncrypting) {
                    boolean confirmed = DialogHelper.showConfirm("Caesar Cipher Warning",
                            "⚠️ WARNING: Caesar cipher does not verify keys!\n\n" +
                                    "If you forget your encryption key, your data will be PERMANENTLY LOST.\n\n" +
                                    "Consider using AES encryption for important data.\n\n" +
                                    "Do you want to continue?");

                    if (confirmed) {
                        try {
                            CryptoHelper.encryptFile(file, key, (byte) 1);
                            DialogHelper.showSuccess("Caesar Encryption", "File encrypted successfully!");
                        } catch (Exception e) {
                            DialogHelper.showError("Encryption Error", e.getMessage());
                        }
                        cipherStage.close();
                    }
                    // If canceled, dialog just closes and returns to cipher selection
                } else {
                    try {
                        CryptoHelper.decryptFile(file, key);
                        DialogHelper.showSuccess("Caesar Decryption", "File decrypted successfully!");
                    } catch (Exception e) {
                        DialogHelper.showError("Decryption Error", e.getMessage());
                    }
                    cipherStage.close();
                }
            }
        });

        Button xorBtn = new Button("XOR Cipher");
        xorBtn.setPrefWidth(200);
        xorBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (isEncrypting) {
                    boolean confirmed = DialogHelper.showConfirm("XOR Cipher Warning",
                            "⚠️ WARNING: XOR cipher does not verify keys!\n\n" +
                                    "If you forget your encryption key, your data will be PERMANENTLY LOST.\n\n" +
                                    "Consider using AES encryption for important data.\n\n" +
                                    "Do you want to continue?");

                    if (confirmed) {
                        try {
                            CryptoHelper.encryptFile(file, key, (byte) 2);
                            DialogHelper.showSuccess("XOR Encryption", "File encrypted successfully!");
                        } catch (Exception e) {
                            DialogHelper.showError("Encryption Error", e.getMessage());
                        }
                        cipherStage.close();
                    }
                    // If canceled, dialog just closes and returns to cipher selection
                } else {
                    try {
                        CryptoHelper.decryptFile(file, key);
                        DialogHelper.showSuccess("XOR Decryption", "File decrypted successfully!");
                    } catch (Exception e) {
                        DialogHelper.showError("Decryption Error", e.getMessage());
                    }
                    cipherStage.close();
                }
            }
        });

        Button aesBtn = new Button("AES Encryption");
        aesBtn.setPrefWidth(200);
        aesBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (isEncrypting) {
                    try {
                        CryptoHelper.encryptFile(file, key, (byte) 3);
                        DialogHelper.showSuccess("AES Encryption", "File encrypted successfully!");
                    } catch (Exception e) {
                        DialogHelper.showError("Encryption Error", e.getMessage());
                    }
                } else {
                    try {
                        CryptoHelper.decryptFile(file, key);
                        DialogHelper.showSuccess("AES Decryption", "File decrypted successfully!");
                    } catch (Exception e) {
                        DialogHelper.showError("Decryption Error", e.getMessage());
                    }
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
