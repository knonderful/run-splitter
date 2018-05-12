package runsplitter;

import java.util.Arrays;
import java.util.List;

/**
 * A {@link VideoFrameHandler} that combines several other {@link VideoFrameHandler}s into a single processing chain.
 */
public class VideoFrameHandlerChain implements VideoFrameHandler {

    private final List<VideoFrameHandler> handlers;

    /**
     * Creates a new instance.
     *
     * @param frameHandlers The chain of {@link VideoFrameHandler}s.
     */
    public VideoFrameHandlerChain(VideoFrameHandler... frameHandlers) {
        this.handlers = Arrays.asList(frameHandlers);
    }

    @Override
    public void handle(VideoFrame frame) {
        handlers.forEach(handler -> handler.handle(frame));
    }
}
