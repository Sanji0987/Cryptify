import java.io.File;
import javax.crypto.SecretKey;
import javafx.collections.ObservableList;
import javafx.application.Platform;

public class CryptoUtils {

    private static void processFile(File inputFile, ObservableList<String> fileList, boolean isEncrypting) {
        if (isEncrypting) {
            if (inputFile.getName().endsWith(".enc")) {
                System.out.println("Skipping: File is already encrypted.");
                return;
            }
            System.out.println("CryptoUtils: Renaming for Encryption " + inputFile.getAbsolutePath());
        } else {
            if (inputFile.getName().startsWith("decrypted_")) {
                System.out.println("Skipping: File is already decrypted.");
                return;
            }
            System.out.println("CryptoUtils: Renaming for Decryption " + inputFile.getAbsolutePath());
        }

        File outFile;
        if (isEncrypting) {
            String fileName = inputFile.getName();
            if (fileName.startsWith("decrypted_")) {
                fileName = fileName.substring(10);
            }
            outFile = new File(inputFile.getParent(), fileName + ".enc");
        } else {
            String originalName = inputFile.getName();
            String newName = originalName;
            if (newName.endsWith(".enc")) {
                newName = newName.substring(0, newName.length() - 4);
            }
            newName = "decrypted_" + newName;
            outFile = new File(inputFile.getParent(), newName);
        }

        if (inputFile.renameTo(outFile)) {
            System.out.println("Success: Renamed to " + outFile.getName());
            Platform.runLater(() -> {
                fileList.remove(inputFile.getAbsolutePath());
                fileList.add(outFile.getAbsolutePath());
            });
        } else {
            System.out.println("Error: Could not rename file.");
        }
    }

    public static void restoreOriginalNames(ObservableList<String> fileList) {
        for (String filePath : fileList) {
            File file = new File(filePath);
            if (file.getName().startsWith("decrypted_")) {
                String newName = file.getName().substring(10); // Remove "decrypted_"
                File outFile = new File(file.getParent(), newName);
                if (file.renameTo(outFile)) {
                    System.out.println("Restored: " + outFile.getName());
                }
            }
        }
    }

    public static void encrypt(File inputFile, SecretKey key, ObservableList<String> fileList) {
        processFile(inputFile, fileList, true);
    }

    public static void decrypt(File inputFile, SecretKey key, ObservableList<String> fileList) {
        processFile(inputFile, fileList, false);
    }

}
