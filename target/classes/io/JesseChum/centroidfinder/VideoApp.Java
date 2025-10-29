package io.JesseChum.centroidfinder;

public class VideoApp {
    public static void main(String[] args){
        VideoProcessor processor = new VideoProcessor("src/main/resources/sample-1.mp4", "output.csv", "#00ff00", 30);
        processor.process();
    }
}