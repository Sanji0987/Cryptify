import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * Caesar cipher implementation using character shifting.
 * 
 * OOP Principles Demonstrated:
 * - Inheritance: Extends abstract Cipher class
 * - Encapsulation: Shift value derived from key internally
 * - Polymorphism: Can be used wherever Cipher is expected
 */
public class CaesarCipher extends Cipher {

    private static final byte CIPHER_TYPE = 1;
    private int shift;

    public CaesarCipher(SecretKey key) {
        super(key);
        this.shift = deriveShiftFromKey();
    }

    @Override
    public byte[] encrypt(byte[] data) throws Exception {
        String text = new String(data, StandardCharsets.UTF_8);
        String encrypted = applyCaesarShift(text, shift);
        return encrypted.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public byte[] decrypt(byte[] data) throws Exception {
        String text = new String(data, StandardCharsets.UTF_8);
        String decrypted = applyCaesarShift(text, -shift);
        return decrypted.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public byte getCipherType() {
        return CIPHER_TYPE;
    }

    @Override
    public String getCipherName() {
        return "Caesar Cipher";
    }

    /**
     * Applies Caesar shift to the text.
     * Private helper method - encapsulation
     */
    private String applyCaesarShift(String text, int shiftAmount) {
        StringBuilder result = new StringBuilder();
        int normalizedShift = ((shiftAmount % 26) + 26) % 26;

        for (char c : text.toCharArray()) {
            if (Character.isUpperCase(c)) {
                result.append((char) ((c - 'A' + normalizedShift) % 26 + 'A'));
            } else if (Character.isLowerCase(c)) {
                result.append((char) ((c - 'a' + normalizedShift) % 26 + 'a'));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * Derives shift value from encryption key.
     * Encapsulated - users don't need to know how shift is calculated
     */
    private int deriveShiftFromKey() {
        byte[] keyBytes = key.getEncoded();
        int sum = 0;
        for (int i = 0; i < Math.min(4, keyBytes.length); i++) {
            sum += keyBytes[i] & 0xFF;
        }
        return (sum % 25) + 1;
    }
}
