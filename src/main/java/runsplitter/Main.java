package runsplitter;

import java.io.IOException;
import runsplitter.analyze.VideoAnalyzer;

/**
 * The main entry point for the application.
 */
public class Main {

    public static void main(String[] args) throws InterruptedException, IOException {
        String filename = "D:\\Videos\\Personal_Runs\\YI_W1_100_PB_16_57_13.mkv";
//        String filename = "D:\\Videos\\Personal_Runs\\YI_W2_100_PB_24_07_76.mkv";
//        String filename = "D:\\Videos\\Personal_Runs\\YI_Clean_W12_100_PB_44_21_80.mkv";

        WindowFrameHandler windowFrameHandler = new WindowFrameHandler(false);
        YoshisIslandFrameHandler yiFrameHandler = new YoshisIslandFrameHandler(false);

        VideoFrameHandlerChain handlerChain = new VideoFrameHandlerChain(yiFrameHandler, windowFrameHandler);
        VideoAnalyzer analyzer = new VideoAnalyzer(handlerChain);

        analyzer.playVideo(filename);

        windowFrameHandler.dispose();
    }

}
