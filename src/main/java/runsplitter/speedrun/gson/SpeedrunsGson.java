package runsplitter.speedrun.gson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import runsplitter.speedrun.Speedrun;

/**
 * A container for multiple {@link SpeedrunGson} elements.
 */
class SpeedrunsGson {

    private final Collection<SpeedrunGson> analyses;

    private SpeedrunsGson(Collection<SpeedrunGson> analyses) {
        this.analyses = new ArrayList<>(analyses);
    }

    /**
     * Converts a collection of {@link Speedrun} instances to an {@link SpeedrunsGson}.
     *
     * @param analyses A collection of {@link Speedrun} instances.
     * @return The {@link SpeedrunsGson}.
     */
    static SpeedrunsGson toGson(Collection<Speedrun> analyses) {
        List<SpeedrunGson> converted = analyses.stream()
                .map(SpeedrunGson::toGson)
                .collect(Collectors.toList());
        return new SpeedrunsGson(converted);
    }

    /**
     * Converts this instance to a collection of {@link Speedrun} instances.
     *
     * @return A collection of {@link Speedrun} instances.
     */
    Collection<Speedrun> fromGson() {
        return analyses.stream()
                .map(SpeedrunGson::fromGson)
                .collect(Collectors.toList());
    }
}
