package runsplitter;

import java.awt.image.BufferedImage;

/**
 * A frame in a video stream.
 */
public interface VideoFrame {

    /**
     * Creates a {@link BufferedImage} that represents the frame.
     * <p>
     * Note that a new {@link BufferedImage} instance is created every time this method is called.
     *
     * @return The {@link BufferedImage}.
     */
    BufferedImage toImage();

    /**
     * Retrieves the time stamp of this frame inside the stream.
     *
     * @return The time stamp in milliseconds.
     */
    long getTimestampMs();

    /**
     * Creates a deep copy of this {@link VideoFrame}.
     *
     * @return The new {@link VideoFrame}.
     */
    VideoFrame copy();
}
