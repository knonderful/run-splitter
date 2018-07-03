package runsplitter;

import java.nio.file.Paths;
import runsplitter.impl.VideoProcessor;
import runsplitter.speedrun.Instant;
import runsplitter.yi.YoshisIslandCleanFeedHandler;

/**
 *
 */
public class TestMain {

    public static void main(String[] args) throws Exception {
        VideoProcessor processor = new VideoProcessor(Paths.get("D:", "Videos", "Personal_Runs", "YI_Clean_W12_100_PB_44_21_80.mkv"));
        long duration = processor.open();
        System.out.println("Video duration: " + new Instant(duration).toTimestamp());
        processor.process(new YoshisIslandCleanFeedHandler());
    }
}
