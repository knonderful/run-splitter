package runsplitter.application;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class for persistence-related functionality.
 */
public class PersistenceUtil {

    private PersistenceUtil() {
    }

    /**
     * Retrieves the user's home directory.
     *
     * @return The directory.
     */
    public static Path getUserHome() {
        return Paths.get(System.getProperty("user.home"));
    }

    /**
     * Retrieves the root directory for application persistence data (such as settings and run information).
     * <p>
     * The provided directory will be created if necessary.
     *
     * @return The path to the directory.
     * @throws IOException If the directory could not be created.
     */
    public static Path getStoreDirectory() throws IOException {
        Path storeDir = PersistenceUtil.getUserHome().resolve(".runsplitter");
        return Files.createDirectories(storeDir);
    }
}
