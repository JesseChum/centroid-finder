package io.jessechum.centroidfinder;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;

public class ThumbNailGenerator extends Application {

    private static String videoPath;

    @Override
    public void start(Stage stage) {
        try {
            File videoFile = new File(videoPath);
            if (!videoFile.exists()) {
                System.err.println(" Video not found: " + videoPath);
                Platform.exit();
                return;
            }

            Media media = new Media(videoFile.toURI().toString());
            MediaPlayer player = new MediaPlayer(media);
            MediaView mediaView = new MediaView(player);

            StackPane root = new StackPane(mediaView);
            Scene scene = new Scene(root, 640, 360);
            stage.setScene(scene);
            stage.setTitle("Thumbnail Generator");
            stage.show();

            player.setOnReady(() -> {
                try {
                    WritableImage image = new WritableImage(640, 360);
                    mediaView.snapshot(null, image);

                    File outputDir = new File("output");
                    if (!outputDir.exists()) outputDir.mkdir();

                    File outputFile = new File(outputDir, "thumbnail.png");
                    ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", outputFile);

                    System.out.println(" Thumbnail saved at: " + outputFile.getAbsolutePath());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    Platform.exit();
                }
            });

            player.play();
        } catch (Exception e) {
            e.printStackTrace();
            Platform.exit();
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: ThumbNailGenerator <videoFile>");
            System.exit(1);
        }
        videoPath = args[0];
        launch(args);
    }
}