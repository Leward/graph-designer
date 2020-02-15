package fr.leward.graphdesigner.ui.drawingpane.shape;

import fr.leward.graphdesigner.ui.drawingpane.event.NodeClickedEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;

public class NodeShape extends Circle {

    public final long id;

    private EventHandler<NodeClickedEvent> onNodeClicked;

    public NodeShape(long id, double x, double y, double radius) {
        super(x, y, radius);
        this.id = id;
        setCursor(Cursor.HAND);
        setOnMouseClicked(this::handleCircleClicked);
        getProperties().put("nodeId", id);
    }

    public NodeShape(long id, double x, double y) {
        this(id, x, y, 20);
    }

    public Point2D getCenter() {
        return new Point2D(getCenterX(), getCenterY());
    }

    /**
     * Get the point on the circle pointing towards another point.
     */
    public Point2D getOuterPointTowards(Point2D point) {
        var center = getCenter();
        var distance = center.distance(point);
        return center.interpolate(point, getRadius() / distance);
    }

    private void handleCircleClicked(MouseEvent event) {
        if(onNodeClicked != null) {
            onNodeClicked.handle(new NodeClickedEvent(id, event));
        }
    }

    public void setOnNodeClicked(EventHandler<NodeClickedEvent> onNodeClicked) {
        this.onNodeClicked = onNodeClicked;
    }
}
