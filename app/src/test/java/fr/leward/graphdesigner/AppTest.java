package fr.leward.graphdesigner;

import fr.leward.graphdesigner.core.IdGenerator;
import fr.leward.graphdesigner.ui.drawingpane.DrawingPane;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
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

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
public class AppTest {

    private Scene scene;

    private MainController controller;

    @Start
    public void start(Stage stage) throws Exception {
        var mainApp = new MainApp();
        mainApp.start(stage);
        controller = mainApp.getController();
        scene = mainApp.getScene();
    }

    @Test
    public void testAddNodeByMenuButton(FxRobot robot) throws Exception {
        ToggleButton createNodeButton = robot.lookup("#createNodeButton").query();
        DrawingPane drawingPane = robot.lookup("#pane").query();
        assertEquals(0, controller.countNodes());

        robot.clickOn(createNodeButton);
        assertTrue(createNodeButton.isSelected());

        // Should add a Node
        clickAt(robot, 200, 300);
        assertEquals(1, controller.countNodes());

        robot.clickOn(createNodeButton);
        assertFalse(createNodeButton.isSelected());

        // Click on Drawing Pane
        clickAt(robot, 220, 350);
        assertEquals(1, controller.countNodes());
    }

    @Test
    public void testAddNodeByKeyboardShortcut(FxRobot robot) throws Exception {
        ToggleButton createNodeButton = robot.lookup("#createNodeButton").query();
        DrawingPane drawingPane = robot.lookup("#pane").query();
        assertEquals(0, controller.countNodes());

        robot.press(KeyCode.N).release(KeyCode.N);
        assertTrue(createNodeButton.isSelected());

        clickAt(robot, 200, 300);
        assertEquals(1, controller.countNodes());

        robot.press(KeyCode.ESCAPE).release(KeyCode.ESCAPE);
        assertFalse(createNodeButton.isSelected());

        // Click on Drawing Pane
        clickAt(robot, 220, 350);
        assertEquals(1, controller.countNodes());
    }

    @Test
    public void testAddRelationshipsByMenuButton(FxRobot robot) {
        ToggleButton createNodeButton = robot.lookup("#createNodeButton").query();
        ToggleButton createRelationshipButton = robot.lookup("#createRelationshipButton").query();
        DrawingPane drawingPane = robot.lookup("#pane").query();
        assertEquals(0, controller.countRelationships());

        // Draw two nodes
        robot.clickOn(createNodeButton);
        clickAt(robot, 200, 300);
        clickAt(robot, 220, 350);
        robot.clickOn(createNodeButton);
        assertEquals(2, controller.countNodes());

        // Draw the relationship
        robot.clickOn(createRelationshipButton);
        assertTrue(createRelationshipButton.isSelected());
        clickAt(robot, 200, 300);
        clickAt(robot, 220, 350);
        robot.clickOn(createRelationshipButton);
        assertFalse(createRelationshipButton.isSelected());

        assertEquals(1, controller.countRelationships());
    }

    @Test
    public void testAddRelationshipsByKeyboardShortcut(FxRobot robot) {
        ToggleButton createNodeButton = robot.lookup("#createNodeButton").query();
        ToggleButton createRelationshipButton = robot.lookup("#createRelationshipButton").query();
        DrawingPane drawingPane = robot.lookup("#pane").query();
        assertEquals(0, controller.countRelationships());

        // Draw two nodes
        robot.clickOn(createNodeButton);
        clickAt(robot, 200, 300);
        clickAt(robot, 220, 350);
        robot.clickOn(createNodeButton);
        assertEquals(2, controller.countNodes());

        // Draw the relationship
        robot.press(KeyCode.R).release(KeyCode.R);
        assertTrue(createRelationshipButton.isSelected());
        clickAt(robot, 200, 300);
        clickAt(robot, 220, 350);
        robot.press(KeyCode.ESCAPE).release(KeyCode.ESCAPE);
        assertFalse(createRelationshipButton.isSelected());

        assertEquals(1, controller.countRelationships());
    }

    /**
     * Triggers a click at the (x,y) coordinates where (0,0) is the top-left corner of the drawing pane.
     */
    protected FxRobotInterface clickAt(FxRobot robot, double x, double y) {
        var point = getPointAt(robot, x, y);
        return robot.clickOn(point);
    }

    protected Point2D getPointAt(FxRobot robot, double x, double y) {
        return robot.point(scene)
                .atPosition(Pos.TOP_LEFT)
                .atOffset(x, y)
                .query();
    }

}
