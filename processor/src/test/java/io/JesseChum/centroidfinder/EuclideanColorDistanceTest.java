package io.JesseChum.centroidfinder;

import org.junit.Test;

import io.JesseChum.centroidfinder.EuclideanColorDistance;

import static org.junit.Assert.*;

public class EuclideanColorDistanceTest {
     @Test
    public void distanceIsZeroForSameColor() {
        EuclideanColorDistance d = new EuclideanColorDistance();
        assertEquals(0.0, d.distance(0x112233, 0x112233), 1e-9);
    }

    @Test
    public void distanceSymmetric() {
        EuclideanColorDistance d = new EuclideanColorDistance();
        double a = d.distance(0xFF0000, 0x00FF00);
        double b = d.distance(0x00FF00, 0xFF0000);
        assertEquals(a, b, 1e-9);
    }
}
