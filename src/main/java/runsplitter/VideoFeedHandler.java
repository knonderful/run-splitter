package runsplitter;

/**
 * A {@link VideoFeed} handler.
 */
public interface VideoFeedHandler {

    /**
     * Handles a {@link VideoFeed}.
     *
     * @param feed The {@link VideoFeed}
     * @return {@code true} if the {@link VideoFeed} should be further processed, otherwise {@code false}.
     */
    boolean handle(VideoFeed feed);
}
