package fr.leward.graphdesigner.ui.drawingpane;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.fail;

public class Snapshot {

    private final Scene scene;
    private final String name;
    private final Image snapshot;

    public Snapshot(Scene scene, String name) {
        this.scene = scene;
        this.name = name;
        this.snapshot = scene.snapshot(null);
    }

    public void saveSnapshot() throws IOException {
        var bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);
        ImageIO.write(bufferedImage, "png", getSnapshotFilePath().toFile());
    }

    public void saveDebugSnapshot() throws IOException {
        var bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);
        ImageIO.write(bufferedImage, "png", getDebugSnapshotPath().toFile());
    }

    public void assertSnapshotRemainsUnchanged() {
        Image previousSnapshot = getSavedSnapshot();

        if (snapshot.getWidth() != previousSnapshot.getWidth() || snapshot.getHeight() != previousSnapshot.getHeight()) {
            fail("The two snapshots are not of the same size");
        }

        for (int x = 0; x < (int) snapshot.getWidth(); x++) {
            for (int y = 0; y < (int) snapshot.getHeight(); y++) {
                var a = snapshot.getPixelReader().getArgb(x, y);
                var b = previousSnapshot.getPixelReader().getArgb(x, y);
                if(a != b) {
                    fail("Current snapshot differs from save snapshot");
                }
            }
        }
    }

    private Image getSavedSnapshot() {
        return new Image(String.format("file:%s", getSnapshotFilePath().toAbsolutePath()));
    }

    private Path getSnapshotFilePath() {
        var fileName = String.format("snapshots/%s.png", name);
        return Paths.get(System.getProperty("user.dir"), fileName);
    }

    private Path getDebugSnapshotPath() {
        var fileName = String.format("snapshots/%s.debug.png", name);
        return Paths.get(System.getProperty("user.dir"), fileName);
    }
}
