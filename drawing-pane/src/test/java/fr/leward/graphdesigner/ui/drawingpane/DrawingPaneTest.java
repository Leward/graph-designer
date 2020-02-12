package fr.leward.graphdesigner.ui.drawingpane;

import fr.leward.graphdesigner.core.IdGenerator;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.api.FxRobotInterface;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@ExtendWith(ApplicationExtension.class)
public class DrawingPaneTest {

    private Scene scene;

    private IdGenerator generator;
    private DrawingPane drawingPane;

    private long nodeA;
    private long nodeB;

    /**
     * Will be called with {@code @Before} semantics, i. e. before each test method.
     *
     * @param stage - Will be injected by the test runner.
     */
    @Start
    private void start(Stage stage) {
        AtomicInteger nextID = new AtomicInteger(0);
        generator = nextID::incrementAndGet;
        drawingPane = new DrawingPane(generator);

        nodeA = drawingPane.addNode(50, 50);
        nodeB = drawingPane.addNode(200, 200);

        scene = new Scene(new StackPane(drawingPane), 500, 500);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param robot - Will be injected by the test runner.
     */
    @Test
    public void testNodeClickedEvent(FxRobot robot) {
        var clickedNode = new AtomicLong(-1);
        drawingPane.setOnNodeClickedHandler(event -> {
            clickedNode.set(event.id);
        });
        this.clickAt(robot, 50, 50); // Node A is at position 50, 50
        Assertions.assertEquals(nodeA, clickedNode.get());
    }

    private FxRobotInterface clickAt(FxRobot robot, double x, double y) {
        var point = robot.point(scene)
                .atPosition(Pos.TOP_LEFT)
                .atOffset(x, y)
                .query();
        return robot.clickOn(point);
    }

//    @Test
//    public void testRelationshipClickedEvent() {
//      // TODO: Implement test
//    }

}
