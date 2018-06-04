package runsplitter;

import runsplitter.speedrun.MutableSpeedrun;

/**
 * The {@link VideoAnalyzer} for Yoshi's Island on the Super NT.
 */
public class YoshisIslandAnalyzer implements VideoAnalyzer {

    private static final String IDENTIFIER = "supernt-yoshis-island";
    private static final String NAME = "Yoshi's Island (Super NT)";

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public VideoFrameHandler createFrameHandler(MutableSpeedrun run) {
        return new YoshisIslandFrameHandler(run, false);
    }

    @Override
    public String toString() {
        return IDENTIFIER;
    }
}
