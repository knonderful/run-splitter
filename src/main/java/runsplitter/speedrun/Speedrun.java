package runsplitter.speedrun;

/**
 * Analysis of a speed run medium (e.g. a video file).
 */
public interface Speedrun {

    /**
     * Retrieves the name of the source that is used for the analysis.
     *
     * @return
     */
    String getSourceName();

    /**
     * Retrieves the start time of the run in the source material.
     *
     * @return
     */
    Instant getStart();

    /**
     * Retrieves the {@link RunMarkers}.
     *
     * @return The {@link RunMarkers}.
     */
    RunMarkers getMarkers();
}
