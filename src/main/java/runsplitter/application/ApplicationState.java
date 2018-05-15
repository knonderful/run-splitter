package runsplitter.application;

/**
 *
 */
public class ApplicationState {

    private final ApplicationSettings settings;

    public ApplicationState(ApplicationSettings settings) {
        this.settings = settings;
    }

    public ApplicationSettings getSettings() {
        return settings;
    }
}
