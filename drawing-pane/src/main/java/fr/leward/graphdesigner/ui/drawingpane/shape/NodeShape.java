package fr.leward.graphdesigner.ui.drawingpane.shape;

import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.shape.Circle;

import java.util.Collection;
import java.util.Collections;

public class NodeShape {

    public final long id;
    private final Circle circle;

    public NodeShape(long id, double x, double y) {
        this.id = id;
        circle = new Circle(x, y, 20);
        circle.setCursor(Cursor.HAND);
    }

    public Collection<Node> getDrawables() {
        return Collections.singletonList(circle);
    }

    public Point2D getCenter() {
        return new Point2D(circle.getCenterX(), circle.getCenterY());
    }
}
