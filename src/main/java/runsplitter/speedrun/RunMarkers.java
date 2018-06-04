package runsplitter.speedrun;

import java.util.List;

/**
 * A speed run.
 */
public interface RunMarkers {

    /**
     * Retrieves all split times.
     *
     * @return A list of split times.
     */
    List<Instant> getSplits();

    /**
     * Retrieves the final time.
     * <p>
     * This is equivalent to last element from {@link #getSplits()}.
     *
     * @return The final time.
     */
    Instant getFinalSplit();
}
