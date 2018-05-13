package runsplitter.speedrun.gson;

import runsplitter.speedrun.Analysis;
import runsplitter.speedrun.Instant;
import runsplitter.speedrun.MutableAnalysis;

/**
 * A GSON wrapper for {@link Analysis}.
 */
class AnalysisGson {

    private final String sourceName;
    private final Instant start;
    private final SpeedrunGson speedrun;

    private AnalysisGson(String sourceName, SpeedrunGson speedrun, Instant start) {
        this.sourceName = sourceName;
        this.speedrun = speedrun;
        this.start = start;
    }

    /**
     * Converts a {@link Analysis} to an {@link AnalysisGson}.
     *
     * @param analysis The {@link Analysis}.
     * @return The {@link AnalysisGson}.
     */
    static AnalysisGson toGson(Analysis analysis) {
        return new AnalysisGson(analysis.getSourceName(), SpeedrunGson.toGson(analysis.getSpeedrun()), analysis.getStart());
    }

    /**
     * Converts this instance to a {@link MutableAnalysis}.
     *
     * @return The {@link MutableAnalysis}.
     */
    MutableAnalysis fromGson() {
        return new MutableAnalysis(sourceName, start, speedrun.fromGson());
    }
}
