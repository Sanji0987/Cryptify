
// --- CryptoDrop Main Application File (Full Refresh) ---
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {

      private ObservableList<String> uiFileList = FXCollections.observableArrayList();
      private FileManager fileManager;
      private KeyManager keyManager;

      @Override
      public void start(Stage primaryStage) {
            // Initialize managers
            fileManager = new FileManager(uiFileList);
            keyManager = new KeyManager();

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
                        fileManager.clearAll();
                  }
            });

            Button deleteBtn = new Button("Delete Selected");
            applyButtonStyle(deleteBtn);
            deleteBtn.setOnAction(new EventHandler<ActionEvent>() {
                  @Override
                  public void handle(ActionEvent event) {
                        fileManager.deleteSelected(fileListView);
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
                        fileManager.openFileChooser(primaryStage);
                  }
            });

            Button keyAdderBtn = new Button("Set Encryption Key");
            applyButtonStyle(keyAdderBtn);
            keyAdderBtn.setOnAction(new EventHandler<ActionEvent>() {
                  @Override
                  public void handle(ActionEvent event) {
                        keyManager.showKeyWindow(primaryStage);
                  }
            });

            Button encryptBtn = new Button("Encrypt Selected");
            applyButtonStyle(encryptBtn);
            encryptBtn.setOnAction(new EventHandler<ActionEvent>() {
                  @Override
                  public void handle(ActionEvent event) {
                        String selectedFile = fileListView.getSelectionModel().getSelectedItem();
                        if (selectedFile == null) {
                              DialogHelper.showError("No File Selected",
                                          "Please select a file from the list to encrypt.");
                              return;
                        }
                        if (!keyManager.hasKey()) {
                              DialogHelper.showError("No Key Set", "Please set an encryption key first!");
                              return;
                        }
                        CipherSelector.show(primaryStage, new File(selectedFile), keyManager.getSecretKey(), uiFileList,
                                    true);
                  }
            });

            Button decryptBtn = new Button("Decrypt Selected");
            applyButtonStyle(decryptBtn);
            decryptBtn.setOnAction(new EventHandler<ActionEvent>() {
                  @Override
                  public void handle(ActionEvent event) {
                        String selectedFile = fileListView.getSelectionModel().getSelectedItem();
                        if (selectedFile == null) {
                              DialogHelper.showError("No File Selected",
                                          "Please select a file from the list to decrypt.");
                              return;
                        }
                        if (!keyManager.hasKey()) {
                              DialogHelper.showError("No Key Set", "Please set a decryption key first!");
                              return;
                        }
                        CryptoUtils.decryptAuto(new File(selectedFile), keyManager.getSecretKey(), uiFileList);
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
