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

    @Test
    public void testFarColorBecomesBlack() {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        img.setRGB(0, 0, 0x0000FF); // blue

        DistanceImageBinarizer binarizer = new DistanceImageBinarizer(
                new MockDistanceFinder(),
                0x00FF00, // green target
                10
        );

        int[][] binary = binarizer.toBinaryArray(img);
        assertEquals(0, binary[0][0]); // Should be black
    }

    @Test
    public void test2x2ImageMixedColors() {
        BufferedImage img = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        img.setRGB(0, 0, 0x00FF00); // same as target → white
        img.setRGB(1, 0, 0x00FF10); // close → white
        img.setRGB(0, 1, 0x0000FF); // far → black
        img.setRGB(1, 1, 0xFF0000); // far → black

        DistanceImageBinarizer binarizer = new DistanceImageBinarizer(
                new MockDistanceFinder(),
                0x00FF00,
                30
        );

        int[][] binary = binarizer.toBinaryArray(img);

        assertEquals(1, binary[0][0]);
        assertEquals(1, binary[0][1]);
        assertEquals(0, binary[1][0]);
        assertEquals(0, binary[1][1]);
    }

    @Test
    public void testAllPixelsBlackWhenThresholdZero() {
        BufferedImage img = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        img.setRGB(0, 0, 0xFFFFFF);
        img.setRGB(1, 0, 0xFFFFFF);
        img.setRGB(0, 1, 0xFFFFFF);
        img.setRGB(1, 1, 0xFFFFFF);

        DistanceImageBinarizer binarizer = new DistanceImageBinarizer(
                new MockDistanceFinder(),
                0x000000,
                0 // threshold = 0
        );

        int[][] binary = binarizer.toBinaryArray(img);
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 2; x++) {
                assertEquals(0, binary[y][x]);
            }
        }
    }

}
