package io.JesseChum.centroidfinder;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

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

    public void process() {
        Platform.startup(() -> {
            try {
                System.out.println("1️⃣ Starting process()");
                File videoFile = new File(inputPath);
                System.out.println("Video path: " + videoFile.toURI());
                System.out.println("File exists? " + videoFile.exists());

                if (!videoFile.exists()) {
                    System.out.println("❌ Video file not found: " + inputPath);
                    Platform.exit();
                    return;
                }

                System.out.println("2️⃣ Creating Media...");
                Media media = new Media(videoFile.toURI().toString());

                // 🔎 Add Media error listener
                media.setOnError(() -> {
                    System.out.println("❌ Media error: " + media.getError());
                });

                System.out.println("3️⃣ Creating MediaPlayer...");
                MediaPlayer player = new MediaPlayer(media);

                // 🔎 Add MediaPlayer error listener
                player.setOnError(() -> {
                    System.out.println("❌ MediaPlayer error: " + player.getError());
                });

                System.out.println("4️⃣ Creating MediaView...");
                MediaView view = new MediaView(player);
                

                javafx.scene.Group root = new javafx.scene.Group(view);
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setOpacity(0);  // Make the window invisible
                stage.show();

                // Prepare centroid finder components
                ColorDistanceFinder distanceFinder = new EuclideanColorDistance();
                ImageBinarizer binarizer = new DistanceImageBinarizer(distanceFinder, targetColor, threshold);
                BinaryGroupFinder groupFinder = new DfsBinaryGroupFinder();
                BinarizingImageGroupFinder centroidFinder = new BinarizingImageGroupFinder(binarizer, groupFinder);

                BufferedWriter writer = new BufferedWriter(new FileWriter(outputCsv));
                writer.write("time_seconds,x,y\n");

                System.out.println("5️⃣ Registering onReady listener...");
                player.setOnReady(() -> {
                    System.out.println("6️⃣ MediaPlayer is READY ✅");
                    double duration = player.getTotalDuration().toSeconds();
                    System.out.println("Processing video: " + inputPath);
                    System.out.println("Duration: " + duration + " seconds");

                    int frameRate = 30;
                    double step = 1.0 / frameRate;

                    for (double t = 0; t < duration; t += step) {
                        player.seek(Duration.seconds(t));
                        Image frame = view.snapshot(null, null);

                        if (frame == null) {
                            System.out.println("⚠️ Frame is null at " + t + "s");
                            writeLine(writer, t, -1, -1);
                            continue;
                        }

                        BufferedImage buffered = SwingFXUtils.fromFXImage(frame, null);
                        List<Group> groups = centroidFinder.findConnectedGroups(buffered);

                        if (groups.isEmpty()) {
                            writeLine(writer, t, -1, -1);
                        } else {
                            Group largest = groups.get(0);
                            System.out.println("🟢 Writing centroid at " + t + "s");
                            writeLine(writer, t, largest.centroid().x(), largest.centroid().y());
                        }
                    }

                    try {
                        writer.close();
                        System.out.println("✅ Processing complete. CSV saved to " + outputCsv);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Platform.exit();
                });

                System.out.println("7️⃣ Calling play()...");
                player.play();
                System.out.println("8️⃣ Waiting for MediaPlayer to be ready...");

            } catch (Exception e) {
                e.printStackTrace();
                Platform.exit();
            }
        });
    }

    private void writeLine(BufferedWriter writer, double time, double x, double y) {
        try {
            writer.write(String.format("%.2f,%.2f,%.2f%n", time, x, y));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
