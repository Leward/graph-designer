package fr.leward.graphdesigner.ui.drawingpane;

import fr.leward.graphdesigner.core.IdGenerator;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.testfx.api.FxRobot;
import org.testfx.api.FxRobotInterface;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class BaseDrawingPaneTest {
    protected Scene scene;

    protected AtomicInteger nextID = new AtomicInteger(0);
    protected IdGenerator generator;
    protected DrawingPane drawingPane;

    protected void start(Stage stage, Runnable initFunction) {
        nextID = new AtomicInteger(0);
        generator = nextID::incrementAndGet;
        drawingPane = new DrawingPane(generator);

       initFunction.run();

        scene = new Scene(new StackPane(drawingPane), 500, 500);
        stage.setScene(scene);
        stage.show();
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
