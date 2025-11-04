package io.JesseChum.centroidfinder;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import java.io.File;

public class JavaFXVideoExperiment extends Application {

    @Override
    public void start(Stage stage) {
        File videoFile = new File("src/main/resources/sample-1.mp4");

        if (!videoFile.exists()) {
            System.out.println("sample-1.mp4 not found. Place it in src/main/resources/");
            return;
        }

        Media media = new Media(videoFile.toURI().toString());
        MediaPlayer player = new MediaPlayer(media);
        MediaView mediaView = new MediaView(player);

        media.getMetadata().addListener((javafx.collections.MapChangeListener<String, Object>) change -> {
            if (change.wasAdded()) {
                System.out.println(change.getKey() + ": " + change.getValueAdded());
            }
        });

        player.play();

        StackPane root = new StackPane(mediaView);
        Scene scene = new Scene(root, 640, 360);

        stage.setTitle("JavaFX Video Experiment");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}