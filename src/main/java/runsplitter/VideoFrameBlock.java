package runsplitter;

import java.util.stream.Stream;

/**
 * A sequence of {@link VideoFrame}s.
 * <p>
 * The motivation for delivering {@link VideoFrame}s in blocks is that many video codecs operate with key frames. In
 * such cases the key frame contains all graphical data for that frame and the subsequent frames only contain the
 * difference from the previous frame.
 */
public interface VideoFrameBlock {

    /**
     * Streams the {@link VideoFrame}s in chronological order.
     * <p>
     * <b><u>Important:</u></b> It is not safe to keep a reference on the {@link VideoFrame}s outside of consuming
     * operations. A sustainable copy of a {@link VideoFrame} can be obtained by calling {@link VideoFrame#copy()}.
     *
     * @return A {@link Stream} of {@link VideoFrame}s.
     */
    Stream<VideoFrame> stream();
}
