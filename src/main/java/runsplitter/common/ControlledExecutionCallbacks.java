package runsplitter.common;

import java.util.function.Consumer;

/**
 * Call-backs for a {@link ControlledExecution}.
 *
 * @param <T> The type of subject.
 */
public interface ControlledExecutionCallbacks<T> {

    /**
     * Submits the tasks that should be executed, regardless of the flush pattern.
     * <p>
     * As opposed to {@link #submitTasksForFlush(java.lang.Object, java.util.function.Consumer)}, this method is called
     * for every subject.
     *
     * @param subject      The subject.
     * @param taskConsumer The task consumer.
     */
    void submitTasks(T subject, Consumer<Runnable> taskConsumer);

    /**
     * Submits the tasks that should only be executed on a flush.
     * <p>
     * This method is only called for subjects that will be flushed.
     *
     * @param subject      The subject.
     * @param taskConsumer The task consumer.
     */
    void submitTasksForFlush(T subject, Consumer<Runnable> taskConsumer);
}
