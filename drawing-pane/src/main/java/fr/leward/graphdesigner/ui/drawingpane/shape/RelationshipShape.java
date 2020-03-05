package fr.leward.graphdesigner.ui.drawingpane.shape;

import fr.leward.graphdesigner.ui.drawingpane.event.RelationshipClickedEvent;
import javafx.event.EventHandler;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collection;

public class RelationshipShape {

    public final long id;

    private final NodeShape start;
    private final NodeShape end;

    private final Arrow arrow;

    private final String type = "IS_A"; // TODO: Allow to customize the relationship type
    private Label label;
    private final Dimension2D labelDimention;

    private EventHandler<RelationshipClickedEvent> onRelationshipClicked;

    public RelationshipShape(long id, NodeShape start, NodeShape end) {
        this.id = id;
        this.start = start;
        this.end = end;

        arrow = new Arrow();

        label = new Label(type);
        label.setStyle("-fx-background-color: white; -fx-opacity: 1.0; ");
        labelDimention = calculateNodeDimensions(label);

        update();

        // Attach Mouse Click events
        label.setOnMouseClicked(this::handleMouseClick);
        arrow.setOnMouseClicked(this::handleMouseClick);

        start.centerXProperty().addListener(observable -> update());
        start.centerYProperty().addListener(observable -> update());
        end.centerXProperty().addListener(observable -> update());
        end.centerYProperty().addListener(observable -> update());
    }

    /**
     * Angle computes the angle between TWO VECTORS both originating at (0, 0)
     * The following allows to get the angle between two points
     * new Point2D(1, 0) is the horizontal vector of size one going to the right (the x axis)
     * see: https://stackoverflow.com/questions/30906542/how-is-the-point2d-angle-method-to-be-understood
     *
     * @param a start point of the relationship (center point of Node A)
     * @param b end point of the relationship (center point of Node B)
     * @return the angle (in degrees) between a and b to set the {@link Label} rotation to
     */
    public static double calculateLabelAngle(Point2D a, Point2D b) {
        Point2D diff = b.subtract(a);

        double angle;
        if (diff.getX() < 0 && diff.getY() <= 0) {
            angle = new Point2D(-1, 0).angle(diff);
        } else {
            angle = new Point2D(1, 0).angle(diff);
        }

        return Math.round(angle * 100) / 100.0;
    }

    public void update() {
        var startAnchorPoint = start.getOuterPointTowards(end.getCenter());
        var endAnchorPoint = end.getOuterPointTowards(start.getCenter());
        arrow.update(startAnchorPoint, endAnchorPoint);

        double angle = calculateLabelAngle(start.getCenter(), end.getCenter());
        var midpoint = start.getCenter().midpoint(end.getCenter());
        label.setLayoutX(midpoint.getX() - (labelDimention.getWidth() / 2));
        label.setLayoutY(midpoint.getY() - (labelDimention.getHeight() / 2));
        label.setRotate(angle);

    }

    public Collection<Node> getDrawables() {
        var list = new ArrayList<Node>();
        list.add(arrow);
        list.add(label);
        return list;
    }

    private Dimension2D calculateNodeDimensions(Control control) {
        Stage stage = new Stage();
        Group root = new Group();
        root.getChildren().add(control);
        stage.setScene(new Scene(root));
        stage.show();
        stage.close();

        return new Dimension2D(control.getWidth(), control.getHeight());
    }

    public void handleMouseClick(MouseEvent event) {
        if (onRelationshipClicked != null) {
            onRelationshipClicked.handle(new RelationshipClickedEvent(id, event));
        }
    }

    public void setOnRelationshipClicked(EventHandler<RelationshipClickedEvent> onRelationshipClicked) {
        this.onRelationshipClicked = onRelationshipClicked;
    }

    public boolean hasNode(long id) {
        return start.id == id || end.id == id;
    }

    /**
     * Package visible for testing.
     */
    double getLabelAngle() {
        return label.getRotate();
    }
}
