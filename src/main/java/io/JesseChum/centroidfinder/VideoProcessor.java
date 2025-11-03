// package io.JesseChum.centroidfinder;

// import javafx.application.Platform;
// 
// import javafx.scene.Scene;
// 
// import javafx.scene.media.Media;
// import javafx.scene.media.MediaPlayer;
// import javafx.scene.media.MediaView;
// import javafx.stage.Stage;
// import javafx.util.Duration;
// 

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
    public void process(){
        JavaFXVideoRunner runner = new JavaFXVideoRunner(inputPath, outputCsv, targetColor, threshold);
        runner.startProcessing();
    }
}


//                     // Run frame capture in background thread, but perform snapshot on FX thread and transfer the Image back
//                     new Thread(() -> {
//                         try {
//                             for (double t = 0; t < duration; t += step) {
//                                 final double timestamp = t;

//                                 final CountDownLatch frameLatch = new CountDownLatch(1);
//                                 final Image[] frameHolder = new Image[1];

//                                 // On FX thread: seek, wait a bit, then snapshot and release latch
//                                 Platform.runLater(() -> {
//                                     try {
//                                         player.seek(Duration.seconds(timestamp));

//                                         PauseTransition wait = new PauseTransition(Duration.millis(200));
//                                         wait.setOnFinished(ev -> {
//                                             try {
//                                                 Image frame = view.snapshot(null, null);
//                                                 frameHolder[0] = frame;
//                                             } catch (Exception ex) {
//                                                 ex.printStackTrace();
//                                             } finally {
//                                                 frameLatch.countDown();
//                                             }
//                                         });
//                                         wait.play();
//                                     } catch (Exception ex) {
//                                         ex.printStackTrace();
//                                         frameLatch.countDown();
//                                     }
//                                 });

//                                 // Wait for snapshot (timeout to avoid hang)
//                                 frameLatch.await(3, TimeUnit.SECONDS);

//                                 Image frame = frameHolder[0];
//                                 if (frame == null) {
//                                     System.out.println("Frame is null at " + timestamp + "s");
//                                     writeLine(writer, timestamp, -1, -1);
//                                     continue;
//                                 }

//                                 int w = (int) frame.getWidth();
//                                 int h = (int) frame.getHeight();
//                                 int sample = frame.getPixelReader().getArgb(w / 2, h / 2);
//                                 System.out.printf("Frame %.2fs center pixel: 0x%08X%n", timestamp, sample);

//                                 BufferedImage buffered = SwingFXUtils.fromFXImage(frame, null);
//                                 List<Group> groups = centroidFinder.findConnectedGroups(buffered);

//                                 if (groups.isEmpty()) {
//                                     writeLine(writer, timestamp, -1, -1);
//                                 } else {
//                                     Group largest = groups.get(0);
//                                     System.out.println(" Writing centroid at " + timestamp + "s");
//                                     writeLine(writer, timestamp, largest.centroid().x(), largest.centroid().y());
//                                 }
//                             }

//                             // Give any remaining FX operations a moment to finish
//                             Thread.sleep(200);
//                             writer.close();
//                             System.out.println("Processing complete. CSV saved to " + outputCsv);
//                             Platform.exit();

//                         } catch (Exception e) {
//                             e.printStackTrace();
//                         }
//                     }, "frame-writer-thread").start();
//                 });

//                 System.out.println("7 Calling play()...");
//                 player.play();
//                 System.out.println("8 Waiting for MediaPlayer to be ready...");

//             } catch (Exception e) {
//                 e.printStackTrace();
//                 Platform.exit();
//             }
//         });
//     }

//     private void writeLine(BufferedWriter writer, double time, double x, double y) {
//         try {
//             writer.write(String.format("%.2f,%.2f,%.2f%n", time, x, y));
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }
// }