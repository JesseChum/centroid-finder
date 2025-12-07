 package io.JesseChum.centroidfinder;

import java.io.IOException;

public class VideoProcessor {
    private final String inputPath;
    private final String outputCsv;
    private final int targetColor;
    private final int threshold;

    public VideoProcessor(String inputPath, String outputCsv, int targetColor, int threshold) {
        this.inputPath = inputPath;
        this.outputCsv = outputCsv;
        this.targetColor = targetColor;
        this.threshold = threshold;
    }
    
    public void process() {
        try {
            FFmpegVideoProcessor processor = new FFmpegVideoProcessor(inputPath, outputCsv, targetColor, threshold);
            processor.process();
        } catch (IOException | InterruptedException e) {
            System.err.println("Error processing video: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}


