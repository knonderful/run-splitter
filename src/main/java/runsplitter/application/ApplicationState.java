package runsplitter.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import runsplitter.VideoAnalyzer;

/**
 *
 */
public class ApplicationState {

    private final ApplicationSettings settings;
    private final GameLibrary library;
    private final List<VideoAnalyzer> analyzers;

    public ApplicationState(ApplicationSettings settings, GameLibrary library, List<VideoAnalyzer> analyzers) {
        this.settings = settings;
        this.library = library;
        this.analyzers = new ArrayList<>(analyzers);
    }

    public GameLibrary getLibrary() {
        return library;
    }

    public ApplicationSettings getSettings() {
        return settings;
    }

    public List<VideoAnalyzer> getAnalyzers() {
        return Collections.unmodifiableList(analyzers);
    }
}
