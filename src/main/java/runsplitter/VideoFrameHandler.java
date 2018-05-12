package runsplitter;

/**
 * A {@link VideoFrame} handler.
 */
@FunctionalInterface
public interface VideoFrameHandler {

    /**
     * Handles the {@link VideoFrame}.
     *
     * @param frame The {@link VideoFrame}
     */
    void handle(VideoFrame frame);
}
