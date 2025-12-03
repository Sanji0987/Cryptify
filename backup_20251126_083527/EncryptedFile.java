import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Represents an encrypted file with encryption state awareness.
 * 
 * OOP Principles Demonstrated:
 * - Encapsulation: File knows its own encryption state
 * - Responsibility: File handles its own encryption/decryption
 * - Information Hiding: Internal state not exposed
 * 
 * Benefits:
 * - Cleaner API: file.encrypt(cipher) vs passing file everywhere
 * - State Management: File knows if it's encrypted
 * - Self-contained: All file operations in one place
 */
public class EncryptedFile {

    private File file;
    private boolean isEncrypted;
    private byte cipherType;

    /**
     * Creates an EncryptedFile and detects its encryption status.
     * 
     * @param file The file to wrap
     * @throws IOException if file cannot be read
     */
    public EncryptedFile(File file) throws IOException {
        this.file = file;
        detectEncryptionStatus();
    }

    /**
     * Encrypts this file using the given cipher.
     * 
     * @param cipher The cipher to use for encryption
     * @throws Exception if encryption fails
     */
    public void encrypt(Cipher cipher) throws Exception {
        if (isEncrypted) {
            throw new IllegalStateException("File is already encrypted");
        }

        // Read file content
        byte[] data = Files.readAllBytes(file.toPath());

        // Encrypt
        byte[] encrypted = cipher.encrypt(data);

        // Create header
        byte[] header = FileHeaderUtil.createHeader(cipher.getCipherType());

        // Combine header + encrypted data
        byte[] combined = new byte[header.length + encrypted.length];
        System.arraycopy(header, 0, combined, 0, header.length);
        System.arraycopy(encrypted, 0, combined, header.length, encrypted.length);

        // Write back to file
        Files.write(file.toPath(), combined);

        // Update state
        isEncrypted = true;
        cipherType = cipher.getCipherType();
    }

    /**
     * Decrypts this file using the given cipher.
     * 
     * @param cipher The cipher to use for decryption
     * @throws Exception if decryption fails
     */
    public void decrypt(Cipher cipher) throws Exception {
        if (!isEncrypted) {
            throw new IllegalStateException("File is not encrypted");
        }

        // Read file
        byte[] fileBytes = Files.readAllBytes(file.toPath());

        // Skip header
        byte[] encryptedData = new byte[fileBytes.length - FileHeaderUtil.getHeaderSize()];
        System.arraycopy(fileBytes, FileHeaderUtil.getHeaderSize(), encryptedData, 0, encryptedData.length);

        // Decrypt
        byte[] decrypted = cipher.decrypt(encryptedData);

        // Write decrypted data
        Files.write(file.toPath(), decrypted);

        // Update state
        isEncrypted = false;
        cipherType = -1;
    }

    /**
     * Gets the underlying File object.
     * 
     * @return The file
     */
    public File getFile() {
        return file;
    }

    /**
     * Checks if this file is encrypted.
     * 
     * @return true if encrypted
     */
    public boolean isEncrypted() {
        return isEncrypted;
    }

    /**
     * Gets the cipher type used (if encrypted).
     * 
     * @return The cipher type, or -1 if not encrypted
     */
    public byte getCipherType() {
        return cipherType;
    }

    /**
     * Gets the name of the cipher used (if encrypted).
     * 
     * @return The cipher name, or "Not Encrypted"
     */
    public String getCipherName() {
        if (!isEncrypted) {
            return "Not Encrypted";
        }

        try {
            // Use factory to get cipher name
            Cipher cipher = CipherFactory.createCipherByType(cipherType, null);
            return cipher.getCipherName();
        } catch (Exception e) {
            return "Unknown";
        }
    }

    /**
     * Detects if the file is encrypted by reading its header.
     * Private method - encapsulation
     */
    private void detectEncryptionStatus() throws IOException {
        cipherType = FileHeaderUtil.readCipherType(file);
        isEncrypted = (cipherType != -1);
    }
}
