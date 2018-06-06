package runsplitter.common;

/**
 * A callback for a {@link ControlledExecution} that allows for the submitting of tasks.
 *
 * @param <T> The type of subject.
 */
@FunctionalInterface
public interface ControlledExecutionCallback<T> {

    /**
     * Submits tasks for the provided subject.
     *
     * @param context The {@link ExecutionContext} for submitting tasks.
     * @param subject The subject.
     */
    void submitTasks(ExecutionContext context, T subject);
}
