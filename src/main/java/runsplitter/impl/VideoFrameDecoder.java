package runsplitter.impl;

import io.humble.video.Decoder;
import io.humble.video.MediaPacket;
import io.humble.video.MediaPicture;
import io.humble.video.Rational;
import java.util.function.Supplier;
import runsplitter.VideoFrame;

/**
 * The default {@link VideoFrameDecoder} implementation.
 */
class VideoFrameDecoder {

    private final MediaPicture picture;
    private final Decoder decoder;
    private final DefaultVideoFrame frame;
    private Rational timeBase;

    VideoFrameDecoder(MediaPicture picture, Decoder decoder, DefaultVideoFrame frame) {
        this.picture = picture;
        this.decoder = decoder;
        this.frame = frame;
    }

    /**
     * Decodes the next {@link VideoFrame}.
     *
     * @param packetSupplier The {@link MediaPacket} supplier.
     * @return The next {@link VideoFrame} or {@code null} if no more frames are available.
     */
    VideoFrame nextFrame(Supplier<MediaPacket> packetSupplier) {
        // Read packets until we can produce a frame
        MediaPacket packet = packetSupplier.get();
        while (packet != null) {
            /*
             * We need to take the timebase from the packet, since the one on the MediaPicture is not always set
             * correctly.
             */
            if (timeBase == null) {
                timeBase = packet.getTimeBase();
            }
            int offset = 0;
            int bytesRead = 0;

            // Keep feeding data into the decoder until the entire packet is consumed
            do {
                bytesRead += decoder.decode(picture, packet, offset);
                if (picture.isComplete()) {
                    return updateFrame();
                }
                offset += bytesRead;
            } while (offset < packet.getSize());

            // This packet did not result in a frame, so let's try the next packet
            packet = packetSupplier.get();
        }

        // We have finished processing all packets, but some decoders keep an internal cache. We need to flush these packets.
        decoder.decode(picture, null, 0);
        if (picture.isComplete()) {
            return updateFrame();
        }

        return null;
    }

    private DefaultVideoFrame updateFrame() {
        frame.update(picture, calculateTimestamp(timeBase, picture.getPts()));
        return frame;
    }

    private static long calculateTimestamp(Rational timeBase, long pts) {
        return timeBase.getNumerator() * 1000 * pts / timeBase.getDenominator();
    }
}
