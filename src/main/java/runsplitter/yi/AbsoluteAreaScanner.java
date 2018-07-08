package runsplitter.yi;

import java.awt.image.BufferedImage;
import runsplitter.VideoFrame;

/**
 * An {@link AreaScanner} that only allows for an exact match of the expected color for each pixel in the scan area.
 */
public class AbsoluteAreaScanner implements AreaScanner {

    private final int expectedRgb;
    private final int left;
    private final int top;
    private final int right;
    private final int bottom;

    /**
     * Creates a new instance.
     *
     * @param rectangle             The scan area.
     * @param expectedRgb           The expected color.
     */
    public AbsoluteAreaScanner(Rectangle rectangle, int expectedRgb) {
        this.left = rectangle.getLeft();
        this.top = rectangle.getTop();
        this.right = rectangle.getRight();
        this.bottom = rectangle.getBottom();
        this.expectedRgb = expectedRgb;
    }

    @Override
    public boolean matches(VideoFrame frame) {
        BufferedImage image = frame.toImage();
        for (int y = top; y <= bottom; y++) {
            for (int x = left; x <= right; x++) {
                if (image.getRGB(x, y) != expectedRgb) {
                    return false;
                }
            }
        }
        return true;
    }
}
