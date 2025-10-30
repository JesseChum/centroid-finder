package io.JesseChum.centroidfinder;

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
}
