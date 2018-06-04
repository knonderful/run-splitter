package runsplitter.speedrun.gson;

import java.util.ArrayList;
import java.util.List;
import runsplitter.speedrun.Instant;
import runsplitter.speedrun.MutableMarkers;
import runsplitter.speedrun.RunMarkers;

/**
 * A GSON wrapper for {@link RunMarkers}.
 */
class RunMarkersGson {

    private final List<Instant> splits;

    private RunMarkersGson(List<Instant> splits) {
        this.splits = new ArrayList<>(splits);
    }

    /**
     * Converts a {@link RunMarkers} to an {@link RunMarkersGson}.
     *
     * @param analysis The {@link RunMarkers}.
     * @return The {@link RunMarkersGson}.
     */
    static RunMarkersGson toGson(RunMarkers speedrun) {
        return new RunMarkersGson(speedrun.getSplits());
    }

    /**
     * Converts this instance to a {@link MutableMarkers}.
     *
     * @return The {@link MutableMarkers}.
     */
    MutableMarkers fromGson() {
        MutableMarkers run = new MutableMarkers();
        splits.forEach(run::addSplit);
        return run;
    }
}
