package runsplitter;

import java.util.stream.Stream;

/**
 * A feed that provides access to the currently available {@link VideoFrameBlock}s that can be used for analysis.
 */
public interface VideoFeed {

    /**
     * Streams the {@link VideoFrameBlock}s in reverse chronological order.
     * <p>
     * Note that depending on the implementation the number of preceeding {@link VideoFrameBlock}s that can be retrieved
     * may be limited.
     *
     * @return A {@link Stream} of {@link VideoFrameBlock}s.
     */
    Stream<? extends VideoFrameBlock> stream();
}
