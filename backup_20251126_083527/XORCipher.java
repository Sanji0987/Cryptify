import javax.crypto.SecretKey;

/**
 * XOR cipher implementation using byte-wise XOR operation.
 * 
 * OOP Principles Demonstrated:
 * - Inheritance: Extends abstract Cipher class
 * - Encapsulation: XOR logic hidden in private method
 * - Symmetry: Same operation for encrypt/decrypt
 */
public class XORCipher extends Cipher {

    private static final byte CIPHER_TYPE = 2;

    public XORCipher(SecretKey key) {
        super(key);
    }

    @Override
    public byte[] encrypt(byte[] data) throws Exception {
        return xorBytes(data);
    }

    @Override
    public byte[] decrypt(byte[] data) throws Exception {
        // XOR is symmetric - same operation for both
        return xorBytes(data);
    }

    @Override
    public byte getCipherType() {
        return CIPHER_TYPE;
    }

    @Override
    public String getCipherName() {
        return "XOR Cipher";
    }

    /**
     * Performs XOR operation on data with key bytes.
     * Private helper - encapsulation principle
     */
    private byte[] xorBytes(byte[] data) {
        byte[] keyBytes = key.getEncoded();
        byte[] result = new byte[data.length];

        for (int i = 0; i < data.length; i++) {
            result[i] = (byte) (data[i] ^ keyBytes[i % keyBytes.length]);
        }

        return result;
    }
}
