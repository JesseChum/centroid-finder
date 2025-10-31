package io.JesseChum.centroidfinder;

import org.junit.Test;
import static org.junit.Assert.*;

public class VideoParserTest {
     @Test
    public void parsesHappyPath() {
        String[] args = {"in.mp4", "out.csv", "#00FF00", "30"};
        VideoParser p = new VideoParser(args);
        assertEquals("in.mp4", p.getInputPath());
        assertEquals("out.csv", p.getOutputCsv());
        assertEquals(0x00FF00, p.getTargetColor());
        assertEquals(30, p.getThreshold());
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsBadColor() {
        new VideoParser(new String[]{"in", "out", "00FF00", "30"}); // missing '#'
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsWrongArity() {
        new VideoParser(new String[]{"a","b","c"}); // 3 instead of 4
    }
}
