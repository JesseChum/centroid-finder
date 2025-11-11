
package io.jessechum.centroidfinder;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;


// import javafx.scene.effect.Light.Point;

public class VideoFrameAnalyzer {
    private final BinarizingImageGroupFinder centroidFinder;

    public VideoFrameAnalyzer(ColorDistanceFinder distanceFinder, int targetColor, int threshold) {
        ImageBinarizer binarizer = new DistanceImageBinarizer(distanceFinder, targetColor, threshold);
        BinaryGroupFinder groupFinder = new DfsBinaryGroupFinder();
        this.centroidFinder = new BinarizingImageGroupFinder(binarizer, groupFinder);
    }

    /** Returns largest group's centroid if any, else empty. */
    public Optional<Coordinate> analyze(BufferedImage frame) {
        List<Group> groups = centroidFinder.findConnectedGroups(frame);
        if (groups.isEmpty()) return Optional.empty();
        return Optional.of(groups.get(0).centroid());
    }
}
