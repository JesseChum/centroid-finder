package io.JesseChum.centroidfinder;

import java.io.File;
import java.io.IOException;

public class SimpleThumbnailGenerator {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: SimpleThumbnailGenerator <videoFile>");
            System.exit(1);
        }
        
        String videoPath = args[0];
        File videoFile = new File(videoPath);
        
        if (!videoFile.exists()) {
            System.err.println("Video not found: " + videoPath);
            System.exit(1);
        }
        
        try {
            // Create output directory
            File outputDir = new File("output");
            if (!outputDir.exists()) {
                outputDir.mkdir();
            }
            
            // Extract video name without extension
            String videoFileName = videoFile.getName();
            String videoNameWithoutExt = videoFileName.substring(0, videoFileName.lastIndexOf('.'));
            
            File outputFile = new File(outputDir, videoNameWithoutExt + ".png");
            
            // Use ffmpeg to extract first frame
            // -i: input file
            // -ss 00:00:01: seek to 1 second (to avoid black frames at start)
            // -vframes 1: extract only 1 frame
            // -q:v 2: high quality
            ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg",
                "-i", videoPath,
                "-ss", "00:00:01",
                "-vframes", "1",
                "-q:v", "2",
                "-y", // overwrite output file
                outputFile.getAbsolutePath()
            );
            
            pb.redirectErrorStream(true);
            Process process = pb.start();
            
            int exitCode = process.waitFor();
            
            if (exitCode == 0 && outputFile.exists()) {
                System.out.println("Thumbnail saved at: " + outputFile.getAbsolutePath());
            } else {
                System.err.println("Failed to generate thumbnail. Exit code: " + exitCode);
                System.exit(1);
            }
            
        } catch (IOException | InterruptedException e) {
            System.err.println("Error generating thumbnail: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
