import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;

/**
 * Utility class for file header operations.
 * Handles reading and writing encryption metadata in file headers.
 * 
 * Design Pattern: Utility/Helper class (static methods)
 * OOP Principle: Single Responsibility - Only handles file headers
 */
public class FileHeaderUtil {

    private static final byte[] MAGIC_BYTES = "ENCR".getBytes(StandardCharsets.UTF_8);
    private static final int HEADER_SIZE = 8;

    /**
     * Creates a file header with the given cipher type.
     * 
     * @param cipherType The cipher type identifier
     * @return 8-byte header array
     */
    public static byte[] createHeader(byte cipherType) {
        byte[] header = new byte[HEADER_SIZE];
        System.arraycopy(MAGIC_BYTES, 0, header, 0, MAGIC_BYTES.length);
        header[4] = cipherType;
        // Bytes 5-7 are reserved (already 0)
        return header;
    }

    /**
     * Reads and validates the cipher type from a file.
     * 
     * @param file The file to read
     * @return The cipher type, or -1 if not a valid encrypted file
     * @throws IOException if file cannot be read
     */
    public static byte readCipherType(File file) throws IOException {
        byte[] fileBytes = Files.readAllBytes(file.toPath());

        if (fileBytes.length < HEADER_SIZE) {
            return -1;
        }

        // Verify magic bytes
        for (int i = 0; i < MAGIC_BYTES.length; i++) {
            if (fileBytes[i] != MAGIC_BYTES[i]) {
                return -1;
            }
        }

        return fileBytes[4];
    }

    /**
     * Checks if a file has a valid encryption header.
     * 
     * @param file The file to check
     * @return true if file has valid header
     */
    public static boolean isEncryptedFile(File file) {
        try {
            return readCipherType(file) != -1;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Gets the header size in bytes.
     * 
     * @return Header size
     */
    public static int getHeaderSize() {
        return HEADER_SIZE;
    }
}
