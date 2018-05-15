package runsplitter.application;

import java.nio.file.Path;

/**
 * Application-related settings that influence how the application behaves.
 */
public class ApplicationSettings {

    private Path videosDirectory;

    private ApplicationSettings(Path videosDirectory) {
        this.videosDirectory = videosDirectory;
    }

    /**
     * Retrieves the default path for videos.
     *
     * @return The path.
     */
    public Path getVideosDirectory() {
        return videosDirectory;
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
     * Creates the default {@link ApplicationSettings}.
     *
     * @return The default {@link ApplicationSettings}.
     */
    public static ApplicationSettings getDefault() {
        return new ApplicationSettings(PersistenceUtil.getUserHome());
    }
}
