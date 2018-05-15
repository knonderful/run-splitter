package runsplitter.common;

/**
 *
 */
@FunctionalInterface
public interface Thrower {

    void call() throws Throwable;
}
