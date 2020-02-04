package fr.leward.graphdesigner.ui.drawingpane.shape;

import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.effect.Light;
import javafx.scene.shape.Circle;

import java.util.Collection;
import java.util.Collections;

public class NodeShape {

    public final long id;
    private final Circle circle;

    public NodeShape(long id, double x, double y, double radius) {
        this.id = id;
        circle = new Circle(x, y, radius);
        circle.setCursor(Cursor.HAND);
    }

    public NodeShape(long id, double x, double y) {
        this(id, x, y, 20);
    }

    public Collection<Node> getDrawables() {
        return Collections.singletonList(circle);
    }

    public Point2D getCenter() {
        return new Point2D(circle.getCenterX(), circle.getCenterY());
    }

    public double getRadius() {
        return circle.getRadius();
    }

    /**
     * Get the point on the circle pointing towards another point.
     */
    public Point2D getOuterPointTowards(Point2D point) {
        var center = getCenter();
        var distance = center.distance(point);
        return center.interpolate(point, getRadius() / distance);
    }
}
