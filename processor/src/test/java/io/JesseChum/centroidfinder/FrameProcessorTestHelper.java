package io.JesseChum.centroidfinder;

import java.io.BufferedWriter;
import java.io.IOException;

public class FrameProcessorTestHelper {

    // Recreates the formatting logic from FrameProcessor for use in tests.
    public static void writeCsvRowForTest(BufferedWriter writer,
                                          double time,
                                          double x,
                                          double y) {
        try {
            writer.write(String.format("%.2f,%.2f,%.2f\n", time, x, y));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}