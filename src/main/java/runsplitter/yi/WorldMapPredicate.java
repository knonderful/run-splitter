package runsplitter.yi;

import java.util.function.Predicate;
import runsplitter.VideoFrame;

/**
 * A {@link Predicate} that determines whether a {@link VideoFrame} represents a world map.
 */
public class WorldMapPredicate implements Predicate<VideoFrame> {

    /**
     * The expected color for each pixel in the scan areas.
     */
    private static final int EXPECTED_COLOR = 0xFF101010;
    /**
     * The average number of deviation points per pixel for a scan area.
     */
    private static final int AVG_DEVIATION_POINTS_PER_PIXEL = 10;
    /**
     * The maximum value that an RGB color component is allowed to deviate from the expected value.
     */
    private static final int MAX_COLOR_COMPONENT_DEVIATION = 10;

    private final AreaScanner leftAreaScanner;
    private final AreaScanner rightAreaScanner;

    /**
     * Creates a new instance.
     *
     * @param displayRectangle The display {@link Rectangle}.
     */
    public WorldMapPredicate(Rectangle displayRectangle) {
        /*
         * This implementation tries to determine whether a frame is a world map frame by scanning two rectangular areas
         * at the side of the screen between the edge of the screen and the rolled up (paper) map. These areas contain a
         * dark grey. Due to the fact that these areas are adjacent to some other colors (e.g. the paper of the map),
         * encoding artifacts can cause deviations from the expected color. This is attempted to compensate for by using
         * the LenientAreaScanner. More scan areas can be added in later implementations to correct for false positives
         * and negatives (for instance the black area at the top and the white area around the score-flip and controls
         * screen buttons).
         */
        // The scan areas in the original video frame format
        Rectangle originalLeftArea = new Rectangle(164, 330, 180, 550);
        Rectangle originalRightArea = new Rectangle(1106, 330, 1122, 550);

        // Project the original scan areas onto the current display rectangle
        Rectangle leftArea = Geometry.projectRectangle(Common.ORIGINAL_DISPLAY_AREA, originalLeftArea, displayRectangle);
        Rectangle rightArea = Geometry.projectRectangle(Common.ORIGINAL_DISPLAY_AREA, originalRightArea, displayRectangle);

        // Build the area scanners
        this.leftAreaScanner = new LenientAreaScanner(
                leftArea,
                EXPECTED_COLOR,
                MAX_COLOR_COMPONENT_DEVIATION,
                AVG_DEVIATION_POINTS_PER_PIXEL * leftArea.getSurfaceArea());
        this.rightAreaScanner = new LenientAreaScanner(
                rightArea,
                EXPECTED_COLOR,
                MAX_COLOR_COMPONENT_DEVIATION,
                AVG_DEVIATION_POINTS_PER_PIXEL * rightArea.getSurfaceArea());
    }

    @Override
    public boolean test(VideoFrame frame) {
        return leftAreaScanner.matches(frame) && rightAreaScanner.matches(frame);
    }
}
