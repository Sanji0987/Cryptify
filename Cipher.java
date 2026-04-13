import javax.crypto.SecretKey;

public abstract class Cipher {

    protected final SecretKey key;

    public Cipher(SecretKey key) {
        this.key = key;
    }

    public abstract byte[] encrypt(byte[] data) throws Exception;

    public abstract byte[] decrypt(byte[] data) throws Exception;
}
