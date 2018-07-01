package runsplitter;

import io.humble.video.awt.ImageFrame;

/**
 * A {@link VideoFrameHandler} that displays the current frame in a window.
 * <p>
 * Note that this class is mainly intended to be used as a means to display the content that is being analyzed to the
 * user; it is not a proper video stream player.
 * <p>
 * <b>NOTE:</b> In order to cleanly dispose of this instance, {@link #dispose()} should be called.
 */
public class WindowFrameHandler implements VideoFrameHandler {

    private final ImageFrame window;
    private final boolean realtime;
    private long playStartTime = 0;

    /**
     * Creates a new instance.
     *
     * @param realtime A flag that specifies whether the video should be played in real-time ({@code true}) or as fast
     *                 as possible ({@code false}). Note that playing in real-time blocks the thread, thus slowing down
     *                 frame processing for other {@link VideoFrameHandler}s in the chain.
     */
    public WindowFrameHandler(boolean realtime) {
        this.realtime = realtime;
        this.window = ImageFrame.make();
        if (this.window == null) {
            throw new RuntimeException("Attempting this demo on a headless machine, and that will not work. Sad day for you.");
        }
    }

    @Override
    public void handle(VideoFrame frame) {
        if (realtime) {
            if (playStartTime == 0) {
                playStartTime = System.currentTimeMillis();
            } else {
                // Wait with updating the frame until the time has come...
                long time = System.currentTimeMillis();
                long targetTime = playStartTime + frame.getTimestampMs();
                while (time < targetTime) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ex) {
                        return;
                    }
                    time = System.currentTimeMillis();
                }
            }
        }

        window.setImage(frame.toImage());
    }

    /**
     * Disposes of the underlying infrastructure.
     */
    public void dispose() {
        window.dispose();
    }
}
