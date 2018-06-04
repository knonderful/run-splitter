package runsplitter;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import javafx.application.Application;
import runsplitter.analyze.VideoAnalyzer;
import runsplitter.application.gui.GuiApplication;
import runsplitter.speedrun.MutableSpeedrun;
import runsplitter.speedrun.Speedrun;
import runsplitter.speedrun.gson.JsonConverter;

/**
 * The main entry point for the application.
 */
public class Main {

    public static void oldMain(String[] args) throws InterruptedException, IOException {
        String filename = "YI_W1_100_PB_16_57_13.mkv";
//        String filename = "YI_W2_100_PB_24_07_76.mkv";
//        String filename = "YI_Clean_W12_100_PB_44_21_80.mkv";

        WindowFrameHandler windowFrameHandler = new WindowFrameHandler(false);
        MutableSpeedrun run = new MutableSpeedrun(filename);
        YoshisIslandFrameHandler yiFrameHandler = new YoshisIslandFrameHandler(run, false);

        VideoFrameHandlerChain handlerChain = new VideoFrameHandlerChain(yiFrameHandler, windowFrameHandler);
        VideoAnalyzer analyzer = new VideoAnalyzer(handlerChain);

        analyzer.playVideo("D:\\Videos\\Personal_Runs\\" + filename);

        // Write to JSON
        try (Writer writer = new FileWriter("analysis_data.json", false)) {
            JsonConverter.toJson(Collections.singletonList(run), writer);
        }

        // Read from JSON
        try (Reader reader = new FileReader("analysis_data.json")) {
            Collection<Speedrun> records = JsonConverter.fromJson(reader);
            records.forEach(System.out::println);
        }

        windowFrameHandler.dispose();
    }

    public static void main(String[] args) {
        Application.launch(GuiApplication.class, args);
    }
}
