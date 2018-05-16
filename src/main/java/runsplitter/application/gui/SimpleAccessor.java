package runsplitter.application.gui;

import java.util.function.Consumer;
import java.util.function.Supplier;
import runsplitter.common.Accessor;

/**
 * A simple {@link Accessor} that uses a {@link Supplier} and a {@link Consumer} for getting and setting values.
 *
 * @param <T> The type of the value.
 */
public class SimpleAccessor<T> implements Accessor<T> {

    private final Supplier<T> getter;
    private final Consumer<T> setter;

    /**
     * Creates a new instance.
     *
     * @param getter The getter.
     * @param setter The setter.
     */
    public SimpleAccessor(Supplier<T> getter, Consumer<T> setter) {
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public T get() {
        return getter.get();
    }

    @Override
    public void set(T value) {
        setter.accept(value);
    }
}
