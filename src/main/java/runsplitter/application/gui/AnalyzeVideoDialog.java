package runsplitter.application.gui;

import java.io.File;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.GridPane;
import runsplitter.VideoAnalyzer;
import runsplitter.VideoFrame;
import runsplitter.VideoFrameHandler;
import runsplitter.VideoFrameHandlerChain;
import runsplitter.common.ControlledExecutionCallbacks;
import runsplitter.common.TimeControlledExecution;
import runsplitter.speedrun.MutableSpeedrun;

/**
 *
 */
public class AnalyzeVideoDialog {

    private static final Logger LOG = Logger.getLogger(AnalyzeVideoDialog.class.getName());

    public static MutableSpeedrun showAndWait(GuiHelper guiHelper, File videoFile, VideoAnalyzer videoAnalyzer) {
        Dialog<ButtonType> analyzeDialog = new Dialog<>();
        analyzeDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TimeSlider timeSlider = new TimeSlider();
        // Don't let the user control the slider
        timeSlider.getSlider().setDisable(true);
        timeSlider.setTimeMs(0);
        GridPane grid = GuiHelper.createFormGrid();
        grid.add(timeSlider, 0, 0);
        analyzeDialog.getDialogPane().setContent(grid);
        analyzeDialog.setTitle(String.format("Analyze %s", videoFile.getName()));

        Task<MutableSpeedrun> analyzeTask = new Task<MutableSpeedrun>() {
            @Override
            protected MutableSpeedrun call() throws Exception {
                MutableSpeedrun run = new MutableSpeedrun(videoFile.getName());
                VideoFrameHandler frameHandler = videoAnalyzer.createFrameHandler(run);
                try (runsplitter.analyze.VideoAnalyzer analyzer = new runsplitter.analyze.VideoAnalyzer(videoFile.toPath())) {
                    long duration = analyzer.open();
                    if (duration > 0) {
                        timeSlider.setEndMs(duration);
                    }

                    // Callbacks for performing GUI-related actions based on incoming video frames
                    ControlledExecutionCallbacks<VideoFrame> analysisExecutionCallbacks = new ControlledExecutionCallbacks<VideoFrame>() {
                        @Override
                        public void submitTasks(VideoFrame subject, Consumer<Runnable> taskConsumer) {
                            // Nothing to be done.
                        }

                        @Override
                        public void submitTasksForFlush(VideoFrame frame, Consumer<Runnable> taskConsumer) {
                            taskConsumer.accept(() -> timeSlider.setTimeMs(frame.getTimestampMs()));
                        }
                    };

                    // Execute the GUI update at most once every 100ms
                    TimeControlledExecution<VideoFrame> guiUpdateExecution = new TimeControlledExecution<>(
                            analysisExecutionCallbacks,
                            Platform::runLater,
                            100L);

                    // Frame handler for updating the GUI with the analysis
                    VideoFrameHandler guiUpdateFrameHandler = guiUpdateExecution::process;

                    VideoFrameHandlerChain frameHandlerChain = new VideoFrameHandlerChain(frameHandler, guiUpdateFrameHandler);

                    boolean readMore = true;
                    while (readMore) {
                        readMore = analyzer.read(frameHandlerChain);
                    }

                    // Explicitly update the GUI with the last state
                    guiUpdateExecution.flush();
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
        if (thread.isAlive()) {
            thread.interrupt();
        }

        return null;
    }
}
