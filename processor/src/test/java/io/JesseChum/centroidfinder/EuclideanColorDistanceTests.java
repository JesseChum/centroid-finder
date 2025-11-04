package io.JesseChum.centroidfinder;


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

  @Test
    public void testSimilarColors() {
        int color1 = 0x112233;
        int color2 = 0x113344;
        double result = distanceFinder.distance(color1, color2);
        assertTrue(result > 0 && result < 100, "Distance between close colors should be small");
    }

    @Test
public void testPrimaryColors() {
    int red = 0xFF0000;
    int green = 0x00FF00;
    double result = distanceFinder.distance(red, green);

    // sqrt((255)^2 + (255)^2) = about 360.62
    double expected = Math.sqrt(255 * 255 + 255 * 255);
    assertEquals(expected, result, 0.0001);
}

@Test
public void testDifferentBlueShades() {
    int navy = 0x000080;     // RGB (0, 0, 128)
    int skyBlue = 0x87CEEB;  // RGB (135, 206, 235)
    double result = distanceFinder.distance(navy, skyBlue);

    // Should be relatively large but not max
    assertTrue(result > 100 && result < 300, "Expected medium difference between navy and sky blue");
}

@Test
public void testOppositeEndsOfGrayScale() {
    int darkGray = 0x111111;
    int lightGray = 0xEEEEEE;
    double result = distanceFinder.distance(darkGray, lightGray);

    // Difference should be significant
    assertTrue(result > 350, "Dark gray vs light gray should have a large difference");
}

@Test
public void testOneChannelDifference() {
    int color1 = 0xAA0000;  // (170, 0, 0)
    int color2 = 0xAB0000;  // (171, 0, 0)
    double result = distanceFinder.distance(color1, color2);

    // Only 1 difference in red channel
    assertEquals(1.0, result, 0.0001);
}

@Test
public void testRandomColors() {
    int colorA = 0x123456;
    int colorB = 0x654321;
    double result = distanceFinder.distance(colorA, colorB);

    // Ensure it’s non-negative and within RGB range
    assertTrue(result >= 0 && result <= Math.sqrt(3 * 255 * 255),
        "Color distance should be within valid range (0 to max RGB distance)");
}

}