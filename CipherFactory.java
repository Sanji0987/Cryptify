import javax.crypto.SecretKey;

public class CipherFactory {

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

}
