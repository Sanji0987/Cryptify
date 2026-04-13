import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.crypto.SecretKey;

public class CryptoHelper {

    public static void encryptFile(File file, SecretKey key, byte cipherType) throws Exception {
        Cipher cipher = CipherFactory.createCipherByType(cipherType, key);
        byte[] fileData = Files.readAllBytes(file.toPath());
        byte[] encrypted = cipher.encrypt(fileData);
        byte[] header = FileHeaderUtil.createHeader(cipherType);
        byte[] combined = combineArrays(header, encrypted);
        Files.write(file.toPath(), combined);
    }

    public static void decryptFile(File file, SecretKey key) throws Exception {
        byte cipherType = FileHeaderUtil.readCipherType(file);

        if (cipherType == -1) {
            throw new IOException("File is not encrypted or was not encrypted by this application");
        }

        Cipher cipher = CipherFactory.createCipherByType(cipherType, key);
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        byte[] encryptedData = extractData(fileBytes);
        byte[] decrypted = cipher.decrypt(encryptedData);
        Files.write(file.toPath(), decrypted);
    }

    private static byte[] combineArrays(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    private static byte[] extractData(byte[] fileBytes) {
        int headerSize = FileHeaderUtil.getHeaderSize();
        byte[] data = new byte[fileBytes.length - headerSize];
        System.arraycopy(fileBytes, headerSize, data, 0, data.length);
        return data;
    }
}
