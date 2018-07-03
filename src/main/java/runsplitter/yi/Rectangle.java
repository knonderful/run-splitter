package runsplitter.yi;

/**
 * A rectangle in 2D space.
 */
public class Rectangle {

    private final int left;
    private final int top;
    private final int right;
    private final int bottom;

    /**
     * Creates a new instance.
     *
     * @param left   The left coordinate.
     * @param top    The top coordinate.
     * @param right  The right coordinate.
     * @param bottom The bottom coordinate.
     */
    public Rectangle(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    /**
     * Retrieves the left coordinate.
     *
     * @return The coordinate.
     */
    public int getLeft() {
        return left;
    }

    /**
     * Retrieves the top coordinate.
     *
     * @return The coordinate.
     */
    public int getTop() {
        return top;
    }

    /**
     * Retrieves the right coordinate.
     *
     * @return The coordinate.
     */
    public int getRight() {
        return right;
    }

    /**
     * Retrieves the bottom coordinate.
     *
     * @return The coordinate.
     */
    public int getBottom() {
        return bottom;
    }
}
