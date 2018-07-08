package runsplitter.yi;

import java.util.function.Predicate;
import runsplitter.VideoFrame;
import runsplitter.speedrun.Instant;

/**
 * A {@link Predicate} that determines whether a {@link VideoFrame} is entirely white.
 */
class WhiteScreenPredicate implements Predicate<VideoFrame> {

    private static final int EXPECTED_COLOR = 0xFFFFFFFF;
    /**
     * The average number of deviation points per pixel for a scan area.
     */
    private static final int AVG_DEVIATION_POINTS_PER_PIXEL = 10;
    /**
     * The maximum value that an RGB color component is allowed to deviate from the expected value.
     */
    private static final int MAX_COLOR_COMPONENT_DEVIATION = 10;
    /**
     * The horizontal margin from the edge of the original display area in pixels.
     */
    private static final int HORIZONTAL_MARGIN = 5;
    /**
     * The vertical margin from the edge of the original display area in pixels.
     */
    private static final int VERTICAL_MARGIN = 20;

    private final AreaScanner scanner;

    /**
     * Creates a new instance.
     *
     * @param displayRectangle The display {@link Rectangle}.
     */
    public WhiteScreenPredicate(Rectangle displayRectangle) {
        // The scan area in the original frame format
        Rectangle originalScanArea = new Rectangle(
                Common.ORIGINAL_DISPLAY_AREA.getLeft() + HORIZONTAL_MARGIN,
                Common.ORIGINAL_DISPLAY_AREA.getTop() + VERTICAL_MARGIN,
                Common.ORIGINAL_DISPLAY_AREA.getRight() - HORIZONTAL_MARGIN,
                Common.ORIGINAL_DISPLAY_AREA.getBottom() - VERTICAL_MARGIN);
        // The translated scan area for the current display format
        Rectangle scanArea = Geometry.projectRectangle(Common.ORIGINAL_DISPLAY_AREA, originalScanArea, displayRectangle);
        // The area scanner
        this.scanner = new LenientAreaScanner(scanArea, EXPECTED_COLOR, MAX_COLOR_COMPONENT_DEVIATION, AVG_DEVIATION_POINTS_PER_PIXEL * scanArea.getSurfaceArea());
    }

    @Override
    public boolean test(VideoFrame frame) {
        if (!scanner.matches(frame)) {
            return false;
        }
        System.out.println("Found white screen at " + new Instant(frame.getTimestampMs()).toTimestamp());
        return true;
    }
}
