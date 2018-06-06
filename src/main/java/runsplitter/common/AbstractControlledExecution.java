package runsplitter.common;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * The abstract base class for {@link ControlledExecution} implementations.
 *
 * @param <T> The subject type.
 */
public abstract class AbstractControlledExecution<T> implements ControlledExecution<T> {

    private final ControlledExecutionCallback<T> callback;
    private final ExecutionContextImpl context;
    private final Consumer<Runnable> taskExecutor;

    protected AbstractControlledExecution(ControlledExecutionCallback<T> callback, Consumer<Runnable> taskExecutor) {
        this.callback = callback;
        this.context = new ExecutionContextImpl();
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
        callback.submitTasks(context, subject);
        if (shouldFlush(subject)) {
            flush();
        }
    }

    @Override
    public synchronized void flush() {
        context.callAll(taskExecutor);
        handlePostFlush();
    }

    private static class ExecutionContextImpl implements ExecutionContext {

        private final List<Runnable> unslottedTasks;
        private final Map<Integer, Runnable> slottedTasks;

        ExecutionContextImpl() {
            this.unslottedTasks = new LinkedList<>();
            this.slottedTasks = new HashMap<>(4);
        }

        @Override
        public void submit(Runnable task) {
            unslottedTasks.add(task);
        }

        @Override
        public void submit(int slot, Runnable task) {
            slottedTasks.put(slot, task);
        }

        void callAll(Consumer<Runnable> executor) {
            unslottedTasks.forEach(executor::accept);
            unslottedTasks.clear();
            slottedTasks.values().forEach(executor::accept);
            slottedTasks.clear();
        }
    }
}
