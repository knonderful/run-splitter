package runsplitter.speedrun.gson;

import java.util.ArrayList;
import java.util.List;
import runsplitter.speedrun.Instant;
import runsplitter.speedrun.MutableSpeedrun;
import runsplitter.speedrun.Speedrun;

/**
 * A GSON wrapper for {@link Speedrun}.
 */
class SpeedrunGson {

    private final List<Instant> splits;

    private SpeedrunGson(List<Instant> splits) {
        this.splits = new ArrayList<>(splits);
    }

    /**
     * Converts a {@link Speedrun} to an {@link SpeedrunGson}.
     *
     * @param analysis The {@link Speedrun}.
     * @return The {@link SpeedrunGson}.
     */
    static SpeedrunGson toGson(Speedrun speedrun) {
        return new SpeedrunGson(speedrun.getSplits());
    }

    /**
     * Converts this instance to a {@link MutableSpeedrun}.
     *
     * @return The {@link MutableSpeedrun}.
     */
    MutableSpeedrun fromGson() {
        MutableSpeedrun run = new MutableSpeedrun();
        splits.forEach(run::addSplit);
        return run;
    }
}
