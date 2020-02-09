package fr.leward.graphdesigner.ui.drawingpane.shape;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.util.Collection;
import java.util.List;

public class RelationshipShape {

    public final long id;

    private final NodeShape start;
    private final NodeShape end;

    private final Arrow arrow;

    private final String type = "IS_A";
    private Label label;
    private final Dimension2D labelDimention;

    private final Circle debugEndAnchorPoint;

    public RelationshipShape(long id, NodeShape start, NodeShape end) {
        this.id = id;
        this.start = start;
        this.end = end;

        var startAnchorPoint = start.getOuterPointTowards(end.getCenter());
        var endAnchorPoint = end.getOuterPointTowards(start.getCenter());

        arrow = new Arrow(startAnchorPoint, endAnchorPoint);

        label = new Label(type);
        label.setStyle("-fx-background-color: white; -fx-opacity: 1.0; ");
        labelDimention = calculateNodeDimensions(label);

        // Angle computes the angle between TWO VECTORS both originating at (0, 0)
        // The following allows to get the angle between two points
        //  new Point2D(1, 0) is the horizontal vector of size one going to the right (the x axis)
        // see: https://stackoverflow.com/questions/30906542/how-is-the-point2d-angle-method-to-be-understood
        double angle = new Point2D(1, 0).angle(end.getCenter().subtract(start.getCenter()));
        double distance = start.getCenter().distance(end.getCenter());
        var midpoint = start.getCenter().midpoint(end.getCenter());
        label.setLayoutX(midpoint.getX() - (labelDimention.getWidth() / 2));
        label.setLayoutY(midpoint.getY() - (labelDimention.getHeight() / 2));
        label.setRotate(angle);

        debugEndAnchorPoint = new Circle(endAnchorPoint.getX(), endAnchorPoint.getY(), 5);
        debugEndAnchorPoint.setFill(Color.PURPLE);
    }

    public Collection<Node> getDrawables() {
        var list = arrow.getDrawables();
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
}
