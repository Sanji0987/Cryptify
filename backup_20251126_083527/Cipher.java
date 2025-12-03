import javax.crypto.SecretKey;

/**
 * Abstract base class for all cipher implementations.
 * Provides common interface and template for encryption/decryption operations.
 * 
 * Design Pattern: Template Method Pattern
 * OOP Principle: Inheritance, Polymorphism, Encapsulation
 */
public abstract class Cipher {

    protected SecretKey key;

    /**
     * Constructor requiring a secret key for encryption/decryption.
     * 
     * @param key The encryption key
     */
    public Cipher(SecretKey key) {
        this.key = key;
    }

    /**
     * Encrypts the given data.
     * 
     * @param data The plaintext data to encrypt
     * @return The encrypted data
     * @throws Exception if encryption fails
     */
    public abstract byte[] encrypt(byte[] data) throws Exception;

    /**
     * Decrypts the given data.
     * 
     * @param data The encrypted data to decrypt
     * @return The decrypted plaintext data
     * @throws Exception if decryption fails
     */
    public abstract byte[] decrypt(byte[] data) throws Exception;

    /**
     * Gets the cipher type identifier (used in file headers).
     * 
     * @return Byte identifier for this cipher type
     */
    public abstract byte getCipherType();

    /**
     * Gets the human-readable name of this cipher.
     * 
     * @return The cipher name
     */
    public abstract String getCipherName();

    /**
     * Gets the secret key being used by this cipher.
     * 
     * @return The secret key
     */
    public SecretKey getKey() {
        return key;
    }
}
