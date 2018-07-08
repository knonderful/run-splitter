package runsplitter.yi;

import static org.hamcrest.CoreMatchers.is;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

/**
 * Unit tests for {@link Geometry}.
 */
public class GeometryTest {

    @Test
    public void testProjectX() {
        Rectangle orgRect = new Rectangle(10, 40, 210, 90);
        int orgX = 30; // 10% of the original rectangle width
        Rectangle targetRect = new Rectangle(50, 10, 150, 20);
        MatcherAssert.assertThat(Geometry.projectX(orgRect, orgX, targetRect), is(60));
    }
    @Test
    public void testProjectY() {
        Rectangle orgRect = new Rectangle(10, 40, 210, 90);
        int orgY = 55; // 30% of the original rectangle height
        Rectangle targetRect = new Rectangle(50, 10, 150, 20);
        MatcherAssert.assertThat(Geometry.projectY(orgRect, orgY, targetRect), is(13));
    }
    
    @Test
    public void testProjectRectangle() {
        Rectangle orgOuter = new Rectangle(10, 40, 210, 90);
        Rectangle orgInner = new Rectangle(30, 55, 190, 60);
        Rectangle targetRect = new Rectangle(50, 10, 150, 20);
        MatcherAssert.assertThat(Geometry.projectRectangle(orgOuter, orgInner, targetRect), is(new Rectangle(60, 13, 140, 14)));
    }
}
