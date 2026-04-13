import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;

public class FileHeaderUtil {

    private static final byte[] MAGIC_BYTES = "ENCR".getBytes(StandardCharsets.UTF_8);
    private static final int HEADER_SIZE = 8;

    public static byte[] createHeader(byte cipherType) {
        byte[] header = new byte[HEADER_SIZE];
        System.arraycopy(MAGIC_BYTES, 0, header, 0, MAGIC_BYTES.length);
        header[4] = cipherType;
        return header;
    }

    public static byte readCipherType(File file) throws IOException {
        byte[] fileBytes = Files.readAllBytes(file.toPath());

        if (fileBytes.length < HEADER_SIZE) {
            return -1;
        }

        for (int i = 0; i < MAGIC_BYTES.length; i++) {
            if (fileBytes[i] != MAGIC_BYTES[i]) {
                return -1;
            }
        }

        return fileBytes[4];
    }

    public static boolean isEncryptedFile(File file) {
        try {
            return readCipherType(file) != -1;
        } catch (IOException e) {
            return false;
        }
    }

    public static int getHeaderSize() {
        return HEADER_SIZE;
    }
}
