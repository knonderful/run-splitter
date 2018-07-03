package runsplitter.impl;

import io.humble.video.Decoder;
import io.humble.video.Demuxer;
import io.humble.video.DemuxerStream;
import io.humble.video.MediaDescriptor;
import io.humble.video.MediaPacket;
import io.humble.video.MediaPicture;
import io.humble.video.awt.MediaPictureConverter;
import io.humble.video.awt.MediaPictureConverterFactory;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Deque;
import java.util.LinkedList;
import java.util.function.Supplier;
import java.util.stream.Stream;
import runsplitter.VideoFeed;
import runsplitter.VideoFeedHandler;

/**
 *
 */
public class VideoProcessor {

    private final Path videoFile;
    private Demuxer demuxer;
    private Decoder videoDecoder;
    private int videoStreamId = -1;

    public VideoProcessor(Path videoFile) {
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

        return demuxer.getDuration() / 1000L;
    }

    /**
     * Processes the video stream.
     *
     * @param feedHandler The {@link VideoFeedHandler}.
     * @throws InterruptedException
     * @throws IOException
     */
    public void process(VideoFeedHandler feedHandler) throws InterruptedException, IOException {
        videoDecoder.open(null, null);

        MediaPictureConverter converter = MediaPictureConverterFactory.createConverter(
                MediaPictureConverterFactory.HUMBLE_BGR_24,
                videoDecoder.getPixelFormat(),
                videoDecoder.getWidth(),
                videoDecoder.getHeight());

        Supplier<VideoFrameDecoder> decoderFactory = () -> {
            // Create a MediaPicture
            MediaPicture picture = MediaPicture.make(videoDecoder.getWidth(),
                    videoDecoder.getHeight(),
                    videoDecoder.getPixelFormat());
            // Create a copy of the Decoder
            Decoder decoderCopy = Decoder.make(videoDecoder);
            return new VideoFrameDecoder(picture, decoderCopy, new DefaultVideoFrame(converter));
        };

        Deque<MediaPacket> packetPool = new LinkedList<>();
        Deque<DefaultVideoFrameBlock> blocks = new LinkedList<>();
        VideoFeed feed = new VideoFeedImpl(blocks);

        MediaPacket currentPacket = MediaPacket.make();
        DefaultVideoFrameBlock currentBlock = null;

        while (demuxer.read(currentPacket) >= 0) {
            if (currentPacket.getStreamIndex() == videoStreamId) {
                // A key packet marks the start of a new block
                if (currentPacket.isKey()) {
                    if (currentBlock == null) {
                        currentBlock = new DefaultVideoFrameBlock(decoderFactory);
                    } else {
                        // The current block is finished
                        blocks.addFirst(currentBlock);
                        // Pass the current feed to the handler
                        if (!feedHandler.handle(feed)) {
                            System.out.println("The handler decided that it's had enough.");
                            return;
                        }

                        // Build up a history of at most 60 blocks
                        if (blocks.size() <= 60) {
                            // Add the current block to the cache
                            currentBlock = new DefaultVideoFrameBlock(decoderFactory);
                        } else {
                            // Reached the limit for block cache; reuse the oldest block
                            currentBlock = blocks.pollLast();
                            // Reset the current block and return all packets to the pool
                            currentBlock.reset(packetPool::add);
                        }
                    }
                }

                /*
                 * A video stream should start with a key frame, but in case that this is not the case, we would not
                 * have a block set here, so we make sure we do.
                 */
                if (currentBlock == null) {
                    currentBlock = new DefaultVideoFrameBlock(decoderFactory);
                }

                // Add a copy of the current packet to the block
                currentBlock.add(MediaPacket.make(currentPacket, true));

                // Get the next packet for reading
                currentPacket = packetPool.poll();
                if (currentPacket == null) {
                    currentPacket = MediaPacket.make();
                }
            }
        }

        if (currentBlock != null && !currentBlock.isEmpty()) {
            // The current block is finished
            blocks.addFirst(currentBlock);
            // Pass the current feed to the handler
            if (!feedHandler.handle(feed)) {
                System.out.println("The handler decided that it's had enough.");
            }
        }
    }

    private static class VideoFeedImpl implements VideoFeed {

        private final Deque<DefaultVideoFrameBlock> blocks;

        VideoFeedImpl(Deque<DefaultVideoFrameBlock> blocks) {
            this.blocks = blocks;
        }

        @Override
        public Stream<DefaultVideoFrameBlock> stream() {
            return blocks.stream();
        }
    }
}
