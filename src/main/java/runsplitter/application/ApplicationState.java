package runsplitter.application;

/**
 *
 */
public class ApplicationState {

    private final ApplicationSettings settings;
    private final GameLibrary library;

    public ApplicationState(ApplicationSettings settings, GameLibrary library) {
        this.settings = settings;
        this.library = library;
    }

    public GameLibrary getLibrary() {
        return library;
    }

    public ApplicationSettings getSettings() {
        return settings;
    }
}
