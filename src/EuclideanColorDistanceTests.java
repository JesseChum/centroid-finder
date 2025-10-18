import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class EuclideanColorDistanceTests {

    private final EuclideanColorDistance distanceFinder = new EuclideanColorDistance();

    @Test
    public void testSameColor() {
        int color = 0x112233;
        double result = distanceFinder.distance(color, color);
        assertEquals(0.0, result, 0.0001);
    }

    @Test
        public void testBlackAndWhite() {
        int black = 0x000000;  // RGB (0, 0, 0)
        int white = 0xFFFFFF;  // RGB (255, 255, 255)

        double result = distanceFinder.distance(black, white);

        // Expected distance = sqrt((255)^2 + (255)^2 + (255)^2)
        double expected = Math.sqrt(3 * 255 * 255);

        assertEquals(expected, result, 0.0001);
}  

}