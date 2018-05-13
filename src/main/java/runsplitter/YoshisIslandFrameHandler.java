package runsplitter;

import java.awt.image.BufferedImage;
import runsplitter.speedrun.Instant;
import runsplitter.speedrun.MutableAnalysis;

/**
 * A {@link VideoFrameHandler} for analyzing Yoshi's Island speed run videos.
 */
public class YoshisIslandFrameHandler implements VideoFrameHandler {

    private static final int BLACK_SCREEN_ARGB = 0xFF000000;
    private static final int SCORE_SCREEN_ARGB = 0xFF1F7C36;
    private static final ScanArea BLACK_SCREEN_SCAN_AREA = createBlackScreenScanArea();
    private static final ScanArea SCORE_SCREEN_SCAN_AREA = createScoreScreenScanArea();
    /**
     * The offset between the start of the run and the first black screen. It takes about 132 frames from the level
     * select start to the black screen. The start of the run is the start of the first black screen minus the time it
     * takes for 132 frames. Note that the start of the run might be negative, if the video starts after the level was
     * selected.
     */
    private static final int RUN_START_OFFSET = (132 * 1000) / 60;

    private final MutableAnalysis analysis;
    private final boolean drawDebug;

    private boolean inBlackScreen;
    private boolean inScoreScreen;
    private long lastBlackScreen = -1L;
    private long runStartTimestamp = -1L;
    private long lastSplit = -1L;
    private int currentLevelNumber = 1;

    /**
     * Creates a new instance.
     *
     * @param analysis  The {@link MutableAnalysis}.
     * @param drawDebug A flag that specifies whether debug artifacts should be drawn.
     */
    public YoshisIslandFrameHandler(MutableAnalysis analysis, boolean drawDebug) {
        this.analysis = analysis;
        this.drawDebug = drawDebug;
    }

    private static ScanArea createBlackScreenScanArea() {
        int scanAreaWidth = 128;
        int scanAreaHeight = 128;
        int left = ((1280 - scanAreaWidth) / 2) + 1;
        int right = left + scanAreaWidth;
        int top = ((720 - scanAreaHeight) / 2) + 1;
        int bottom = top + scanAreaHeight;
        return new ScanArea(left, top, right, bottom);
    }

    private static ScanArea createScoreScreenScanArea() {
        int scanAreaSize = 32;
        int left = 520;
        int right = left + scanAreaSize;
        int top = 200;
        int bottom = top + scanAreaSize;
        return new ScanArea(left, top, right, bottom);
    }

    private static String formatTimestamp(long millis) {
        return new Instant(millis).toString();
    }

    @Override
    public void handle(VideoFrame frame) {
        BufferedImage image = frame.getImage();

        long timeStamp = frame.getTimestampMs();
        if (frameIsBlackScreen(image, drawDebug)) {
            if (runStartTimestamp < 0) {
                runStartTimestamp = timeStamp - RUN_START_OFFSET;
                analysis.setStart(new Instant(runStartTimestamp));
                System.out.printf("%s - Start%n", formatTimestamp(runStartTimestamp));
            }
            if (!inBlackScreen) {
                lastBlackScreen = timeStamp;
                inBlackScreen = true;
            }
            if (inScoreScreen) {
                inScoreScreen = false;
            }
        } else {
            inBlackScreen = false;
        }

        if (!inScoreScreen) {
            if (frameIsEndScreen(image, drawDebug)) {
                long splitTime;
                if (lastSplit < 0) {
                    splitTime = lastBlackScreen - runStartTimestamp;
                } else {
                    splitTime = lastBlackScreen - lastSplit;
                }

                System.out.printf("%s - %s Level %d completed%n", formatTimestamp(lastBlackScreen), formatTimestamp(splitTime), currentLevelNumber);
                analysis.getSpeedrun().addSplit(new Instant(lastBlackScreen - runStartTimestamp));

                lastSplit = lastBlackScreen;
                currentLevelNumber++;
                inScoreScreen = true;
            }
        }
    }

    private static boolean frameIsEndScreen(BufferedImage image, boolean drawArea) {
        boolean result = areaContainsOnly(image, SCORE_SCREEN_SCAN_AREA, SCORE_SCREEN_ARGB);
        if (drawArea) {
            drawScanArea(image, SCORE_SCREEN_SCAN_AREA);
        }
        return result;
    }

    private static boolean frameIsBlackScreen(BufferedImage image, boolean drawArea) {
        boolean result = areaContainsOnly(image, BLACK_SCREEN_SCAN_AREA, BLACK_SCREEN_ARGB);
        if (drawArea) {
            drawScanArea(image, BLACK_SCREEN_SCAN_AREA);
        }
        return result;
    }

    private static boolean areaContainsOnly(BufferedImage image, ScanArea area, int argb) {
        int xEnd = area.getRight() + 1;
        int yEnd = area.getBottom() + 1;
        // Scan the entire square to see if it entirely filled with the expected color
        for (int x = area.getLeft(); x < xEnd; x++) {
            for (int y = area.getTop(); y < yEnd; y++) {
                if (image.getRGB(x, y) != argb) {
                    return false;
                }
            }
        }
        return true;
    }

    private static void drawScanArea(BufferedImage image, ScanArea area) {
        // Horizontal lines
        int xEnd = area.getRight() + 1;
        for (int x = area.getLeft(); x < xEnd; x += 2) {
            invertPixel(image, x, area.getTop());
            invertPixel(image, x, area.getBottom());
        }

        // Vertical lines
        int yEnd = area.getBottom() + 1;
        // NB: +2 and -2 because we already drawn the top and bottom with the previous loop
        for (int y = area.getTop() + 2; y < yEnd - 2; y += 2) {
            invertPixel(image, area.getLeft(), y);
            invertPixel(image, area.getRight(), y);
        }
    }

    private static void invertPixel(BufferedImage image, int x, int y) {
        int argb = image.getRGB(x, y);
        int alpha = 0xFF000000 & argb;
        int rgb = 0xFFFFFF & argb;
        image.setRGB(x, y, alpha | (rgb ^ 0xFFFFFF));
    }

    private static class ScanArea {

        private final int left;
        private final int top;
        private final int right;
        private final int bottom;

        public ScanArea(int left, int top, int right, int bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }

        public int getLeft() {
            return left;
        }

        public int getTop() {
            return top;
        }

        public int getRight() {
            return right;
        }

        public int getBottom() {
            return bottom;
        }
    }
}
