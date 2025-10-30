package io.JesseChum.centroidfinder;

public class VideoApp {
     try {
            ArgumentParser parser = new ArgumentParser(args);

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
// public class VideoApp {
//     public static void main(String[] args){
//         VideoProcessor processor = new VideoProcessor("src/main/resources/sample-1.mp4", "output.csv", "#00ff00", 30);
//         processor.process();
//     }
// }