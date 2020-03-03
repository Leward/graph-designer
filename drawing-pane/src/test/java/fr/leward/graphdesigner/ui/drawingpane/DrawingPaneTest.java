package fr.leward.graphdesigner.ui.drawingpane;

import fr.leward.graphdesigner.core.IdGenerator;
import fr.leward.graphdesigner.ui.drawingpane.shape.NodeShape;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
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
public class DrawingPaneTest extends BaseDrawingPaneTest {

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
        super.start(stage, () -> {
            nodeA = drawingPane.addNode(pointA.getX(), pointA.getY());
            nodeB = drawingPane.addNode(pointB.getX(), pointB.getY());
            nodeC = drawingPane.addNode(pointC.getX(), pointC.getY());
            relA = drawingPane.addRelationship(nodeA, nodeC);
        });
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
    public void testMoveNode(FxRobot robot) {
        Point2D newPointB = new Point2D(300, 200);

        var dragStart = getPointAt(robot, pointB.getX(), pointB.getY());
        var dragEnd = getPointAt(robot, newPointB.getX(), newPointB.getY());
        robot.drag(dragStart, MouseButton.PRIMARY).dropTo(dragEnd);

        var clickedNode = new AtomicLong(-1);
        drawingPane.setOnNodeClickedHandler(event -> clickedNode.set(event.id));

        this.clickAt(robot, 300, 200);
        assertEquals(nodeB, clickedNode.get());
    }

    @Test
    public void testMoveNodeWithRelationship(FxRobot robot) {
        Point2D newPointC = new Point2D(250, 150);

        var dragStart = getPointAt(robot, pointC.getX(), pointC.getY());
        var dragEnd = getPointAt(robot, newPointC.getX(), newPointC.getY());
        robot.drag(dragStart, MouseButton.PRIMARY).dropTo(dragEnd);

        var clickedRelationship = new AtomicLong(-1);
        drawingPane.setOnRelationshipClickedHandler(event -> clickedRelationship.set(event.id));
        var midPoint = pointA.midpoint(newPointC);
        clickAt(robot, midPoint.getX(), midPoint.getY());
        assertEquals(relA, clickedRelationship.get());

    }

    @Test
    public void testSelectASingleNode(FxRobot robot) {
        this.clickAt(robot, pointA.getX(), pointA.getY());
        assertTrue(drawingPane.isSelected(nodeA));

        // Node A has the .selected class
        var node = robot
                .lookup(n -> n instanceof NodeShape && ((NodeShape) n).id == nodeA)
                .query();
        assertTrue(node.getStyleClass().contains("selected"));
    }

    @Test
    public void testSelectTwoNodes(FxRobot robot) {
        this.clickAt(robot, pointA.getX(), pointA.getY());
        robot.press(KeyCode.CONTROL);
        this.clickAt(robot, pointB.getX(), pointB.getY());
        robot.release(KeyCode.CONTROL);
        assertTrue(drawingPane.isSelected(nodeA));
        assertTrue(drawingPane.isSelected(nodeB));
    }

    @Test
    public void testMoveTwoNodesTogether(FxRobot robot) {
        robot.press(KeyCode.CONTROL); // Use control to select and move several nodes
        this.clickAt(robot, pointB.getX(), pointB.getY());

        double moveX = 50;
        double moveY = 70;
        var dragStart = getPointAt(robot, pointA.getX(), pointA.getY());
        var dragEnd = getPointAt(robot, pointA.getX() + moveX, pointA.getY() + moveY);
        robot.drag(dragStart, MouseButton.PRIMARY).dropTo(dragEnd); // Control has to be pressed during drag

        var clickedNode = new AtomicLong(-1);
        drawingPane.setOnNodeClickedHandler(event -> {
            clickedNode.set(event.id);
        });
        this.clickAt(robot, pointA.getX() + moveX, pointA.getY() + moveY); // New Position of A
        assertEquals(nodeA, clickedNode.get());
        clickedNode.set(-1); // reset
        this.clickAt(robot, pointB.getX() + moveX, pointB.getY() + moveY); // New Position of B, should have been dragged alongside A
        robot.release(KeyCode.CONTROL);
        assertEquals(nodeB, clickedNode.get());
    }

}
