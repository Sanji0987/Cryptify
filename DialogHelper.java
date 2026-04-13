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

public class DialogHelper {

    public static void showError(String title, String message) {
        Platform.runLater(() -> {
            showCustomDialog(title, message, DialogType.ERROR);
        });
    }

    public static void showSuccess(String title, String message) {
        Platform.runLater(() -> {
            showCustomDialog(title, message, DialogType.INFO);
        });
    }

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

        HBox contentBox = new HBox(15);
        contentBox.setAlignment(Pos.CENTER_LEFT);

        Label iconLabel = new Label("⚠");
        iconLabel.setStyle(Styles.DIALOG_ICON_BASE + "-fx-text-fill: #ff9800;");

        Text messageText = new Text(message);
        messageText.setStyle(Styles.DIALOG_MESSAGE);
        messageText.setWrappingWidth(350);

        contentBox.getChildren().addAll(iconLabel, messageText);

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

        Scene scene = new Scene(container, 450, 200);
        dialog.setScene(scene);
        dialog.showAndWait();

        return result[0];
    }

    private static void showCustomDialog(String title, String message, DialogType type) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(title);
        dialog.setResizable(false);

        VBox container = new VBox(15);
        container.setPadding(new Insets(20));
        container.setAlignment(Pos.CENTER_LEFT);
        container.setStyle(Styles.DIALOG_CONTAINER);

        HBox contentBox = new HBox(15);
        contentBox.setAlignment(Pos.CENTER_LEFT);

        Label iconLabel = new Label();
        if (type == DialogType.ERROR) {
            iconLabel.setText("✖");
            iconLabel.setStyle(Styles.DIALOG_ICON_ERROR);
        } else {
            iconLabel.setText("ℹ");
            iconLabel.setStyle(Styles.DIALOG_ICON_INFO);
        }

        Text messageText = new Text(message);
        messageText.setStyle(Styles.DIALOG_MESSAGE);
        messageText.setWrappingWidth(350);

        contentBox.getChildren().addAll(iconLabel, messageText);

        Button okButton = new Button("OK");
        okButton.setPrefWidth(80);
        okButton.setStyle(Styles.DIALOG_BUTTON_NORMAL);
        okButton.setOnMouseEntered(e -> {
            okButton.setStyle(Styles.DIALOG_BUTTON_HOVER);
        });

        okButton.setOnMouseExited(e -> {
            okButton.setStyle(Styles.DIALOG_BUTTON_NORMAL);
        });

        okButton.setOnAction(e -> dialog.close());

        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getChildren().add(okButton);

        container.getChildren().addAll(contentBox, buttonBox);

        Scene scene = new Scene(container, 450, 150);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private enum DialogType {
        ERROR,
        INFO
    }
}
