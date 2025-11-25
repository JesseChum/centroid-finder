package io.JesseChum.centroidfinder;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.BufferedWriter;
import java.util.List;

public class FrameProcessor {

    private final MediaPlayer player;
    private final MediaView view;
    private final BufferedWriter writer;
    private final BinarizingImageGroupFinder centroidFinder;
    private final double duration;
    private final double step;
    private final String outputCsv;
    private volatile boolean cancelled = false;
    private double nextCaptureTime;

    public FrameProcessor(MediaPlayer player, MediaView view,
        BufferedWriter writer, BinarizingImageGroupFinder centroidFinder,
        double duration, double step, String outputCsv) {
        this.player = player;
        this.view = view;
        this.writer = writer;
        this.centroidFinder = centroidFinder;
        this.duration = duration;
        this.step = step;
        this.outputCsv = outputCsv;

        // constructor input validation 
        if (duration <= 0 || step <= 0){
            throw new IllegalArgumentException("Duration and step must be > 0");
        }
    }
// Run frame capture in background thread, but perform snapshot on FX thread and transfer the Image back
    public void start(){
        nextCaptureTime = 0;

        player.setRate(2.0);
        player.play();

         AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (cancelled) {
                    stop();
                    player.stop();
                    closeWriter();
                    Platform.exit();
                    return;
                }

                double currentTime = player.getCurrentTime().toSeconds();

      // Have we reached the next capture timestamp?
                if (currentTime >= nextCaptureTime) {
                    captureFrame(currentTime);
                    nextCaptureTime += step;

                    // Are we done?
                    if (nextCaptureTime > duration) {
                        stop();
                        player.stop();
                        closeWriter();
                        System.out.println("Processing complete. Saved to: " + outputCsv);
                        Platform.exit();
                    }
                }
            }
        };

        timer.start();
    }

    /** Takes a snapshot of the current video frame */
    private void captureFrame(double timestamp) {
        // Must run on FX thread
        Image frame = view.snapshot(null, null);
        if (frame == null) {
            writeCsv(timestamp, -1, -1);
            return;
        }

        BufferedImage buffered = SwingFXUtils.fromFXImage(frame, null);
        List<Group> groups = centroidFinder.findConnectedGroups(buffered);

        if (groups.isEmpty()) {
            writeCsv(timestamp, -1, -1);
        } else {
            Group largest = groups.get(0);
            writeCsv(timestamp, largest.centroid().x(), largest.centroid().y());
        }
    }

    /** Writes a row to the CSV */
    //error handling to catch any CSV writing errors
    private void writeCsv(double time, double x, double y) {
        try {
            writer.write(String.format("%.2f,%.2f,%.2f%n", time, x, y));
        } catch (IOException e) {
            System.err.println("CSV write error: " + e.getMessage());
        }
    }

    /** Clean shutdown */
    //error hnadling to catch any writing closing errors
    private void closeWriter() {
        try {
            writer.close();
        } catch (IOException e) {
            System.err.println("Failed closing writer: " + e.getMessage());
        }
    }
    public void cancel() {
        cancelled = true;
    }
}
