import javax.crypto.SecretKey;
import java.io.File;
import java.io.IOException;

/**
 * Factory class for creating Cipher instances.
 * 
 * Design Pattern: Factory Pattern
 * OOP Principles:
 * - Encapsulation: Hides cipher creation complexity
 * - Polymorphism: Returns abstract Cipher type
 * - Single Responsibility: Only creates cipher objects
 * 
 * Benefits:
 * - Centralized object creation
 * - Easy to add new cipher types
 * - Client code doesn't need to know concrete classes
 */
public class CipherFactory {

    /**
     * Creates a cipher instance based on type identifier.
     * 
     * @param type The cipher type (1=Caesar, 2=XOR, 3=AES)
     * @param key  The encryption key
     * @return A Cipher instance
     * @throws IllegalArgumentException if type is unknown
     */
    public static Cipher createCipherByType(byte type, SecretKey key) {
        switch (type) {
            case 1:
                return new CaesarCipher(key);
            case 2:
                return new XORCipher(key);
            case 3:
                return new AESCipher(key);
            default:
                throw new IllegalArgumentException("Unknown cipher type: " + type);
        }
    }

    /**
     * Detects the cipher type from a file and creates the appropriate cipher.
     * This is the power of the factory pattern - automatic detection!
     * 
     * @param file The encrypted file
     * @param key  The decryption key
     * @return The appropriate Cipher instance for this file
     * @throws IOException              if file cannot be read
     * @throws IllegalArgumentException if file is not encrypted
     */
    public static Cipher detectAndCreate(File file, SecretKey key) throws IOException {
        byte cipherType = FileHeaderUtil.readCipherType(file);

        if (cipherType == -1) {
            throw new IllegalArgumentException("File is not encrypted or invalid format");
        }

        return createCipherByType(cipherType, key);
    }

    /**
     * Gets all available cipher types.
     * Useful for UI cipher selection.
     * 
     * @param key The key to use for cipher creation
     * @return Array of all available ciphers
     */
    public static Cipher[] getAllCiphers(SecretKey key) {
        return new Cipher[] {
                new CaesarCipher(key),
                new XORCipher(key),
                new AESCipher(key)
        };
    }
}
