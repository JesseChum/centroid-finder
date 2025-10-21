import static org.junit.Assert.*;
import org.junit.Test;
import java.awt.image.BufferedImage;


public class DistanceImageBianarizerTest {
    
       // Mock color distance finder for predictable outputs
    private static class MockDistanceFinder implements ColorDistanceFinder {
        @Override
        public double distance(int color1, int color2) {
            // Simple "distance": absolute difference between color values
            return Math.abs(color1 - color2);
        }
    }

    @Test
    public void testExactTargetColorBecomesWhite() {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        img.setRGB(0, 0, 0x00FF00); // pure green

        DistanceImageBinarizer binarizer = new DistanceImageBinarizer(
                new MockDistanceFinder(),
                0x00FF00, // target green
                10 // threshold
        );

        int[][] binary = binarizer.toBinaryArray(img);
        assertEquals(1, binary[0][0]); // Should be white
    }

}
