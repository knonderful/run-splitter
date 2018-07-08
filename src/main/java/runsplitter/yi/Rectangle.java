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

    /**
     * Retrieves the width.
     *
     * @return The width in pixels.
     */
    public int getWidth() {
        return (right - left) + 1;
    }

    /**
     * Retrieves the height.
     *
     * @return The height in pixels.
     */
    public int getHeight() {
        return (bottom - top) + 1;
    }

    /**
     * Retrieves the surface area.
     *
     * @return The surface area in pixels.
     */
    public int getSurfaceArea() {
        return getWidth() * getHeight();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + this.left;
        hash = 67 * hash + this.top;
        hash = 67 * hash + this.right;
        hash = 67 * hash + this.bottom;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Rectangle other = (Rectangle) obj;
        if (this.left != other.left) {
            return false;
        }
        if (this.top != other.top) {
            return false;
        }
        if (this.right != other.right) {
            return false;
        }
        return this.bottom == other.bottom;
    }

    @Override
    public String toString() {
        return String.format("(%d, %d)-(%d, %d)", left, top, right, bottom);
    }
}
