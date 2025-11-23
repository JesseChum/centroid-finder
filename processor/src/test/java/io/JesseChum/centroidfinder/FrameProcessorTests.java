package io.JesseChum.centroidfinder;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.BufferedWriter;
import java.io.StringWriter;
import java.io.IOException;

public class FrameProcessorTests {

    @Test
    public void testWriteCsvRowFormatsCorrectly() throws Exception {
        StringWriter sw = new StringWriter();
        BufferedWriter writer = new BufferedWriter(sw);

        FrameProcessorTestHelper.writeCsvRowForTest(writer, 1.23, 10, 20);

        writer.flush();
        assertEquals("1.23,10.00,20.00\n", sw.toString());
    }

    @Test
    public void testNoGroupsWritesNegativeValues() throws Exception {
    StringWriter sw = new StringWriter();
    BufferedWriter writer = new BufferedWriter(sw);

    // Simulate no groups found
    FrameProcessorTestHelper.writeCsvRowForTest(writer, 2.50, -1, -1);
    writer.flush();

    assertEquals("2.50,-1.00,-1.00\n", sw.toString());
}
@Test
public void testWriteCsvRowIOExceptionThrowsRuntimeException() {
    BufferedWriter writer = new BufferedWriter(new StringWriter()) {
        @Override
        public void write(String s) throws IOException {
            throw new IOException("Simulated failure");
        }
    };

    assertThrows(RuntimeException.class, () ->
        FrameProcessorTestHelper.writeCsvRowForTest(writer, 1.0, 5.0, 5.0)
    );
}
}

