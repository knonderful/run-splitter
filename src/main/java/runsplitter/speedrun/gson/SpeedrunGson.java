package runsplitter.speedrun.gson;

import runsplitter.speedrun.Instant;
import runsplitter.speedrun.MutableSpeedrun;
import runsplitter.speedrun.Speedrun;

/**
 * A GSON wrapper for {@link Speedrun}.
 */
class SpeedrunGson {

    private final String sourceName;
    private final Instant start;
    private final RunMarkersGson markers;

    private SpeedrunGson(String sourceName, RunMarkersGson markers, Instant start) {
        this.sourceName = sourceName;
        this.markers = markers;
        this.start = start;
    }

    /**
     * Converts a {@link Speedrun} to an {@link SpeedrunGson}.
     *
     * @param analysis The {@link Speedrun}.
     * @return The {@link SpeedrunGson}.
     */
    static SpeedrunGson toGson(Speedrun analysis) {
        return new SpeedrunGson(analysis.getSourceName(), RunMarkersGson.toGson(analysis.getMarkers()), analysis.getStart());
    }

    /**
     * Converts this instance to a {@link MutableSpeedrun}.
     *
     * @return The {@link MutableSpeedrun}.
     */
    MutableSpeedrun fromGson() {
        return new MutableSpeedrun(sourceName, start, markers.fromGson());
    }
}
