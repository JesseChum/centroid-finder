package io.JesseChum.centroidfinder;

public class VideoApp {
    public static void main(String[] args) {
        try {
            VideoParser parser = new VideoParser(args);

            VideoProcessor processor = new VideoProcessor(
                parser.getInputPath(),
                parser.getOutputCsv(),
                parser.getTargetColor(),
                parser.getThreshold()
            );

            processor.process();

        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            System.err.println("Usage: java -jar videoprocessor.jar <inputPath> <outputCsv> <targetColor> <threshold>");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}