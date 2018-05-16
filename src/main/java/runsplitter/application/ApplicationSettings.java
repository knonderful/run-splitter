package runsplitter.application;

import java.nio.file.Path;

/**
 * Application-related settings that influence how the application behaves.
 */
public class ApplicationSettings {

    private Path videosDirectory;
    private GuiTheme theme;

    private ApplicationSettings() {
    }

    /**
     * Retrieves the default path for videos.
     *
     * @return The path.
     */
    public Path getVideosDirectory() {
        return get(videosDirectory, PersistenceUtil.getUserHome());
    }

    /**
     * Sets the default path for videos.
     *
     * @param videosDirectory The path.
     */
    public void setVideosDirectory(Path videosDirectory) {
        this.videosDirectory = videosDirectory;
    }

    /**
     * Retrieves the {@link GuiTheme}.
     *
     * @return The {@link GuiTheme}.
     */
    public GuiTheme getTheme() {
        return get(theme, GuiTheme.DEFAULT);
    }

    /**
     * Sets the {@link GuiTheme}.
     *
     * @param theme The {@link GuiTheme}.
     */
    public void setTheme(GuiTheme theme) {
        this.theme = theme;
    }

    /**
     * Creates the default {@link ApplicationSettings}.
     *
     * @return The default {@link ApplicationSettings}.
     */
    public static ApplicationSettings getDefault() {
        return new ApplicationSettings();
    }

    private static <T> T get(T value, T defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        return value;
    }
}
