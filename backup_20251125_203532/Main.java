
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
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
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
            // Main horizontal container
            HBox mainContainer = new HBox(15);
            mainContainer.setStyle("-fx-padding: 15; -fx-background-color: #f5f5f5;");

            // ===== LEFT SIDE: File List =====
            VBox leftPanel = new VBox(10);
            leftPanel.setPrefWidth(350);
            leftPanel.setStyle("-fx-background-color: white; -fx-padding: 10; " +
                        "-fx-border-color: #d0d0d0; -fx-border-width: 1;");

            Label fileListLabel = new Label("FILE LIST");
            fileListLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; " +
                        "-fx-text-fill: #666;");

            ListView<String> fileListView = new ListView<>(uiFileList);
            fileListView.setPrefHeight(430);
            fileListView.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; " +
                        "-fx-border-width: 1; -fx-font-size: 11px;");

            Button clearListBtn = new Button("Clear All");
            applyButtonStyle(clearListBtn);
            clearListBtn.setOnAction(new EventHandler<ActionEvent>() {
                  @Override
                  public void handle(ActionEvent event) {
                        uiFileList.clear();
                  }
            });

            Button deleteBtn = new Button("Delete Selected");
            applyButtonStyle(deleteBtn);
            deleteBtn.setOnAction(new EventHandler<ActionEvent>() {
                  @Override
                  public void handle(ActionEvent event) {
                        deleteSelectedFile(fileListView);
                  }
            });

            leftPanel.getChildren().addAll(fileListLabel, fileListView, clearListBtn, deleteBtn);

            // ===== RIGHT SIDE: Drag Zone + Buttons =====
            VBox rightPanel = new VBox(12);
            rightPanel.setPrefWidth(350);

            // Drag zone
            VBox dropZone = new VBox();
            dropZone.setPrefHeight(160);
            dropZone.setAlignment(Pos.CENTER);
            dropZone.setStyle("-fx-background-color: white; -fx-border-color: #999; " +
                        "-fx-border-width: 2; -fx-border-style: dashed; " +
                        "-fx-border-radius: 2;");

            Label dropLabel = new Label("DROP FILES HERE");
            dropLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #999;");

            Label dropHint = new Label(".txt and encrypted files");
            dropHint.setStyle("-fx-font-size: 10px; -fx-text-fill: #aaa;");

            dropZone.getChildren().addAll(dropLabel, dropHint);

            dropZone.setOnDragOver(new EventHandler<DragEvent>() {
                  @Override
                  public void handle(DragEvent event) {
                        if (event.getDragboard().hasFiles()) {
                              event.acceptTransferModes(TransferMode.COPY);
                              dropZone.setStyle("-fx-background-color: #fafafa; -fx-border-color: #666; " +
                                          "-fx-border-width: 2; -fx-border-style: dashed; " +
                                          "-fx-border-radius: 2;");
                        }
                        event.consume();
                  }
            });

            dropZone.setOnDragExited(new EventHandler<DragEvent>() {
                  @Override
                  public void handle(DragEvent event) {
                        dropZone.setStyle("-fx-background-color: white; -fx-border-color: #999; " +
                                    "-fx-border-width: 2; -fx-border-style: dashed; " +
                                    "-fx-border-radius: 2;");
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

            // Buttons container
            VBox buttonsContainer = new VBox(8);
            buttonsContainer.setStyle("-fx-padding: 10 0 0 0;");

            Button fileAdderBtn = new Button("Add File...");
            applyButtonStyle(fileAdderBtn);
            fileAdderBtn.setOnAction(new EventHandler<ActionEvent>() {
                  @Override
                  public void handle(ActionEvent event) {
                        openFileChooser(primaryStage);
                  }
            });

            Button keyAdderBtn = new Button("Set Encryption Key");
            applyButtonStyle(keyAdderBtn);
            keyAdderBtn.setOnAction(new EventHandler<ActionEvent>() {
                  @Override
                  public void handle(ActionEvent event) {
                        openKeyWindow(primaryStage);
                  }
            });

            Button encryptBtn = new Button("Encrypt Selected");
            applyButtonStyle(encryptBtn);
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

            Button decryptBtn = new Button("Decrypt Selected");
            applyButtonStyle(decryptBtn);
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

            buttonsContainer.getChildren().addAll(fileAdderBtn, keyAdderBtn, encryptBtn, decryptBtn);
            rightPanel.getChildren().addAll(dropZone, buttonsContainer);

            // Add both panels to main container
            mainContainer.getChildren().addAll(leftPanel, rightPanel);

            Scene scene = new Scene(mainContainer, 750, 550);
            primaryStage.setTitle("CryptoDrop - File Encryptor");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
      }

      private void applyButtonStyle(Button button) {
            button.setPrefWidth(350);
            button.setPrefHeight(32);
            button.setStyle("-fx-background-color: white; -fx-text-fill: #333; " +
                        "-fx-border-color: #ccc; -fx-border-width: 1; " +
                        "-fx-font-size: 11px; -fx-font-weight: normal; " +
                        "-fx-cursor: hand;");

            button.setOnMouseEntered(e -> {
                  button.setStyle("-fx-background-color: #f8f8f8; -fx-text-fill: #000; " +
                              "-fx-border-color: #999; -fx-border-width: 1; " +
                              "-fx-font-size: 11px; -fx-font-weight: normal; " +
                              "-fx-cursor: hand;");
            });

            button.setOnMouseExited(e -> {
                  button.setStyle("-fx-background-color: white; -fx-text-fill: #333; " +
                              "-fx-border-color: #ccc; -fx-border-width: 1; " +
                              "-fx-font-size: 11px; -fx-font-weight: normal; " +
                              "-fx-cursor: hand;");
            });
      }

      @Override
      public void stop() {
            System.out.println("Application closing...");
      }

      public static void main(String[] args) {
            launch(args);
      }
}
