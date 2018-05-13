package runsplitter.speedrun.gson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import runsplitter.speedrun.Analysis;

/**
 * A container for multiple {@link AnalysisGson} elements.
 */
class AnalysesGson {

    private final Collection<AnalysisGson> analyses;

    private AnalysesGson(Collection<AnalysisGson> analyses) {
        this.analyses = new ArrayList<>(analyses);
    }

    /**
     * Converts a collection of {@link Analysis} instances to an {@link AnalysesGson}.
     *
     * @param analyses A collection of {@link Analysis} instances.
     * @return The {@link AnalysesGson}.
     */
    static AnalysesGson toGson(Collection<Analysis> analyses) {
        List<AnalysisGson> converted = analyses.stream()
                .map(AnalysisGson::toGson)
                .collect(Collectors.toList());
        return new AnalysesGson(converted);
    }

    /**
     * Converts this instance to a collection of {@link Analysis} instances.
     *
     * @return A collection of {@link Analysis} instances.
     */
    Collection<Analysis> fromGson() {
        return analyses.stream()
                .map(AnalysisGson::fromGson)
                .collect(Collectors.toList());
    }
}
