import javafx.stage.Stage;
import javax.crypto.SecretKey;

/**
 * Interface for encryption key management.
 * 
 * Design Pattern: Interface Segregation Principle
 * OOP Benefits:
 * - Abstraction: Separates interface from implementation
 * - Security: Defines only necessary public methods
 * - Flexibility: Can swap key storage mechanisms
 */
public interface IKeyManager {

    /**
     * Gets the current encryption key.
     * 
     * @return The secret key, or null if not set
     */
    SecretKey getKey();

    /**
     * Checks if an encryption key has been set.
     * 
     * @return true if key exists
     */
    boolean hasKey();

    /**
     * Shows the key entry window to the user.
     * 
     * @param owner The parent window
     */
    void showKeyWindow(Stage owner);

    /**
     * Derives a secret key from a password string.
     * 
     * @param password The password to derive from
     * @return The derived SecretKey
     */
    SecretKey deriveKey(String password);
}
