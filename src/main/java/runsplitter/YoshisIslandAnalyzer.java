package runsplitter;

import runsplitter.speedrun.MutableAnalysis;

/**
 * The {@link VideoAnalyzer} for Yoshi's Island on the Super NT.
 */
public class YoshisIslandAnalyzer implements VideoAnalyzer {

    @Override
    public String getIdentifier() {
        return "supernt-yoshis-island";
    }

    @Override
    public String getName() {
        return "Yoshi's Island (Super NT)";
    }

    @Override
    public VideoFrameHandler createFrameHandler(MutableAnalysis analysis) {
        return new YoshisIslandFrameHandler(analysis, false);
    }
}
