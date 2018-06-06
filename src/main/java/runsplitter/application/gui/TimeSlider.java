package runsplitter.application.gui;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import runsplitter.speedrun.Instant;

/**
 * A {@link Slider} for a time line.
 */
public class TimeSlider extends Parent {

    private static final double SLIDER_MAX = 100;
    private final Slider slider;
    private final Label sliderTimeLabel;
    private long startMs;
    private long endMs;
    private long timeRangeMs;

    /**
     * Creates a new instance.
     */
    public TimeSlider() {
        this.slider = new Slider(0, SLIDER_MAX, 0);
        this.sliderTimeLabel = new Label();

        slider.valueProperty().addListener((obs, oldVal, newVal) -> updateSliderTimeLabel(newVal.doubleValue()));

        VBox vbox = new VBox(slider, sliderTimeLabel);
        vbox.setAlignment(Pos.CENTER);
        super.getChildren().add(vbox);
    }

    /**
     * Retrieves the {@link Slider}.
     *
     * @return The {@link Slider}
     */
    public Slider getSlider() {
        return slider;
    }

    /**
     * Retrieves the start time.
     *
     * @return The start time in milliseconds.
     */
    public long getStartMs() {
        return startMs;
    }

    /**
     * Sets the start time.
     *
     * @param startMs The start time in milliseconds.
     */
    public void setStartMs(long startMs) {
        updateTimeRangeMs(startMs, this.endMs);
    }

    /**
     * Retrieves the end time.
     *
     * @return The end time in milliseconds.
     */
    public long getEndMs() {
        return endMs;
    }

    /**
     * Sets the end time.
     *
     * @param endMs The end time in milliseconds.
     */
    public void setEndMs(long endMs) {
        updateTimeRangeMs(this.startMs, endMs);
    }

    /**
     * Retrieves the current time.
     *
     * @return The time in milliseconds.
     */
    public long getTimeMs() {
        return timeFromProgress(slider.getValue());
    }

    /**
     * Sets the current time.
     *
     * @param timeMs The current time in milliseconds.
     */
    public void setTimeMs(long timeMs) {
        slider.setValue(progressFromTime(timeMs));
    }

    private void updateSliderTimeLabel(double sliderValue) {
        sliderTimeLabel.setText(new Instant(timeFromProgress(sliderValue)).toTimestamp());
    }

    private void updateTimeRangeMs(long startMs, long endMs) {
        this.startMs = startMs;
        this.endMs = endMs;
        this.timeRangeMs = endMs - startMs;
        // Feed the time back into the slider to update the GUI with the change
        slider.setValue(slider.getValue());
    }

    private double progressFromTime(long currentMs) {
        double sliderValue = (((double) currentMs) / ((double) timeRangeMs)) * SLIDER_MAX;
        if (sliderValue < 0) {
            return 0;
        }
        if (sliderValue > SLIDER_MAX) {
            return SLIDER_MAX;
        }
        return sliderValue;
    }

    private long timeFromProgress(double progress) {
        long time = (long) ((progress / SLIDER_MAX) * ((double) timeRangeMs));
        if (time < startMs) {
            return startMs;
        }
        if (time > endMs) {
            return endMs;
        }
        return time;
    }
}
