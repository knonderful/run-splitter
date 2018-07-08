package runsplitter.yi;

import runsplitter.VideoFrame;

/**
 * A scanner for an area inside a {@link VideoFrame}.
 * <p>
 * Implementing classes will determine whether a {@link VideoFrame} matches certain criteria, set out by that
 * {@link AreaScanner}. This can be used to detect an expected pattern in a video stream.
 */
public interface AreaScanner {

    /**
     * Determines whether the provided {@link VideoFrame} matches the criteria of the {@link AreaScanner}.
     *
     * @param frame The {@link VideoFrame}.
     * @return {@code true} if the {@link VideoFrame} matches the criteria, otherwise {@code false}.
     */
    boolean matches(VideoFrame frame);
}
