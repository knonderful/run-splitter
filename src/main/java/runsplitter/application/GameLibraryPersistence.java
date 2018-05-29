package runsplitter.application;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.function.Supplier;
import runsplitter.VideoAnalyzer;
import runsplitter.application.json.GameLibraryJson;

/**
 * Utility class for saving and loading the {@link GameLibrary}.
 */
public class GameLibraryPersistence {

    private final GameLibraryJson json;
    
    public GameLibraryPersistence(Supplier<Collection<VideoAnalyzer>> videoAnalyzersSupplier) {
        this.json = new GameLibraryJson(videoAnalyzersSupplier);
    }

    /**
     * Loads the {@link GameLibrary}.
     *
     * @return The {@link GameLibrary}.
     * @throws IOException If the {@link GameLibrary} could not be loaded.
     */
    public GameLibrary load() throws IOException {
        File libraryFile = getLibraryFile();
        if (!libraryFile.exists()) {
            return new GameLibrary();
        }
        try (Reader reader = new FileReader(libraryFile)) {
            return json.fromJson(reader);
        }
    }

    /**
     * Saves the {@link GameLibrary}.
     *
     * @param library The {@link GameLibrary}.
     * @throws IOException If the {@link GameLibrary} could not be saved.
     */
    public void save(GameLibrary library) throws IOException {
        try (Writer writer = new FileWriter(getLibraryFile())) {
            json.toJson(library, writer);
        }
    }

    private static File getLibraryFile() throws IOException {
        return PersistenceUtil.getStoreDirectory().resolve("library.json").toFile();
    }
}
