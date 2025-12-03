import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Custom dialog helper that mimics GTK/Zenity style dialogs.
 * Clean, simple dialogs without JavaFX Alert overhead.
 */
public class DialogHelper {

    /**
     * Shows an error dialog with the specified title and message.
     * Mimics zenity --error appearance.
     */
    public static void showError(String title, String message) {
        Platform.runLater(() -> {
            showCustomDialog(title, message, DialogType.ERROR);
        });
    }

    /**
     * Shows a success/information dialog with the specified title and message.
     * Mimics zenity --info appearance.
     */
    public static void showSuccess(String title, String message) {
        Platform.runLater(() -> {
            showCustomDialog(title, message, DialogType.INFO);
        });
    }

    /**
     * Shows an information dialog with the specified title and message.
     * Mimics zenity --info appearance.
     */
    public static void showInfo(String title, String message) {
        Platform.runLater(() -> {
            showCustomDialog(title, message, DialogType.INFO);
        });
    }

    /**
     * Shows a confirmation dialog with Continue and Cancel buttons.
     * Returns true if user clicks Continue, false if Cancel.
     * 
     * @param title   Dialog title
     * @param message Dialog message
     * @return true if Continue clicked, false if Cancel clicked
     */
    public static boolean showConfirm(String title, String message) {
        final boolean[] result = { false };

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(title);
        dialog.setResizable(false);

        VBox container = new VBox(15);
        container.setPadding(new Insets(20));
        container.setAlignment(Pos.CENTER_LEFT);
        container.setStyle(Styles.DIALOG_CONTAINER);

        // Content with warning icon
        HBox contentBox = new HBox(15);
        contentBox.setAlignment(Pos.CENTER_LEFT);

        Label iconLabel = new Label("⚠");
        iconLabel.setStyle(Styles.DIALOG_ICON_BASE + "-fx-text-fill: #ff9800;");

        Text messageText = new Text(message);
        messageText.setStyle(Styles.DIALOG_MESSAGE);
        messageText.setWrappingWidth(350);

        contentBox.getChildren().addAll(iconLabel, messageText);

        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setPrefWidth(80);
        cancelBtn.setStyle(Styles.DIALOG_BUTTON_NORMAL);
        cancelBtn.setOnMouseEntered(e -> cancelBtn.setStyle(Styles.DIALOG_BUTTON_HOVER));
        cancelBtn.setOnMouseExited(e -> cancelBtn.setStyle(Styles.DIALOG_BUTTON_NORMAL));
        cancelBtn.setOnAction(e -> {
            result[0] = false;
            dialog.close();
        });

        Button continueBtn = new Button("Continue");
        continueBtn.setPrefWidth(80);
        continueBtn.setStyle(Styles.DIALOG_BUTTON_NORMAL);
        continueBtn.setOnMouseEntered(e -> continueBtn.setStyle(Styles.DIALOG_BUTTON_HOVER));
        continueBtn.setOnMouseExited(e -> continueBtn.setStyle(Styles.DIALOG_BUTTON_NORMAL));
        continueBtn.setOnAction(e -> {
            result[0] = true;
            dialog.close();
        });

        buttonBox.getChildren().addAll(cancelBtn, continueBtn);

        container.getChildren().addAll(contentBox, buttonBox);

        Scene scene = new Scene(container, 450, 200); // Increased height to 200
        dialog.setScene(scene);
        dialog.showAndWait();

        return result[0];
    }

    /**
     * Internal method to create and show a custom dialog.
     * Designed to look like simple GTK/zenity dialogs.
     */
    private static void showCustomDialog(String title, String message, DialogType type) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(title);
        dialog.setResizable(false);

        // Main container
        VBox container = new VBox(15);
        container.setPadding(new Insets(20));
        container.setAlignment(Pos.CENTER_LEFT);
        container.setStyle(Styles.DIALOG_CONTAINER);

        // Content area with icon and message
        HBox contentBox = new HBox(15);
        contentBox.setAlignment(Pos.CENTER_LEFT);

        // Icon label (simple text-based icon)
        Label iconLabel = new Label();
        if (type == DialogType.ERROR) {
            iconLabel.setText("✖");
            iconLabel.setStyle(Styles.DIALOG_ICON_ERROR);
        } else {
            iconLabel.setText("ℹ");
            iconLabel.setStyle(Styles.DIALOG_ICON_INFO);
        }

        // Message text
        Text messageText = new Text(message);
        messageText.setStyle(Styles.DIALOG_MESSAGE);
        messageText.setWrappingWidth(350);

        contentBox.getChildren().addAll(iconLabel, messageText);

        // OK button
        Button okButton = new Button("OK");
        okButton.setPrefWidth(80);
        okButton.setStyle(Styles.DIALOG_BUTTON_NORMAL);
        // Button hover effect
        okButton.setOnMouseEntered(e -> {
            okButton.setStyle(Styles.DIALOG_BUTTON_HOVER);
        });

        okButton.setOnMouseExited(e -> {
            okButton.setStyle(Styles.DIALOG_BUTTON_NORMAL);
        });

        okButton.setOnAction(e -> dialog.close());

        // Button container (right-aligned like GTK dialogs)
        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getChildren().add(okButton);

        // Add all to container
        container.getChildren().addAll(contentBox, buttonBox);

        Scene scene = new Scene(container, 450, 150);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    /**
     * Dialog type enumeration
     */
    private enum DialogType {
        ERROR,
        INFO
    }
}
