import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.security.SecureRandom;

public class AESCipher extends Cipher {

    private static final int IV_SIZE = 12;
    private static final int TAG_LENGTH = 128;
    private static final String ALGORITHM = "AES/GCM/NoPadding";

    public AESCipher(SecretKey key) {
        super(key);
    }

    @Override
    public byte[] encrypt(byte[] data) throws Exception {
        javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance(ALGORITHM);

        byte[] iv = new byte[IV_SIZE];
        new SecureRandom().nextBytes(iv);
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH, iv);

        cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, key, spec);
        byte[] encrypted = cipher.doFinal(data);

        return prependIV(iv, encrypted);
    }

    @Override
    public byte[] decrypt(byte[] data) throws Exception {
        byte[] iv = extractIV(data);
        byte[] encryptedData = extractEncryptedData(data);

        javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance(ALGORITHM);
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH, iv);
        cipher.init(javax.crypto.Cipher.DECRYPT_MODE, key, spec);

        return cipher.doFinal(encryptedData);
    }

    private byte[] prependIV(byte[] iv, byte[] encrypted) {
        byte[] combined = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);
        return combined;
    }

    private byte[] extractIV(byte[] data) {
        byte[] iv = new byte[IV_SIZE];
        System.arraycopy(data, 0, iv, 0, IV_SIZE);
        return iv;
    }

    private byte[] extractEncryptedData(byte[] data) {
        byte[] encrypted = new byte[data.length - IV_SIZE];
        System.arraycopy(data, IV_SIZE, encrypted, 0, encrypted.length);
        return encrypted;
    }
}
