package runsplitter;

import java.awt.image.BufferedImage;

/**
 * A frame in a video stream.
 */
public interface VideoFrame {

    /**
     * Retrieves the {@link BufferedImage} that represents the frame.
     *
     * @return The {@link BufferedImage}.
     */
    BufferedImage getImage();

    /**
     * Retrieves the time stamp of this frame inside the stream.
     *
     * @return The time stamp in milliseconds.
     */
    long getTimestampMs();
}
