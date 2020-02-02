package fr.leward.graphdesigner.ui.drawingpane;

import fr.leward.graphdesigner.core.IdGenerator;
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
    void testSomething() throws IOException {
        interact(() -> {
            var a = drawingPane.addNode(30, 30);
            var b = drawingPane.addNode(100, 60);
            var c = drawingPane.addNode(90, 160);

            drawingPane.addRelationship(a, b);
            drawingPane.addRelationship(b, c);

            snapshot = new Snapshot(scene, "drawing-pane-1");
        });
        // snapshot.saveSnapshot(); // Uncomment to save and register an expected change
        snapshot.saveDebugSnapshot();
        snapshot.assertSnapshotRemainsUnchanged();
    }


}
