import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

public class CryptoUtils {

    // File header constants
    private static final byte[] MAGIC_BYTES = "ENCR".getBytes(StandardCharsets.UTF_8);
    private static final byte CIPHER_CAESAR = (byte) 1;
    private static final byte CIPHER_XOR = (byte) 2;
    private static final byte CIPHER_AES = (byte) 3;
    private static final int HEADER_SIZE = 8;

    // ==================== AUTO-DETECTION ====================

    public static int detectCipherType(File file) throws IOException {
        byte[] fileBytes = Files.readAllBytes(file.toPath());

        if (fileBytes.length < HEADER_SIZE) {
            return -1; // Not encrypted by our app
        }

        // Check magic bytes
        for (int i = 0; i < MAGIC_BYTES.length; i++) {
            if (fileBytes[i] != MAGIC_BYTES[i]) {
                return -1; // Not encrypted by our app
            }
        }

        // Return cipher type
        return fileBytes[4];
    }

    public static void decryptAuto(File file, SecretKey key, ObservableList<String> fileList) {
        try {
            int cipherType = detectCipherType(file);

            if (cipherType == -1) {
                showError("Decryption Error", "File is not encrypted or was not encrypted by this application.");
                return;
            }

            switch (cipherType) {
                case CIPHER_CAESAR:
                    decryptCaesar(file, key, fileList);
                    break;
                case CIPHER_XOR:
                    decryptXOR(file, key, fileList);
                    break;
                case CIPHER_AES:
                    decryptAES(file, key, fileList);
                    break;
                default:
                    showError("Decryption Error", "Unknown cipher type: " + cipherType);
            }

        } catch (Exception e) {
            showError("Auto-Decrypt Error", "Failed to decrypt file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==================== CAESAR CIPHER ====================

    public static void encryptCaesar(File file, SecretKey key, ObservableList<String> fileList) {
        try {
            // Derive shift value from key bytes
            int shift = deriveShiftFromKey(key);

            // Read file content as text
            String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);

            // Encrypt using Caesar cipher
            String encrypted = applyCaesarShift(content, shift);
            byte[] encryptedBytes = encrypted.getBytes(StandardCharsets.UTF_8);

            // Create header
            byte[] header = createHeader(CIPHER_CAESAR);

            // Combine header + encrypted data
            byte[] combined = new byte[header.length + encryptedBytes.length];
            System.arraycopy(header, 0, combined, 0, header.length);
            System.arraycopy(encryptedBytes, 0, combined, header.length, encryptedBytes.length);

            // Overwrite original file
            Files.write(file.toPath(), combined);

            showSuccess("Caesar Encryption", "File encrypted successfully:\n" + file.getName());

        } catch (Exception e) {
            showError("Caesar Encryption Error", "Failed to encrypt file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void decryptCaesar(File file, SecretKey key, ObservableList<String> fileList) {
        try {
            // Derive same shift value
            int shift = deriveShiftFromKey(key);

            // Read file and skip header
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            byte[] encryptedData = new byte[fileBytes.length - HEADER_SIZE];
            System.arraycopy(fileBytes, HEADER_SIZE, encryptedData, 0, encryptedData.length);

            // Decrypt (reverse shift)
            String content = new String(encryptedData, StandardCharsets.UTF_8);
            String decrypted = applyCaesarShift(content, -shift);

            // Overwrite original file with decrypted content
            Files.write(file.toPath(), decrypted.getBytes(StandardCharsets.UTF_8));

            showSuccess("Caesar Decryption", "File decrypted successfully:\n" + file.getName());

        } catch (Exception e) {
            showError("Caesar Decryption Error", "Failed to decrypt file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String applyCaesarShift(String text, int shift) {
        StringBuilder result = new StringBuilder();
        shift = ((shift % 26) + 26) % 26; // Normalize shift to 0-25

        for (char c : text.toCharArray()) {
            if (Character.isUpperCase(c)) {
                result.append((char) ((c - 'A' + shift) % 26 + 'A'));
            } else if (Character.isLowerCase(c)) {
                result.append((char) ((c - 'a' + shift) % 26 + 'a'));
            } else {
                result.append(c); // Keep non-alphabetic characters unchanged
            }
        }
        return result.toString();
    }

    private static int deriveShiftFromKey(SecretKey key) {
        byte[] keyBytes = key.getEncoded();
        int sum = 0;
        for (int i = 0; i < Math.min(4, keyBytes.length); i++) {
            sum += keyBytes[i] & 0xFF; // Convert to unsigned
        }
        return (sum % 25) + 1; // Shift between 1-25
    }

    // ==================== XOR CIPHER ====================

    public static void encryptXOR(File file, SecretKey key, ObservableList<String> fileList) {
        try {
            byte[] keyBytes = key.getEncoded();
            byte[] fileBytes = Files.readAllBytes(file.toPath());

            // XOR each byte with key
            byte[] encrypted = xorBytes(fileBytes, keyBytes);

            // Create header
            byte[] header = createHeader(CIPHER_XOR);

            // Combine header + encrypted data
            byte[] combined = new byte[header.length + encrypted.length];
            System.arraycopy(header, 0, combined, 0, header.length);
            System.arraycopy(encrypted, 0, combined, header.length, encrypted.length);

            // Overwrite original file
            Files.write(file.toPath(), combined);

            showSuccess("XOR Encryption", "File encrypted successfully:\n" + file.getName());

        } catch (Exception e) {
            showError("XOR Encryption Error", "Failed to encrypt file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void decryptXOR(File file, SecretKey key, ObservableList<String> fileList) {
        try {
            byte[] keyBytes = key.getEncoded();

            // Read file and skip header
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            byte[] encryptedData = new byte[fileBytes.length - HEADER_SIZE];
            System.arraycopy(fileBytes, HEADER_SIZE, encryptedData, 0, encryptedData.length);

            // XOR is symmetric - same operation for decrypt
            byte[] decrypted = xorBytes(encryptedData, keyBytes);

            // Overwrite original file with decrypted content
            Files.write(file.toPath(), decrypted);

            showSuccess("XOR Decryption", "File decrypted successfully:\n" + file.getName());

        } catch (Exception e) {
            showError("XOR Decryption Error", "Failed to decrypt file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static byte[] xorBytes(byte[] data, byte[] key) {
        byte[] result = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = (byte) (data[i] ^ key[i % key.length]);
        }
        return result;
    }

    // ==================== AES-GCM ENCRYPTION ====================

    public static void encryptAES(File file, SecretKey key, ObservableList<String> fileList) {
        try {
            // Initialize cipher
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            // Generate random 12-byte IV
            byte[] iv = new byte[12];
            new SecureRandom().nextBytes(iv);
            GCMParameterSpec spec = new GCMParameterSpec(128, iv);

            cipher.init(Cipher.ENCRYPT_MODE, key, spec);

            // Read and encrypt file
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            byte[] encrypted = cipher.doFinal(fileBytes);

            // Create header
            byte[] header = createHeader(CIPHER_AES);

            // Combine: header + IV + encrypted data
            byte[] combined = new byte[header.length + iv.length + encrypted.length];
            System.arraycopy(header, 0, combined, 0, header.length);
            System.arraycopy(iv, 0, combined, header.length, iv.length);
            System.arraycopy(encrypted, 0, combined, header.length + iv.length, encrypted.length);

            // Overwrite original file
            Files.write(file.toPath(), combined);

            showSuccess("AES Encryption", "File encrypted successfully:\n" + file.getName());

        } catch (Exception e) {
            showError("AES Encryption Error", "Failed to encrypt file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void decryptAES(File file, SecretKey key, ObservableList<String> fileList) {
        try {
            // Read file
            byte[] fileBytes = Files.readAllBytes(file.toPath());

            if (fileBytes.length < HEADER_SIZE + 12) {
                throw new IOException("File too short to contain IV");
            }

            // Skip header, extract IV (next 12 bytes)
            byte[] iv = new byte[12];
            System.arraycopy(fileBytes, HEADER_SIZE, iv, 0, 12);

            // Extract encrypted data
            int encryptedStart = HEADER_SIZE + 12;
            byte[] encryptedData = new byte[fileBytes.length - encryptedStart];
            System.arraycopy(fileBytes, encryptedStart, encryptedData, 0, encryptedData.length);

            // Initialize cipher for decryption
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec spec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, spec);

            // Decrypt
            byte[] decrypted = cipher.doFinal(encryptedData);

            // Overwrite original file with decrypted content
            Files.write(file.toPath(), decrypted);

            showSuccess("AES Decryption", "File decrypted successfully:\n" + file.getName());

        } catch (Exception e) {
            showError("AES Decryption Error", "Failed to decrypt file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==================== HELPER METHODS ====================

    private static byte[] createHeader(byte cipherType) {
        byte[] header = new byte[HEADER_SIZE];
        System.arraycopy(MAGIC_BYTES, 0, header, 0, MAGIC_BYTES.length);
        header[4] = cipherType;
        // Bytes 5-7 are reserved (already 0)
        return header;
    }

    private static void showError(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private static void showSuccess(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}
