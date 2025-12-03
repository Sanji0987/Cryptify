
// --- CryptoDrop Main Application File (Full Refresh) ---
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {

      private ObservableList<String> uiFileList = FXCollections.observableArrayList();
      private javax.crypto.SecretKey secretKey = null;

      public javax.crypto.SecretKey getSecretKey() {
            return secretKey;
      }

      private javax.crypto.SecretKey deriveKey(String password) {
            try {
                  java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
                  byte[] hash = digest.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                  return new javax.crypto.spec.SecretKeySpec(hash, "AES");
            } catch (Exception e) {
                  showError("Key Error", "Could not derive key: " + e.getMessage());
                  return null;
            }
      }

      private void showError(String title, String message) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
      }

      private void deleteSelectedFile(ListView<String> listView) {
            String selectedItem = listView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                  uiFileList.remove(selectedItem);
                  System.out.println("Removed file from list: " + selectedItem);
            } else {
                  showError("Selection Error", "Nothing selected to delete.");
            }
      }

      private void openFileChooser(Stage primaryStage) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select TXT File");
            fileChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("Supported Files", "*.txt", "*.enc"));
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                  uiFileList.add(selectedFile.getAbsolutePath());
                  System.out.println("File added to list: " + selectedFile.getAbsolutePath());
            }
      }

      private void openKeyWindow(Stage ownerStage) {
            Stage keyStage = new Stage();
            keyStage.setTitle("Enter Key");
            VBox keyBox = new VBox(10);
            keyBox.setAlignment(Pos.CENTER);
            Label keyLabel = new Label("Enter Encryption Key:");
            javafx.scene.control.PasswordField keyField = new javafx.scene.control.PasswordField();
            keyField.setMaxWidth(200);
            Button confirmBtn = new Button("Confirm");
            confirmBtn.setOnAction(new EventHandler<ActionEvent>() {
                  @Override
                  public void handle(ActionEvent event) {
                        String rawKey = keyField.getText().trim();
                        if (rawKey.isEmpty()) {
                              showError("Invalid Key", "Encryption key cannot be empty!");
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
            keyStage.setX(ownerStage.getX() + ownerStage.getWidth() + 10);
            keyStage.setY(ownerStage.getY());
            keyStage.show();
      }

      private void openCipherSelectionWindow(Stage ownerStage, File selectedFile, boolean isEncrypting) {
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
                              CryptoUtils.encryptCaesar(selectedFile, secretKey, uiFileList);
                        } else {
                              CryptoUtils.decryptCaesar(selectedFile, secretKey, uiFileList);
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
                              CryptoUtils.encryptXOR(selectedFile, secretKey, uiFileList);
                        } else {
                              CryptoUtils.decryptXOR(selectedFile, secretKey, uiFileList);
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
                              CryptoUtils.encryptAES(selectedFile, secretKey, uiFileList);
                        } else {
                              CryptoUtils.decryptAES(selectedFile, secretKey, uiFileList);
                        }
                        cipherStage.close();
                  }
            });

            cipherBox.getChildren().addAll(titleLabel, caesarBtn, xorBtn, aesBtn);

            Scene cipherScene = new Scene(cipherBox, 300, 250);
            cipherStage.setScene(cipherScene);
            cipherStage.setX(ownerStage.getX() + ownerStage.getWidth() + 10);
            cipherStage.setY(ownerStage.getY());
            cipherStage.show();
      }

      @Override
      public void start(Stage primaryStage) {
            VBox mainBox = new VBox(20);
            mainBox.setAlignment(Pos.CENTER);

            VBox dropZone = new VBox();
            dropZone.setPrefSize(350, 100);
            dropZone.setAlignment(Pos.CENTER);
            dropZone.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.DASHED, CornerRadii.EMPTY,
                        new BorderWidths(2))));
            dropZone.getChildren().add(new Label("[ DRAG TXT FILES HERE ]"));

            dropZone.setOnDragOver(new EventHandler<DragEvent>() {
                  @Override
                  public void handle(DragEvent event) {
                        if (event.getDragboard().hasFiles()) {
                              event.acceptTransferModes(TransferMode.COPY);
                        }
                        event.consume();
                  }
            });

            dropZone.setOnDragDropped(new EventHandler<DragEvent>() {
                  @Override
                  public void handle(DragEvent event) {
                        Dragboard db = event.getDragboard();
                        boolean success = false;
                        if (db.hasFiles()) {
                              success = true;
                              for (File file : db.getFiles()) {
                                    String name = file.getName().toLowerCase();
                                    if (name.endsWith(".txt") || name.endsWith(".enc")) {
                                          uiFileList.add(file.getAbsolutePath());
                                          System.out.println("Dropped: " + file.getAbsolutePath());
                                    }
                              }
                        }
                        event.setDropCompleted(success);
                        event.consume();
                  }
            });

            Button clearListBtn = new Button("Clear List");
            clearListBtn.setMaxWidth(100);
            clearListBtn.setOnAction(new EventHandler<ActionEvent>() {
                  @Override
                  public void handle(ActionEvent event) {
                        uiFileList.clear();
                  }
            });

            ListView<String> fileListView = new ListView<>(uiFileList);
            fileListView.setMaxHeight(150);
            fileListView.setMaxWidth(350);

            Button fileAdderBtn = new Button("Add File");
            fileAdderBtn.setOnAction(new EventHandler<ActionEvent>() {
                  @Override
                  public void handle(ActionEvent event) {
                        openFileChooser(primaryStage);
                  }
            });

            Button deleteBtn = new Button("Delete Selected");
            deleteBtn.setOnAction(new EventHandler<ActionEvent>() {
                  @Override
                  public void handle(ActionEvent event) {
                        deleteSelectedFile(fileListView);
                  }
            });

            Button keyAdderBtn = new Button("Set Key");
            keyAdderBtn.setOnAction(new EventHandler<ActionEvent>() {
                  @Override
                  public void handle(ActionEvent event) {
                        openKeyWindow(primaryStage);
                  }
            });

            Button encryptBtn = new Button("ENCRYPT Selected");
            encryptBtn.setOnAction(new EventHandler<ActionEvent>() {
                  @Override
                  public void handle(ActionEvent event) {
                        String selectedFile = fileListView.getSelectionModel().getSelectedItem();
                        if (selectedFile == null) {
                              showError("No File Selected", "Please select a file from the list to encrypt.");
                              return;
                        }
                        if (secretKey == null) {
                              showError("No Key Set", "Please set an encryption key first!");
                              return;
                        }
                        openCipherSelectionWindow(primaryStage, new File(selectedFile), true);
                  }
            });

            Button decryptBtn = new Button("DECRYPT Selected");
            decryptBtn.setOnAction(new EventHandler<ActionEvent>() {
                  @Override
                  public void handle(ActionEvent event) {
                        String selectedFile = fileListView.getSelectionModel().getSelectedItem();
                        if (selectedFile == null) {
                              showError("No File Selected", "Please select a file from the list to decrypt.");
                              return;
                        }
                        if (secretKey == null) {
                              showError("No Key Set", "Please set a decryption key first!");
                              return;
                        }
                        CryptoUtils.decryptAuto(new File(selectedFile), secretKey, uiFileList);
                  }
            });

            mainBox.getChildren().addAll(
                        dropZone,
                        clearListBtn,
                        fileListView,
                        fileAdderBtn,
                        deleteBtn,
                        keyAdderBtn,
                        encryptBtn,
                        decryptBtn);

            Scene scene = new Scene(mainBox, 400, 700, Color.WHITE);
            primaryStage.setTitle("CryptoDrop - TXT Encryptor");
            primaryStage.setScene(scene);
            primaryStage.show();
      }

      @Override
      public void stop() {
            System.out.println("Application closing...");
      }

      public static void main(String[] args) {
            launch(args);
      }
}
