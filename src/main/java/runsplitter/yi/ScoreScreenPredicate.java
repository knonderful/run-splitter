package runsplitter.yi;

import java.util.function.Predicate;
import runsplitter.VideoFrame;

/**
 * A {@link Predicate} that determines whether a {@link VideoFrame} represents a world map.
 */
public class ScoreScreenPredicate implements Predicate<VideoFrame> {

    /**
     * The expected color for each pixel in the scan areas.
     */
    private static final int EXPECTED_COLOR = 0xFF1F7C36;
    /**
     * The average number of deviation points per pixel for a scan area.
     */
    private static final int AVG_DEVIATION_POINTS_PER_PIXEL = 10;
    /**
     * The maximum value that an RGB color component is allowed to deviate from the expected value.
     */
    private static final int MAX_COLOR_COMPONENT_DEVIATION = 10;

    private final AreaScanner areaScanner1;
    private final AreaScanner areaScanner2;

    /**
     * Creates a new instance.
     *
     * @param displayRectangle The display {@link Rectangle}.
     */
    public ScoreScreenPredicate(Rectangle displayRectangle) {
        /*
         * This implementation tries to determine whether a frame is a world map frame by scanning several areas of the
         * screen for the green chalk board color.
         */
        // The scan areas in the original video frame format
        Rectangle originalArea1 = new Rectangle(270, 120, 300, 150);
        Rectangle originalArea2 = new Rectangle(270, 500, 350, 550);

        // Project the original scan areas onto the current display rectangle
        Rectangle area1 = Geometry.projectRectangle(Common.ORIGINAL_DISPLAY_AREA, originalArea1, displayRectangle);
        Rectangle area2 = Geometry.projectRectangle(Common.ORIGINAL_DISPLAY_AREA, originalArea2, displayRectangle);

        // Build the area scanners
        this.areaScanner1 = new LenientAreaScanner(
                area1,
                EXPECTED_COLOR,
                MAX_COLOR_COMPONENT_DEVIATION,
                AVG_DEVIATION_POINTS_PER_PIXEL * area1.getSurfaceArea());
        this.areaScanner2 = new LenientAreaScanner(
                area2,
                EXPECTED_COLOR,
                MAX_COLOR_COMPONENT_DEVIATION,
                AVG_DEVIATION_POINTS_PER_PIXEL * area2.getSurfaceArea());
    }

    @Override
    public boolean test(VideoFrame frame) {
        return areaScanner1.matches(frame) && areaScanner2.matches(frame);
    }
}
