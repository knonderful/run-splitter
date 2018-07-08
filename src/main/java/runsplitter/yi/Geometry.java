package runsplitter.yi;

/**
 * A utility class for geometry-related functions.
 */
public class Geometry {

    private Geometry() {
    }

    /**
     * Projects an X-coordinate from one {@link Rectangle} onto another {@link Rectangle} and returns the X-coordinate
     * in the target {@link Rectangle}.
     *
     * @param originalRectangle The original {@link Rectangle}.
     * @param originalX         The X-coordinate in the original {@link Rectangle}.
     * @param targetRectangle   The target {@link Rectangle}.
     * @return The X-coordinate in the target rectangle.
     */
    public static int projectX(Rectangle originalRectangle, int originalX, Rectangle targetRectangle) {
        // Calculate the horizontal offset of the X-coordinate in the original rectangle
        int originalOffset = originalX - originalRectangle.getLeft();
        // Check for boundaries
        int originalWidth = originalRectangle.getWidth();
        if (originalOffset < 0 || originalOffset > originalWidth) {
            throw new IllegalArgumentException(String.format("X-coordinate %d lies outside of the provided rectangle: %s.", originalX, originalRectangle));
        }
        // Calculate the offset of that same point in the target rectangle
        int targetOffset = originalOffset * targetRectangle.getWidth() / originalWidth;
        // Return the absolute X-coordinate
        return targetRectangle.getLeft() + targetOffset;
    }

    /**
     * Projects an Y-coordinate from one {@link Rectangle} onto another {@link Rectangle} and returns the Y-coordinate
     * in the target {@link Rectangle}.
     *
     * @param originalRectangle The original {@link Rectangle}.
     * @param originalY         The Y-coordinate in the original {@link Rectangle}.
     * @param targetRectangle   The target {@link Rectangle}.
     * @return The Y-coordinate in the target rectangle.
     */
    public static int projectY(Rectangle originalRectangle, int originalY, Rectangle targetRectangle) {
        // Calculate the horizontal offset of the Y-coordinate in the original rectangle
        int originalOffset = originalY - originalRectangle.getTop();
        // Check for boundaries
        int originalHeight = originalRectangle.getHeight();
        if (originalOffset < 0 || originalOffset > originalHeight) {
            throw new IllegalArgumentException(String.format("Y-coordinate %d lies outside of the provided rectangle: %s.", originalY, originalRectangle));
        }
        // Calculate the offset of that same point in the target rectangle
        int targetOffset = originalOffset * targetRectangle.getHeight() / originalHeight;
        // Return the absolute Y-coordinate
        return targetRectangle.getTop() + targetOffset;
    }

    /**
     * Projects a {@link Rectangle} that lies inside another {@link Rectangle] onto the target {@link Rectangle}.
     *
     * @param originalOuter   The {@link Rectangle} that contains the {@link Rectangle} that is to be projected.
     * @param originalInner   The {@link Rectangle} that is to be projected.
     * @param targetRectangle The target {@link Rectangle}.
     * @return The projection of the {@link Rectangle}.
     */
    public static Rectangle projectRectangle(Rectangle originalOuter, Rectangle originalInner, Rectangle targetRectangle) {
        int left = projectX(originalOuter, originalInner.getLeft(), targetRectangle);
        int top = projectY(originalOuter, originalInner.getTop(), targetRectangle);
        int right = projectX(originalOuter, originalInner.getRight(), targetRectangle);
        int bottom = projectY(originalOuter, originalInner.getBottom(), targetRectangle);
        return new Rectangle(left, top, right, bottom);
    }
}
