package runsplitter;

import runsplitter.speedrun.MutableAnalysis;

/**
 * A video analyzer can be used to analyze a speed run video for split detection.
 */
public interface VideoAnalyzer {

    /**
     * Retrieves the (unique) identifier.
     *
     * @return The identifier.
     */
    String getIdentifier();

    /**
     * Retrieves the (human-readable) name.
     *
     * @return The name.
     */
    String getName();

    /**
     * Creates the {@link VideoFrameHandler} that will handle input frames.
     *
     * @param analysis The {@link MutableAnalysis} to which the run should be applied.
     * @return The {@link VideoFrameHandler}.
     */
    VideoFrameHandler createFrameHandler(MutableAnalysis analysis);
}
