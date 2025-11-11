package io.jessechum.centroidfinder;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.PauseTransition;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;



public class JavaFXVideoRunner{
    private final String inputPath;
    private final String outputCsv;
    private final int targetColor;
    private final int threshold;

    public JavaFXVideoRunner(String inputPath, String outputCsv, int targetColor, int threshold) {
        this.inputPath = inputPath;
        this.outputCsv = outputCsv;
        this.targetColor = targetColor;
        this.threshold = threshold;
    }
     public void startProcessing() {
        Platform.startup(() -> {
            try {
                System.out.println("1 Starting process()");
                File videoFile = new File(inputPath);
                System.out.println("Video path: " + videoFile.toURI());
                System.out.println("File exists? " + videoFile.exists());

                if (!videoFile.exists()) {
                    System.out.println(" Video file not found: " + inputPath);
                    Platform.exit();
                    return;
                }

                System.out.println("2 Creating Media...");
                Media media = new Media(videoFile.toURI().toString());

                media.setOnError(() ->
                    System.out.println("Media error: " + media.getError())
                );

                System.out.println("3 Creating MediaPlayer...");
                MediaPlayer player = new MediaPlayer(media);

                player.setOnError(() ->
                    System.out.println("MediaPlayer error: " + player.getError())
                );

                System.out.println("4 Creating MediaView...");
                MediaView view = new MediaView(player);

                javafx.scene.Group root = new javafx.scene.Group(view);
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setOpacity(0); // invisible window
                stage.setWidth(640);
                stage.setHeight(360);
                stage.show();
                stage.setX(-2000);

                // Prepare centroid finder components
                ColorDistanceFinder distanceFinder = new EuclideanColorDistance();
                ImageBinarizer binarizer = new DistanceImageBinarizer(distanceFinder, targetColor, threshold);
                BinaryGroupFinder groupFinder = new DfsBinaryGroupFinder();
                BinarizingImageGroupFinder centroidFinder = new BinarizingImageGroupFinder(binarizer, groupFinder);

                BufferedWriter writer = new BufferedWriter(new FileWriter(outputCsv));
                writer.write("time_seconds,x,y\n");

                System.out.println("5 Registering onReady listener...");
                player.setOnReady(() -> {
                    System.out.println("6 MediaPlayer is READY");
                    double duration = player.getTotalDuration().toSeconds();
                    System.out.println("Processing video: " + inputPath);
                    System.out.println("Duration: " + duration + " seconds");

                    int frameRate = 30;
                    double step = 1.0 / frameRate;

                    player.play();
                 new FrameProcessor(player, view, writer, centroidFinder, duration, step, outputCsv).start();
                });

                System.out.println("7 Calling play()...");
                player.play();
                System.out.println("8 Waiting for MediaPlayer to be ready...");

            } catch (Exception e) {
                e.printStackTrace();
                Platform.exit();
            }
        });
    }
}