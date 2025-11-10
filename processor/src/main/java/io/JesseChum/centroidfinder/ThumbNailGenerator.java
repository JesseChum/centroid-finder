package io.jessechum.centroidfinder;

import javafx.application.Application;
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
                System.err.println("Video not found: " + videoPath);
                System.exit(1);
            }
            //1.recieve a video
            // Load video
            Media media = new Media(videoFile.toURI().toString());
            MediaPlayer player = new MediaPlayer(media);
            MediaView mediaView = new MediaView(player);

            player.setOnReady(() -> {
                try {
                    //2. grab first frame
                    // Wait until first frame is ready
                    WritableImage image = new WritableImage(640, 360);
                    mediaView.snapshot(null, image);

                    // Save thumbnail
                    File output = new File("thumbnail.png");
                    ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", output);

                    System.out.println("thumbnail.png");
                    System.exit(0);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(2);
                }
            });

            player.play();
            new Scene(new StackPane(mediaView), 640, 360);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(3);
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
    
