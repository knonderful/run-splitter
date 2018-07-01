package runsplitter.impl;

import io.humble.video.MediaPacket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import runsplitter.VideoFrame;
import runsplitter.VideoFrameBlock;

/**
 * The default {@link VideoFrameBlock} implementation.
 */
class DefaultVideoFrameBlock implements VideoFrameBlock {

    private final Collection<MediaPacket> packets;
    private final Supplier<VideoFrameDecoder> decoderFactory;

    DefaultVideoFrameBlock(Supplier<VideoFrameDecoder> decoderFactory) {
        this.packets = new ArrayList<>(64);
        this.decoderFactory = decoderFactory;
    }

    boolean isEmpty() {
        return packets.isEmpty();
    }

    void add(MediaPacket packet) {
        packets.add(packet);
    }

    void reset(Consumer<MediaPacket> packetConsumer) {
        packets.forEach(packetConsumer);
        packets.clear();
    }

    @Override
    public Stream<VideoFrame> stream() {
        return StreamSupport.stream(new BlockSpliterator(decoderFactory.get(), packets.size(), packets.iterator()), false);
    }

    private static class BlockSpliterator implements Spliterator<VideoFrame> {

        private static final int CHARACTERISTICS = Spliterator.ORDERED | Spliterator.SIZED | Spliterator.NONNULL | Spliterator.IMMUTABLE;
        private final VideoFrameDecoder decoder;
        private final long size;
        private final Supplier<MediaPacket> packetSupplier;

        BlockSpliterator(VideoFrameDecoder decoder, long size, Iterator<MediaPacket> packetIterator) {
            this.decoder = decoder;
            this.size = size;
            this.packetSupplier = () -> {
                if (packetIterator.hasNext()) {
                    return packetIterator.next();
                }
                return null;
            };
        }

        @Override
        public boolean tryAdvance(Consumer<? super VideoFrame> action) {
            VideoFrame frame = decoder.nextFrame(packetSupplier);
            if (frame == null) {
                return false;
            }

            action.accept(frame);
            return true;
        }

        @Override
        public Spliterator<VideoFrame> trySplit() {
            return null;
        }

        @Override
        public long estimateSize() {
            return size;
        }

        @Override
        public int characteristics() {
            return CHARACTERISTICS;
        }
    }
}
