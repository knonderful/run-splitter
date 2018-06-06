package runsplitter.common;

/**
 * Controls (limits) the execution of a tasks.
 * <p>
 * This can be useful for things like GUI updates for a process that runs in a loop. Updating the GUI for every loop
 * iteration might be overkill and make the GUI unresponsive. In addition to limiting the execution a
 * {@link ControlledExecution} allows for the caller to explicitly "flush" any queued tasks. This can be useful to
 * make sure to enforce the final state of the task, for instance to make sure that a progress actually makes it to
 * 100 percent, rather than being stuck on the last value that was executed.
 *
 * @param <T> The subject type.
 */
public interface ControlledExecution<T> {

    /**
     * Processes a new subject.
     *
     * @param subject The subject.
     */
    public void process(T subject);

    /**
     * Flushes all tasks.
     */
    public void flush();
}
