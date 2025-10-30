package io.JesseChum.centroidfinder;
import io.JesseChum.centroidfinder.DfsBinaryGroupFinder;
import io.JesseChum.centroidfinder.EuclideanColorDistance;

import javafx.scene.image.Image;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;

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

     /**
     * Main method to process the video file frame-by-frame.
     */
    public void process() {
        try {
            File videoFile = new File(inputPath);
            if (!videoFile.exists()) {
                System.out.println("Video file not found: " + inputPath);
                return;
            }

            // Create JavaFX Media Player
            Media media = new Media(videoFile.toURI().toString());
            MediaPlayer player = new MediaPlayer(media);

            // Core centroid detection logic
            ColorDistanceFinder distanceFinder = new EuclideanColorDistance();
            ImageBinarizer binarizer = new DistanceImageBinarizer(distanceFinder, targetColor, threshold);
            BinaryGroupFinder groupFinder = new DfsBinaryGroupFinder();
            BinarizingImageGroupFinder centroidFinder = new BinarizingImageGroupFinder(binarizer, groupFinder);

            // Prepare CSV output
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputCsv));
            writer.write("time_seconds,x,y\n");

            player.setOnReady(() -> {
                double duration = player.getTotalDuration().toSeconds();
                System.out.println("Processing video: " + inputPath);
                System.out.println("Duration: " + duration + " seconds");

                int frameRate = 30; // or extract from metadata
                double step = 1.0 / frameRate;

                for (double t = 0; t < duration; t += step) {
                    player.seek(Duration.seconds(t));
                    Image frame = player.snapshot(null, null);

                    if (frame == null) {
                        writeLine(writer, t, -1, -1);
                        continue;
                    }

                    BufferedImage buffered = SwingFXUtils.fromFXImage(frame, null);
                    List<Group> groups = centroidFinder.findConnectedGroups(buffered);

                    if (groups.isEmpty()) {
                        writeLine(writer, t, -1, -1);
                    } else {
                        Group largest = groups.get(0); // already sorted descending
                        writeLine(writer, t, largest.centroid().x(), largest.centroid().y());
                    }
                }

                try {
                    writer.close();
                    System.out.println("Processing complete. Output saved to " + outputCsv);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            player.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeLine(BufferedWriter writer, double time, double x, double y) {
        try {
            writer.write(String.format("%.2f,%.2f,%.2f\n", time, x, y));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}