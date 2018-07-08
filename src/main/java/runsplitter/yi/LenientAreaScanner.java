package runsplitter.yi;

import java.awt.image.BufferedImage;
import runsplitter.VideoFrame;

/**
 * An {@link AreaScanner} that allows for deviation off an expected color.
 * <p>
 * This implementation allocates a "budget" of deviation for the entire area for each call to
 * {@link #matches(runsplitter.VideoFrame)}. If the sum of individual color component deviation exceeds the budget, the
 * {@link VideoFrame} does not match. Additionally, a maximum deviation per color component guarantees that no single
 * pixel deviates too much from the expected color.
 */
public class LenientAreaScanner implements AreaScanner {

    private final int left;
    private final int top;
    private final int right;
    private final int bottom;
    private final int maxComponentDeviation;
    private final int maxAreaDeviation;
    private final int expectedRed;
    private final int expectedGreen;
    private final int expectedBlue;

    /**
     * Creates a new instance.
     *
     * @param rectangle             The scan area.
     * @param expectedRgb           The expected color.
     * @param maxComponentDeviation The maximum deviation that is allowed for a single color component.
     * @param maxAreaDeviation      The maximum deviation (sum of color component deviations) that is allowed for the
     *                              entire scan area.
     */
    public LenientAreaScanner(Rectangle rectangle, int expectedRgb, int maxComponentDeviation, int maxAreaDeviation) {
        this.left = rectangle.getLeft();
        this.top = rectangle.getTop();
        this.right = rectangle.getRight();
        this.bottom = rectangle.getBottom();
        this.maxComponentDeviation = maxComponentDeviation;
        this.maxAreaDeviation = maxAreaDeviation;
        this.expectedRed = (expectedRgb >> 16) & 0xFF;
        this.expectedGreen = (expectedRgb >> 8) & 0xFF;
        this.expectedBlue = expectedRgb & 0xFF;
    }

    @Override
    public boolean matches(VideoFrame frame) {
        int remainingDeviationBudget = maxAreaDeviation;
        BufferedImage image = frame.toImage();
        for (int y = top; y <= bottom; y++) {
            for (int x = left; x <= right; x++) {
                int rgb = image.getRGB(x, y);
                // Blue
                int blueDiff = calculateComponentDiff(rgb, expectedBlue);
                if (blueDiff > maxComponentDeviation) {
                    return false;
                }
                // Green
                int greenDiff = calculateComponentDiff(rgb >> 8, expectedGreen);
                if (greenDiff > maxComponentDeviation) {
                    return false;
                }
                // Red
                int redDiff = calculateComponentDiff(rgb >> 16, expectedRed);
                if (redDiff > maxComponentDeviation) {
                    return false;
                }

                remainingDeviationBudget -= blueDiff;
                remainingDeviationBudget -= redDiff;
                remainingDeviationBudget -= redDiff;
                if (remainingDeviationBudget <= 0) {
                    return false;
                }
            }
        }
        System.out.printf("Matched area with %d/%d (%d%%) if the deviation budget left.%n", remainingDeviationBudget, maxAreaDeviation, remainingDeviationBudget * 100 / maxAreaDeviation);
        return true;
    }

    private static int calculateComponentDiff(int component, int expected) {
        return Math.abs((component & 0xFF) - expected);
    }
}
