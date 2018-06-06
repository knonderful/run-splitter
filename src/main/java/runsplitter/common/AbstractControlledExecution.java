package runsplitter.common;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * The abstract base class for {@link ControlledExecution} implementations.
 *
 * @param <T> The subject type.
 */
public abstract class AbstractControlledExecution<T> implements ControlledExecution<T> {

    private final ControlledExecutionCallbacks<T> callbacks;
    private final List<Runnable> queuedTasks = new LinkedList<>();
    private final Consumer<Runnable> taskExecutor;
    private T lastSubject;
    private boolean lastSubjectFlushed = true;

    protected AbstractControlledExecution(ControlledExecutionCallbacks<T> callbacks, Consumer<Runnable> taskExecutor) {
        this.callbacks = callbacks;
        if (taskExecutor == null) {
            this.taskExecutor = Runnable::run;
        } else {
            this.taskExecutor = taskExecutor;
        }
    }

    /**
     * Determines whether the tasks should be flushed.
     *
     * @param subject The current subject.
     * @return {@code true} if tasks should be flushed.
     */
    protected abstract boolean shouldFlush(T subject);

    /**
     * Handles any post-flush logic in the implementing class.
     */
    protected void handlePostFlush() {
    }

    @Override
    public synchronized void process(T subject) {
        boolean willFlush = shouldFlush(subject);

        callbacks.submitTasks(subject, queuedTasks::add);
        if (willFlush) {
            flush();
        } else {
            lastSubject = subject;
            lastSubjectFlushed = false;
        }
    }

    @Override
    public synchronized void flush() {
        for (Iterator<Runnable> it = queuedTasks.iterator(); it.hasNext();) {
            Runnable task = it.next();
            taskExecutor.accept(task);
            it.remove();
        }

        if (!lastSubjectFlushed) {
            callbacks.submitTasksForFlush(lastSubject, taskExecutor);
            lastSubjectFlushed = true;
        }

        handlePostFlush();
    }
}
