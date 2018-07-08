package runsplitter.yi;

import java.util.function.Predicate;
import runsplitter.VideoFrame;
import runsplitter.speedrun.Instant;

/**
 * A {@link Predicate} that determines whether a {@link VideoFrame} is entirely white.
 */
class BlackScreenPredicate implements Predicate<VideoFrame> {

    private static final int EXPECTED_COLOR = 0xFF000000;
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
    public BlackScreenPredicate(Rectangle displayRectangle) {
        // The scan area in the original frame format
        Rectangle originalScanArea = new Rectangle(
                Common.ORIGINAL_DISPLAY_AREA.getLeft() + HORIZONTAL_MARGIN,
                Common.ORIGINAL_DISPLAY_AREA.getTop() + VERTICAL_MARGIN,
                Common.ORIGINAL_DISPLAY_AREA.getRight() - HORIZONTAL_MARGIN,
                Common.ORIGINAL_DISPLAY_AREA.getBottom() - VERTICAL_MARGIN);
        // The translated scan area for the current display format
        Rectangle scanArea = Geometry.projectRectangle(Common.ORIGINAL_DISPLAY_AREA, originalScanArea, displayRectangle);
        // The area scanner
        this.scanner = new AbsoluteAreaScanner(scanArea, EXPECTED_COLOR);
    }

    @Override
    public boolean test(VideoFrame frame) {
        if (!scanner.matches(frame)) {
            return false;
        }
        System.out.println("Found black screen at " + new Instant(frame.getTimestampMs()).toTimestamp());
        return true;
    }
}
