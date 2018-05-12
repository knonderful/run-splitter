package runsplitter.analyze;

import io.humble.video.Decoder;
import io.humble.video.Demuxer;
import io.humble.video.DemuxerStream;
import io.humble.video.MediaDescriptor;
import io.humble.video.MediaPacket;
import io.humble.video.MediaPicture;
import java.io.IOException;
import runsplitter.VideoFrameHandler;

/**
 *
 */
public class VideoAnalyzer {

    private final VideoFrameHandler frameHandler;

    public VideoAnalyzer(VideoFrameHandler frameHandler) {
        this.frameHandler = frameHandler;
    }

    public void playVideo(String filename) throws InterruptedException, IOException {
        Demuxer demuxer = Demuxer.make();
        demuxer.open(filename, null, false, true, null, null);

        int numStreams = demuxer.getNumStreams();
        int videoStreamId = -1;
        Decoder videoDecoder = null;
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
            throw new RuntimeException("The provided file contains no video stream.");
        }

        videoDecoder.open(null, null);

        final MediaPicture picture = MediaPicture.make(videoDecoder.getWidth(),
                videoDecoder.getHeight(),
                videoDecoder.getPixelFormat());

        final VideoFrameImpl frame = new VideoFrameImpl(picture);

        final MediaPacket packet = MediaPacket.make();
        while (demuxer.read(packet) >= 0) {
            if (packet.getStreamIndex() == videoStreamId) {
                int offset = 0;
                int bytesRead = 0;
                do {
                    bytesRead += videoDecoder.decode(picture, packet, offset);
                    if (picture.isComplete()) {
                        processPicture(frame, picture);
                    }
                    offset += bytesRead;
                } while (offset < packet.getSize());
            }
        }

        // Final loop to flush remaining packets that haven't been decoded yet (some decoders do this)
        do {
            videoDecoder.decode(picture, null, 0);
            if (picture.isComplete()) {
                processPicture(frame, picture);
            }
        } while (picture.isComplete());

        demuxer.close();
    }

    private void processPicture(VideoFrameImpl frame, MediaPicture picture) {
        frame.update(picture);
        frameHandler.handle(frame);
    }
}
