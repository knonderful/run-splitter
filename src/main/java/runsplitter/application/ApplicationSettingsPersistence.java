package runsplitter.application;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import runsplitter.application.json.ApplicationSettingsJson;

/**
 * Utility class for saving and loading the {@link ApplicationSettings}.
 */
public class ApplicationSettingsPersistence {

    private ApplicationSettingsPersistence() {
    }

    /**
     * Loads the {@link ApplicationSettings}.
     *
     * @return The {@link ApplicationSettings}.
     * @throws IOException If the {@link ApplicationSettings} could not be loaded.
     */
    public static ApplicationSettings load() throws IOException {
        File settingsFile = getSettingsFile();
        if (!settingsFile.exists()) {
            return ApplicationSettings.getDefault();
        }
        try (Reader reader = new FileReader(settingsFile)) {
            return ApplicationSettingsJson.fromJson(reader);
        }
    }

    /**
     * Saves the {@link ApplicationSettings}.
     *
     * @param settings The {@link ApplicationSettings}.
     * @throws IOException If the {@link ApplicationSettings} could not be saved.
     */
    public static void save(ApplicationSettings settings) throws IOException {
        try (Writer writer = new FileWriter(getSettingsFile())) {
            ApplicationSettingsJson.toJson(settings, writer);
        }
    }

    private static File getSettingsFile() throws IOException {
        return PersistenceUtil.getStoreDirectory().resolve("app-settings.json").toFile();
    }
}
