package runsplitter.common;

import java.io.IOException;

/**
 * A callback for saving an object.
 *
 * @param <T> The type of object to save.
 */
@FunctionalInterface
public interface SaveCallback<T> {

    /**
     * Saves the object.
     *
     * @param object The object to save.
     * @throws IOException If the object could not be saved.
     */
    void save(T object) throws IOException;
}
