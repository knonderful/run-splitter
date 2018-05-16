package runsplitter.common;

/**
 * An {@link Accessor} provides both read and write access to a value.
 * <p>
 * In practice this can be used to encapsulate getters and setters in a generic way.
 *
 * @param <T> The type of the value.
 */
public interface Accessor<T> {

    /**
     * Gets the value.
     *
     * @return The value.
     */
    T get();

    /**
     * Sets the value.
     *
     * @param value The value.
     */
    void set(T value);
}
