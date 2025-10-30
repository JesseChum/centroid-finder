package io.JesseChum.centroidfinder;

public class VideoParser {
    private final String inputPath;
    private final String outputCsv;
    private final int targetColor;
    private final int threshold;

    /**
     * Parses command-line arguments.
     * Expected format:
     *   java -jar videoprocessor.jar <inputPath> <outputCsv> <targetColor> <threshold>
     *
     * Example:
     *   java -jar videoprocessor.jar src/main/resources/sample-1.mp4 output.csv "#00FF00" 30
     */
    public VideoParser(String[] args) {
        if (args.length != 4) {
            throw new IllegalArgumentException("Expected 4 arguments: <inputPath> <outputCsv> <targetColor> <threshold>");
        }

        this.inputPath = args[0];
        this.outputCsv = args[1];

        // Convert color string like "#00FF00" → integer 0x00FF00
        String colorString = args[2];
        if (!colorString.startsWith("#") || (colorString.length() != 7)) {
            throw new IllegalArgumentException("Color must be in format #RRGGBB");
        }
        this.targetColor = Integer.parseInt(colorString.substring(1), 16);

        try {
            this.threshold = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Threshold must be an integer.");
        }
    }

    // Getters
    public String getInputPath() {
        return inputPath;
    }

    public String getOutputCsv() {
        return outputCsv;
    }

    public int getTargetColor() {
        return targetColor;
    }

    public int getThreshold() {
        return threshold;
    }
}