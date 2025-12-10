import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.crypto.SecretKey;

/**
 * Utility class for file encryption/decryption using Cipher objects.
 * This is the modern replacement for CryptoUtils, using the OOP Cipher
 * hierarchy.
 * 
 * OOP Principles:
 * - Uses Cipher abstraction (polymorphism)
 * - Factory pattern for cipher creation
 * - Separation of concerns (file I/O separate from cipher logic)
 */
public class CryptoHelper {

    /**
     * Encrypts a file using the specified cipher type.
     * 
     * @param file       The file to encrypt
     * @param key        The encryption key
     * @param cipherType The cipher type (1=Caesar, 2=XOR, 3=AES)
     * @throws Exception if encryption fails
     */
    public static void encryptFile(File file, SecretKey key, byte cipherType) throws Exception {
        // Create appropriate cipher using Factory pattern
        Cipher cipher = CipherFactory.createCipherByType(cipherType, key);

        // Read file data
        byte[] fileData = Files.readAllBytes(file.toPath());

        // Encrypt using cipher
        byte[] encrypted = cipher.encrypt(fileData);

        // Create header
        byte[] header = FileHeaderUtil.createHeader(cipherType);

        // Combine header + encrypted data
        byte[] combined = combineArrays(header, encrypted);

        // Write back to file
        Files.write(file.toPath(), combined);
    }

    /**
     * Decrypts a file by auto-detecting the cipher type from the header.
     * 
     * @param file The file to decrypt
     * @param key  The decryption key
     * @throws Exception if decryption fails or file is not encrypted
     */
    public static void decryptFile(File file, SecretKey key) throws Exception {
        // Detect cipher type from file header
        byte cipherType = FileHeaderUtil.readCipherType(file);

        if (cipherType == -1) {
            throw new IOException("File is not encrypted or was not encrypted by this application");
        }

        // Create appropriate cipher using Factory pattern
        Cipher cipher = CipherFactory.createCipherByType(cipherType, key);

        // Read file
        byte[] fileBytes = Files.readAllBytes(file.toPath());

        // Extract encrypted data (skip header)
        byte[] encryptedData = extractData(fileBytes);

        // Decrypt using cipher
        byte[] decrypted = cipher.decrypt(encryptedData);

        // Write decrypted data back to file
        Files.write(file.toPath(), decrypted);
    }

    /**
     * Combines two byte arrays into one.
     * 
     * @param a First array
     * @param b Second array
     * @return Combined array
     */
    private static byte[] combineArrays(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    /**
     * Extracts encrypted data from file bytes (skips header).
     * 
     * @param fileBytes Complete file bytes including header
     * @return Encrypted data without header
     */
    private static byte[] extractData(byte[] fileBytes) {
        int headerSize = FileHeaderUtil.getHeaderSize();
        byte[] data = new byte[fileBytes.length - headerSize];
        System.arraycopy(fileBytes, headerSize, data, 0, data.length);
        return data;
    }
}
