package fr.leward.graphdesigner.snaptests;

import fr.leward.graphdesigner.core.IdGenerator;
import fr.leward.graphdesigner.ui.drawingpane.DrawingPane;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class DrawingPaneSnapshotTest extends ApplicationTest {

    private DrawingPane drawingPane;
    private Scene scene;
    private Snapshot snapshot;

    /**
     * Will be called with {@code @Before} semantics, i. e. before each test method.
     */
    @Override
    public void start(Stage stage) throws Exception {
        AtomicInteger nextID = new AtomicInteger(0);
        IdGenerator generator = nextID::incrementAndGet;
        drawingPane = new DrawingPane(generator);

        scene = new Scene(drawingPane, 400, 400);
        stage.setScene(scene);
    }

    @Test
    void testSnapshot() throws IOException {
        interact(() -> {
            var a = drawingPane.addNode(30, 30);
            var b = drawingPane.addNode(150, 90);
            var c = drawingPane.addNode(135, 240);

            drawingPane.addRelationship(a, b);
            drawingPane.addRelationship(b, c);

            snapshot = new Snapshot(scene, "drawing-pane-1");
        });
//         snapshot.saveSnapshot(); // Uncomment to save and register an expected change
        snapshot.saveDebugSnapshot();
        snapshot.assertSnapshotRemainsUnchanged();
    }

    @Test
    void testSnapshotDifferentRelsDirections() throws IOException {
        interact(() -> {
            var a = drawingPane.addNode(50, 50);
            var b = drawingPane.addNode(250, 70);
            var c = drawingPane.addNode(240, 290);
            var d = drawingPane.addNode(60, 200);

            drawingPane.addRelationship(a, b);
            drawingPane.addRelationship(b, c);
            drawingPane.addRelationship(c, d);
            drawingPane.addRelationship(d, a);

            snapshot = new Snapshot(scene, "drawing-pane-2");
        });
//         snapshot.saveSnapshot(); // Uncomment to save and register an expected change
        snapshot.saveDebugSnapshot();
        snapshot.assertSnapshotRemainsUnchanged();
    }


}
