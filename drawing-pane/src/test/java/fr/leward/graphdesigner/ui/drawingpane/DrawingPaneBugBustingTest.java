package fr.leward.graphdesigner.ui.drawingpane;

import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Some weird bugs get replicated here as test to prevent them from happening again.
 */
@ExtendWith(ApplicationExtension.class)
public class DrawingPaneBugBustingTest extends BaseDrawingPaneTest {
    @Start
    private void start(Stage stage) {
        super.start(stage, () -> {});
    }

    @Test
    public void testShouldNotCreateRelationship(FxRobot robot) {
        AtomicInteger nbRelationshipCreated = new AtomicInteger(0);
        drawingPane.setOnRelationshipDrawnHandler(event -> nbRelationshipCreated.incrementAndGet());

        AtomicInteger nbNodesCreated = new AtomicInteger(0);
        drawingPane.setOnNodeDrawnHandler(event -> nbNodesCreated.incrementAndGet());

        drawingPane.switchMode(DrawingPaneMode.ADD_NODE);
        clickAt(robot, 100, 100);
        clickAt(robot, 250, 250);

        drawingPane.leaveMode();
        clickAt(robot, 100, 100);
        clickAt(robot, 250, 250);

        Assertions.assertEquals(2, nbNodesCreated.get());
        Assertions.assertEquals(0, nbRelationshipCreated.get());
    }
}
