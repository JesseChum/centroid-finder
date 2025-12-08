package io.JesseChum.centroidfinder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;

public class FFmpegVideoProcessor {
    private final String inputPath;
    private final String outputCsv;
    private final int targetColor;
    private final int threshold;
    private static final int FRAME_RATE = 30;

    public FFmpegVideoProcessor(String inputPath, String outputCsv, int targetColor, int threshold) {
        this.inputPath = inputPath;
        this.outputCsv = outputCsv;
        this.targetColor = targetColor;
        this.threshold = threshold;
    }

    public void process() throws IOException, InterruptedException {
        System.out.println("Processing video: " + inputPath);

        // Get video duration
        double duration = getVideoDuration();

        // Create temporary directory for frames
        File tempDir = new File(System.getProperty("java.io.tmpdir"), "video_frames_" + System.currentTimeMillis());
        if (!tempDir.exists() && !tempDir.mkdirs()) {
            throw new IOException("Failed to create temp directory: " + tempDir);
        }

        try {
            // Extract frames using ffmpeg
            extractFrames(tempDir);

            // Process frames and write centroids to CSV
            processFrames(tempDir, duration);

            System.out.println("Processing complete. Output saved to: " + outputCsv);
        } finally {
            // Cleanup temp directory
            deleteDirectory(tempDir);
        }
    }

    private double getVideoDuration() throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(
            "ffprobe",
            "-v", "error",
            "-show_entries", "format=duration",
            "-of", "default=noprint_wrappers=1:nokey=1",
            inputPath
        );

        Process process = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String durationStr = reader.readLine();
        process.waitFor();

        if (durationStr == null || durationStr.trim().isEmpty()) {
            throw new IOException("Could not determine video duration");
        }

        return Double.parseDouble(durationStr.trim());
    }

    private void extractFrames(File tempDir) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(
            "ffmpeg",
            "-i", inputPath,
            "-vf", "fps=" + FRAME_RATE,
            "-q:v", "2",
            "-loglevel", "error",
            tempDir.getAbsolutePath() + "/frame_%05d.png"
        );

        Process process = pb.start();
        int exitCode = process.waitFor();
        
        if (exitCode != 0) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.err.println(line);
            }
            throw new IOException("FFmpeg failed with exit code: " + exitCode);
        }
    }

    private void processFrames(File tempDir, double duration) throws IOException {
        // Initialize centroid finder components
        ColorDistanceFinder distanceFinder = new EuclideanColorDistance();
        ImageBinarizer binarizer = new DistanceImageBinarizer(distanceFinder, targetColor, threshold);
        BinaryGroupFinder groupFinder = new DfsBinaryGroupFinder();
        
        BinarizingImageGroupFinder centroidFinder = new BinarizingImageGroupFinder(binarizer, groupFinder);

        // Get all frame files
        File[] frameFiles = tempDir.listFiles((dir, name) -> name.startsWith("frame_") && name.endsWith(".png"));
        if (frameFiles == null || frameFiles.length == 0) {
            throw new IOException("No frames extracted");
        }

        // Sort frames by filename
        java.util.Arrays.sort(frameFiles);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputCsv))) {
            writer.write("time_seconds,x,y\n");

            double step = 1.0 / FRAME_RATE;
            int frameCount = 0;

            for (File frameFile : frameFiles) {
                double currentTime = frameCount * step;

                // Don't process frames beyond video duration
                if (currentTime > duration) {
                    break;
                }

                // Read frame as BufferedImage
                BufferedImage image = ImageIO.read(frameFile);
                if (image == null) {
                    System.err.println("Warning: Could not read frame " + frameFile.getName());
                    continue;
                }

                // Find centroids in this frame
                List<Group> groups = centroidFinder.findConnectedGroups(image);

                // Only process the largest group (biggest area)
                 if (!groups.isEmpty()) {
                    Group largest = null;
                    int maxSize = 0;

                    for (Group g : groups) {
                        int size = g.size();
                        if (size > maxSize) {
                            maxSize = size;
                            largest = g;
                        }
                    }

                    if (largest != null) {
                        Coordinate centroid = largest.centroid();
                        writer.write(String.format("%.3f,%d,%d%n",
                                currentTime,
                                centroid.x(),
                                centroid.y()));
                    }
                }

                frameCount++;
            }

            System.out.println("Processed " + frameCount + " frames -> " + outputCsv);
        }
    }
    private void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }
}
