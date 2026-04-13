import javax.crypto.SecretKey;

public class XORCipher extends Cipher {

    public XORCipher(SecretKey key) {
        super(key);
    }

    @Override
    public byte[] encrypt(byte[] data) throws Exception {
        return xorBytes(data);
    }

    @Override
    public byte[] decrypt(byte[] data) throws Exception {
        return xorBytes(data);
    }

    private byte[] xorBytes(byte[] data) {
        byte[] keyBytes = key.getEncoded();
        byte[] result = new byte[data.length];

        for (int i = 0; i < data.length; i++) {
            result[i] = (byte) (data[i] ^ keyBytes[i % keyBytes.length]);
        }

        return result;
    }
}
