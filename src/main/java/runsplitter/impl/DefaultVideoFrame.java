package runsplitter.impl;

import io.humble.video.MediaPicture;
import io.humble.video.awt.MediaPictureConverter;
import java.awt.image.BufferedImage;
import runsplitter.VideoFrame;

/**
 * Default {@link VideoFrame} implementation.
 */
class DefaultVideoFrame implements VideoFrame {

    private final MediaPictureConverter converter;
    private MediaPicture picture;
    private long timestampMs;

    DefaultVideoFrame(MediaPictureConverter converter) {
        this(converter, null, 0L);
    }

    private DefaultVideoFrame(MediaPictureConverter converter, MediaPicture picture, long timestampMs) {
        this.converter = converter;
        this.picture = picture;
        this.timestampMs = timestampMs;
    }

    void update(MediaPicture picture, long timestampMs) {
        this.picture = picture;
        this.timestampMs = timestampMs;
    }

    @Override
    public long getTimestampMs() {
        return timestampMs;
    }

    @Override
    public BufferedImage toImage() {
        return converter.toImage(null, picture);
    }

    @Override
    public VideoFrame copy() {
        return new DefaultVideoFrame(converter, MediaPicture.make(picture, true), timestampMs);
    }
}
