package runsplitter.yi;

import java.awt.image.BufferedImage;
import java.util.function.Predicate;
import runsplitter.VideoFrame;
import runsplitter.speedrun.Instant;

/**
 * A {@link Predicate} that determines whether a {@link VideoFrame} is entirely white.
 */
class WhiteScreenPredicate implements Predicate<VideoFrame> {

    private static final int RGB_WHITE = 0xFFFFFFFF;
    private static final int HORIZONTAL_MARGIN = 5;
    private static final int VERTICAL_MARGIN = 20;
    private final int left;
    private final int top;
    private final int right;
    private final int bottom;

    /**
     * Creates a new instance.
     *
     * @param displayRectangle The display {@link Rectangle}.
     */
    public WhiteScreenPredicate(Rectangle displayRectangle) {
        this.left = displayRectangle.getLeft() + HORIZONTAL_MARGIN;
        this.top = displayRectangle.getTop() + VERTICAL_MARGIN;
        this.right = displayRectangle.getRight() - HORIZONTAL_MARGIN;
        this.bottom = displayRectangle.getBottom() - VERTICAL_MARGIN;
    }

    @Override
    public boolean test(VideoFrame frame) {
        BufferedImage image = frame.toImage();
        for (int y = top; y < bottom; y++) {
            for (int x = left; x < right; x++) {
                if (image.getRGB(x, y) != RGB_WHITE) {
                    return false;
                }
            }
        }
        System.out.println("Found white screen at " + new Instant(frame.getTimestampMs()).toTimestamp());
        return true;
    }
}
