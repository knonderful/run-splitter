package runsplitter.common;

/**
 *
 */
public class UnknownElementException extends SplitterException {

    public UnknownElementException() {
    }

    public UnknownElementException(String message) {
        super(message);
    }

    public UnknownElementException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownElementException(Throwable cause) {
        super(cause);
    }

}
