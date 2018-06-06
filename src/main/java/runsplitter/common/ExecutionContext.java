package runsplitter.common;

/**
 * A context object for {@link ControlledExecution#process(java.lang.Object)} calls.
 * <p>
 * Tasks can either be submitted to a specific slot or to a general queue. Tasks that are not submitted to a slot will
 * eventually all be executed, whereas slotted tasks will be overwritten, resulting in only the last task to be executed
 * for an execution cycle.
 */
public interface ExecutionContext {

    /**
     * Submits a task.
     * <p>
     * The task is guaranteed to be executed eventually.
     *
     * @param task The task.
     */
    void submit(Runnable task);

    /**
     * Submits a task into the specified slot.
     * <p>
     * If there is already a task in the specified slot it will be replaced by the new task.
     *
     * @param slot The slot.
     * @param task The task.
     */
    void submit(int slot, Runnable task);
}
