package runsplitter.analyze;

import io.humble.video.MediaPicture;
import io.humble.video.awt.MediaPictureConverter;
import io.humble.video.awt.MediaPictureConverterFactory;
import java.awt.image.BufferedImage;
import runsplitter.VideoFrame;

/**
 *
 */
class VideoFrameImpl implements VideoFrame {

    private final MediaPictureConverter converter;
    private BufferedImage image;
    private long timestampMs;

    VideoFrameImpl(MediaPicture picture) {
        this.converter = MediaPictureConverterFactory.createConverter(MediaPictureConverterFactory.HUMBLE_BGR_24, picture);
    }

    void update(MediaPicture picture, long timestampMs) {
        this.image = converter.toImage(image, picture);
        this.timestampMs = timestampMs;
    }

    @Override
    public long getTimestampMs() {
        return timestampMs;
    }

    @Override
    public BufferedImage toImage() {
        return image;
    }
}
