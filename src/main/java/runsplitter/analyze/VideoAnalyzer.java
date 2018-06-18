package runsplitter.analyze;

import io.humble.video.Decoder;
import io.humble.video.Demuxer;
import io.humble.video.DemuxerStream;
import io.humble.video.MediaDescriptor;
import io.humble.video.MediaPacket;
import io.humble.video.MediaPicture;
import io.humble.video.Rational;
import java.io.IOException;
import java.nio.file.Path;
import runsplitter.VideoFrameHandler;

/**
 * Class for analyzing video files.
 */
public class VideoAnalyzer implements AutoCloseable {

    private final Path videoFile;
    private Demuxer demuxer;
    private Decoder videoDecoder;
    private MediaPicture picture;
    private int videoStreamId = -1;
    private VideoFrameImpl frame;
    private MediaPacket packet;

    public VideoAnalyzer(Path videoFile) {
        this.videoFile = videoFile;
    }

    /**
     * Opens the file.
     *
     * @return The duration of the video stream in milliseconds.
     * @throws java.lang.InterruptedException
     * @throws java.io.IOException
     */
    public long open() throws InterruptedException, IOException {
        this.demuxer = Demuxer.make();
        demuxer.open(videoFile.toAbsolutePath().toString(), null, false, true, null, null);

        int numStreams = demuxer.getNumStreams();
        this.videoDecoder = null;
        for (int i = 0; i < numStreams; i++) {
            DemuxerStream stream = demuxer.getStream(i);
            Decoder decoder = stream.getDecoder();
            if (decoder != null && decoder.getCodecType() == MediaDescriptor.Type.MEDIA_VIDEO) {
                videoStreamId = i;
                videoDecoder = decoder;
                break;
            }
        }
        if (videoStreamId == -1) {
            throw new IOException("The provided file contains no video stream.");
        }

        videoDecoder.open(null, null);

        this.picture = MediaPicture.make(videoDecoder.getWidth(),
                videoDecoder.getHeight(),
                videoDecoder.getPixelFormat());

        this.frame = new VideoFrameImpl(picture);
        this.packet = MediaPacket.make();
        return demuxer.getDuration() / 1000L;
    }

    /**
     * Reads the next packet from the video stream.
     * <p>
     * Note that a single packet may contain multiple video frames. The {@link VideoFrameHandler} will be called for
     * every frame in the packet.
     *
     * @param frameHandler The {@link VideoFrameHandler}.
     * @return {@code false} if the end of the video stream was reached, otherwise {@code true}.
     * @throws InterruptedException
     * @throws IOException
     */
    public boolean read(VideoFrameHandler frameHandler) throws InterruptedException, IOException {
        if (demuxer.read(packet) >= 0) {
            if (packet.getStreamIndex() == videoStreamId) {
                int offset = 0;
                int bytesRead = 0;
                // Keep feeding data into the decoder until the entire packet is consumed
                do {
                    bytesRead += videoDecoder.decode(picture, packet, offset);
                    if (picture.isComplete()) {
                        processPicture(frame, picture, packet, frameHandler);
                    }
                    offset += bytesRead;
                } while (offset < packet.getSize());
            }
            return true;
        }

        // Final loop to flush remaining packets that haven't been decoded yet (some decoders do this)
        do {
            videoDecoder.decode(picture, null, 0);
            if (picture.isComplete()) {
                processPicture(frame, picture, packet, frameHandler);
            }
        } while (picture.isComplete());

        return false;
    }

    /**
     * Closes the video file.
     *
     * @throws java.lang.InterruptedException
     * @throws java.io.IOException
     */
    @Override
    public void close() throws InterruptedException, IOException {
        demuxer.close();
    }

    private void processPicture(VideoFrameImpl frame, MediaPicture picture, MediaPacket packet, VideoFrameHandler frameHandler) {
        frame.update(picture, calculateTimestamp(packet));
        frameHandler.handle(frame);
    }

    private static long calculateTimestamp(MediaPacket packet) {
        Rational timeBase = packet.getTimeBase();
        return timeBase.getNumerator() * 1000 * packet.getPts() / timeBase.getDenominator();
    }
}
