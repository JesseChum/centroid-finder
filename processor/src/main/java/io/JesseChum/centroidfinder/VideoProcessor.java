 package io.jessechum.centroidfinder;

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
    public void process(){
        JavaFXVideoRunner runner = new JavaFXVideoRunner(inputPath, outputCsv, targetColor, threshold);
        runner.startProcessing();
    }
}


