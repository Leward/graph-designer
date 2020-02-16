package fr.leward.graphdesigner.ui.drawingpane;

import fr.leward.graphdesigner.core.IdGenerator;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.api.FxRobotInterface;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(ApplicationExtension.class)
public class DrawingPaneTest {

    private Scene scene;

    private AtomicInteger nextID = new AtomicInteger(0);
    private IdGenerator generator;
    private DrawingPane drawingPane;

    private long nodeA;
    private long nodeB;
    private long nodeC;

    private long relA;

    private final Point2D pointA = new Point2D(50, 50);
    private final Point2D pointB = new Point2D(200, 200);
    private final Point2D pointC = new Point2D(250, 50); // 100px to the right of Node A

    /**
     * Will be called with {@code @Before} semantics, i. e. before each test method.
     *
     * @param stage - Will be injected by the test runner.
     */
    @Start
    private void start(Stage stage) {
        nextID = new AtomicInteger(0);
        generator = nextID::incrementAndGet;
        drawingPane = new DrawingPane(generator);

        nodeA = drawingPane.addNode(pointA.getX(), pointA.getY());
        nodeB = drawingPane.addNode(pointB.getX(), pointB.getY());
        nodeC = drawingPane.addNode(pointC.getX(), pointC.getY());

        relA = drawingPane.addRelationship(nodeA, nodeC);

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
        assertEquals(nodeA, clickedNode.get());
    }

    @Test
    public void testRelationshipClickedEvent(FxRobot robot) {
        var clickedRelationship = new AtomicLong(-1);
        drawingPane.setOnRelationshipClickedHandler(event -> clickedRelationship.set(event.id));
        this.clickAt(robot, 150, 50); // in the middle of node A and node C
        assertEquals(relA, clickedRelationship.get());
    }

    @Test
    public void testClickToAddANode(FxRobot robot) {
        var expectedNextNodeId = nextID.get() + 1;
        var clickedNode = new AtomicLong(-1);

        // Click to add Node
        drawingPane.switchMode(DrawingPaneMode.ADD_NODE);
        clickAt(robot, 100, 100);
        drawingPane.leaveMode();

        // Click the select the new Node
        drawingPane.setOnNodeClickedHandler(event -> clickedNode.set(event.id));
        clickAt(robot, 100, 100);
        assertEquals(expectedNextNodeId, clickedNode.get());
    }

    @Test
    public void testClickToAddRelationship(FxRobot robot) {
        var expectedRelationshipId = nextID.get() + 1;
        var clickedRelationship = new AtomicLong(-1);

        // Click to add Relationship
        drawingPane.switchMode(DrawingPaneMode.ADD_RELATIONSHIP);
        clickAt(robot, pointA.getX(), pointA.getY());
        clickAt(robot, pointB.getX(), pointB.getY());

        // Click the selected relationship
        drawingPane.setOnRelationshipClickedHandler(event -> clickedRelationship.set(event.id));
        var midPoint = pointA.midpoint(pointB);
        clickAt(robot, midPoint.getX(), midPoint.getY());
        assertEquals(expectedRelationshipId, clickedRelationship.get());
    }

    @Test
    public void testMoveNode(FxRobot robot) {
        Point2D newPointB = new Point2D(300, 200);

        var dragStart = getPointAt(robot, pointB.getX(), pointB.getY());
        var dragEnd = getPointAt(robot, newPointB.getX(), newPointB.getY());
        robot.drag(dragStart, MouseButton.PRIMARY).dropTo(dragEnd);

        var clickedNode = new AtomicLong(-1);
        drawingPane.setOnNodeClickedHandler(event -> {
            clickedNode.set(event.id);
        });

        this.clickAt(robot, 300, 200);
        assertEquals(nodeB, clickedNode.get());
    }

    @Test
    public void testSelectASingleNode(FxRobot robot) {
        this.clickAt(robot, pointA.getX(), pointA.getY());
        assertTrue(drawingPane.isNodeSelected(nodeA));
    }

    @Test
    public void testSelectTwoNodes(FxRobot robot) {
        this.clickAt(robot, pointA.getX(), pointA.getY());
        robot.press(KeyCode.CONTROL);
        this.clickAt(robot, pointB.getX(), pointB.getY());
        robot.release(KeyCode.CONTROL);
        assertTrue(drawingPane.isNodeSelected(nodeA));
        assertTrue(drawingPane.isNodeSelected(nodeB));
    }

    /**
     * Triggers a click at the (x,y) coordinates where (0,0) is the top-left corner of the drawing pane.
     */
    private FxRobotInterface clickAt(FxRobot robot, double x, double y) {
        var point = getPointAt(robot, x, y);
        return robot.clickOn(point);
    }

    private Point2D getPointAt(FxRobot robot, double x, double y) {
        return robot.point(scene)
                .atPosition(Pos.TOP_LEFT)
                .atOffset(x, y)
                .query();
    }


}
