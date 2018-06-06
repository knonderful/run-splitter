package runsplitter.common;

import java.util.function.Consumer;

/**
 * A {@link ControlledExecution} that only executes task after a specific time interval has passed.
 *
 * @param <T> The subject type.
 */
public class TimeControlledExecution<T> extends AbstractControlledExecution<T> {

    private final long minimumInterval;
    private long lastExecution;

    public TimeControlledExecution(ControlledExecutionCallback<T> callback, Consumer<Runnable> taskExecutor, long minumumInterval) {
        super(callback, taskExecutor);
        this.minimumInterval = minumumInterval;
    }

    @Override
    protected boolean shouldFlush(T subject) {
        return System.currentTimeMillis() - lastExecution > minimumInterval;
    }

    @Override
    protected void handlePostFlush() {
        lastExecution = System.currentTimeMillis();
    }
}
