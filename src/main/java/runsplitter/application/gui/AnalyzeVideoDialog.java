package runsplitter.application.gui;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import runsplitter.VideoAnalyzer;
import runsplitter.VideoFrameHandler;
import runsplitter.VideoFrameHandlerChain;
import runsplitter.speedrun.MutableSpeedrun;

/**
 *
 */
public class AnalyzeVideoDialog {

    private static final Logger LOG = Logger.getLogger(AnalyzeVideoDialog.class.getName());

    public static MutableSpeedrun showAndWait(GuiHelper guiHelper, File videoFile, VideoAnalyzer videoAnalyzer) {
        Dialog<ButtonType> analyzeDialog = new Dialog<>();
        analyzeDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        ProgressBar progressBar = new ProgressBar();
        GridPane grid = GuiHelper.createFormGrid();
        grid.add(progressBar, 0, 0);
        analyzeDialog.getDialogPane().setContent(grid);
        analyzeDialog.setTitle(String.format("Analyze %s", videoFile.getName()));

        Task<MutableSpeedrun> analyzeTask = new Task<MutableSpeedrun>() {
            @Override
            protected MutableSpeedrun call() throws Exception {
                MutableSpeedrun run = new MutableSpeedrun(videoFile.getName());
                VideoFrameHandler frameHandler = videoAnalyzer.createFrameHandler(run);
                try (runsplitter.analyze.VideoAnalyzer analyzer = new runsplitter.analyze.VideoAnalyzer(videoFile.toPath())) {
                    long duration = analyzer.open();

                    VideoFrameHandler finalFrameHandler;
                    if (duration > 0) {
                        // Frame handler for updating the progress in the GUI
                        VideoFrameHandler progressFrameHandler = frame -> {
                            // TODO: Maybe not report the progress so often? Maybe at most 10 times per second or so...
                            double progress = ((double) (frame.getTimestampMs() * 100 / duration)) / 100.0;
                            Platform.runLater(() -> progressBar.setProgress(progress));
                        };

                        finalFrameHandler = new VideoFrameHandlerChain(frameHandler, progressFrameHandler);
                    } else {
                        finalFrameHandler = frameHandler;
                    }

                    boolean readMore = true;
                    while (readMore) {
                        readMore = analyzer.read(finalFrameHandler);
                    }
                }

                return run;
            }
        };

        Thread thread = new Thread(analyzeTask);
        thread.start();

        ButtonType buttonType = analyzeDialog.showAndWait().orElse(null);
        Worker.State taskState = analyzeTask.getState();
        LOG.log(Level.INFO, String.format("Task window ended with %s.", taskState));
        if (buttonType == ButtonType.OK && taskState == Worker.State.SUCCEEDED) {
            return analyzeTask.getValue();
        }

        // Might have to terminate the thread, in case it is still running
        thread.interrupt();

        return null;
    }
}
