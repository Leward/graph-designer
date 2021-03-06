package fr.leward.graphdesigner.math;

import fr.leward.graphdesigner.graph.Relationship;
import javafx.event.EventHandler;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.RotateEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;

public class Arrow {

    private static final Logger log = LoggerFactory.getLogger(Arrow.class);

    private Relationship relationship;
    private Line line;

    private Label typeLabel;
    private double typeLabelWidth;
    private double typeLabelHeight;

    private Path headPath;
    private MoveTo moveTo1;
    private LineTo lineHead1;
    private MoveTo moveTo2;
    private LineTo lineHead2;
    private LineTo lineHead3;

    private double arrowAngle = Math.toRadians(30);
    private double arrowLength = 10;

    private EventHandler<MouseEvent> onMouseClicked;

    public Arrow(Relationship relationship) {
        this.relationship = relationship;
        buildShapes();
    }

    public void buildShapes() {
        buildLine();
        buildHeadPath();
        buildTypeLabel();
    }

    public void buildTypeLabel() {
        Circle startNodeShape = relationship.getStartNode().getCircle();
        Circle endNodeShape = relationship.getEndNode().getCircle();

        if(typeLabel == null) {
            typeLabel = new Label(relationship.getRelationshipType().getName());
            typeLabel.setStyle("-fx-background-color: white; -fx-opacity: 1.0; ");
            Dimension2D typeLabelSize = calculateNodeDimensions(typeLabel);
            typeLabelWidth = typeLabelSize.getWidth();
            typeLabelHeight = typeLabelSize.getHeight();

            typeLabel.setOnRotate(new EventHandler<RotateEvent>() {
                @Override
                public void handle(RotateEvent event) {
                    log.debug("onRotate!");
                }
            });

            // Label is a element that constitute the arrow object and is clickable
            typeLabel.setOnMouseClicked(event -> {
                if(onMouseClicked != null) {
                    onMouseClicked.handle(event);
                }
            });
        }

        // Define the line that goes through the center of both nodes. The StraightLine object will calculate the equation of the line
        StraightLine straightLine = new StraightLine(startNodeShape.getCenterX(), startNodeShape.getCenterY(), endNodeShape.getCenterX(), endNodeShape.getCenterY());

        double lineAngle;
        if(straightLine.getA() == Double.POSITIVE_INFINITY) {
            lineAngle = Math.PI / 2;
        }
        else if(straightLine.getA() == Double.NEGATIVE_INFINITY) {
            lineAngle = -1 * Math.PI / 2;
        }
        else {
            lineAngle = Math.atan(straightLine.getA()); // atan of the elevation of the lines equals its angle
        }

        double direction = (startNodeShape.getCenterX() > endNodeShape.getCenterX()) ? -1 : 1;

        double distance = Math.sqrt(Math.pow(endNodeShape.getCenterX() - startNodeShape.getCenterX(), 2) + Math.pow(endNodeShape.getCenterY() - startNodeShape.getCenterY(), 2));
        double labelCenterX = direction * (distance / 2 * Math.cos(lineAngle)) + startNodeShape.getCenterX();
        double labelCenterY = direction * (distance / 2 * Math.sin(lineAngle)) + startNodeShape.getCenterY();

        typeLabel.setLayoutX(labelCenterX - (typeLabelWidth / 2));
        typeLabel.setLayoutY(labelCenterY - (typeLabelHeight / 2));
        typeLabel.setRotationAxis(new Point3D(labelCenterX, labelCenterY, 0));

        double pivotX = typeLabelWidth / 2;
        double pivotY = typeLabelHeight / 2;
        Rotate rotate = new Rotate(Math.toDegrees(lineAngle), pivotX, pivotY);
        typeLabel.getTransforms().clear();
        typeLabel.getTransforms().add(rotate);

        log.debug("Angle: " + Math.toDegrees(lineAngle));

    }

    /**
     * Build the Line shape of the arrow that links the two nodes
     */
    public void buildLine() {
        Circle startNodeShape = relationship.getStartNode().getCircle();
        Circle endNodeShape = relationship.getEndNode().getCircle();
        // Define the line that goes through the center of both nodes. The StraightLine object will calculate the equation of the line
        StraightLine straightLine = new StraightLine(startNodeShape.getCenterX(), startNodeShape.getCenterY(), endNodeShape.getCenterX(), endNodeShape.getCenterY());

        // Calculate the angle of the straight line.
        // Be careful, if the line is vertical, then the a in y = ax + b is infinite.
        double lineAngle;
        if(straightLine.getA() == Double.POSITIVE_INFINITY) {
            lineAngle = Math.PI / 2;
        }
        else if(straightLine.getA() == Double.NEGATIVE_INFINITY) {
            lineAngle = -1 * Math.PI / 2;
        }
        else {
            lineAngle = Math.atan(straightLine.getA()); // atan of the elevation of the lines equals its angle
        }

        if(line == null) {
            line = new Line();
            // Line is a element that constitute the arrow object and is clickable
            line.setOnMouseClicked(event -> {
                if(onMouseClicked != null) {
                    onMouseClicked.handle(event);
                }
            });
        }

        if(straightLine.getA() == Double.POSITIVE_INFINITY) {
            line.setStartX(startNodeShape.getCenterX());
            line.setStartY(startNodeShape.getCenterY() + startNodeShape.getRadius());
            line.setEndX(endNodeShape.getCenterX());
            line.setEndY(endNodeShape.getCenterY() - endNodeShape.getRadius());
        }
        else if(straightLine.getA() == Double.NEGATIVE_INFINITY) {
            line.setStartX(startNodeShape.getCenterX());
            line.setStartY(startNodeShape.getCenterY() - startNodeShape.getRadius());
            line.setEndX(endNodeShape.getCenterX());
            line.setEndY(endNodeShape.getCenterY() + endNodeShape.getRadius());
        }
        else {
            double direction = (startNodeShape.getCenterX() > endNodeShape.getCenterX()) ? -1 : 1;
            line.setStartX(direction * (startNodeShape.getRadius() * Math.cos(lineAngle)) + startNodeShape.getCenterX());
            line.setStartY(straightLine.calculateY(line.getStartX()));
            line.setEndX(-1 * direction * (endNodeShape.getRadius() * Math.cos(lineAngle)) + endNodeShape.getCenterX());
            line.setEndY(straightLine.calculateY(line.getEndX()));
        }
    }

    /**
     * Build the head of the arrow
     */
    public void buildHeadPath() {
        // Head Path
        if(headPath == null) {
            headPath = new Path();
            headPath.setFill(Color.BLACK);
            headPath.setFillRule(FillRule.EVEN_ODD);
            // Head Path is a element that constitute the arrow object and is clickable
            headPath.setOnMouseClicked(event -> {
                if(onMouseClicked != null) {
                    onMouseClicked.handle(event);
                }
            });
        }

        // Move To 1
        if(moveTo1 == null) {
            moveTo1 = new MoveTo();
        }
        moveTo1.setX(line.getEndX());
        moveTo1.setY(line.getEndY());
        if(!headPath.getElements().contains(moveTo1)) {
            headPath.getElements().add(moveTo1);
        }

        // Calculate angle
        double dx = line.getEndX() - line.getStartX();
        double dy = line.getEndY() - line.getStartY();
        double tetha = Math.atan2(dy, dx);
        double rho;

        // Line Head 1
        rho = tetha + arrowAngle;
        if(lineHead1 == null) {
            lineHead1 = new LineTo();
        }
        lineHead1.setX(line.getEndX() - arrowLength * Math.cos(rho));
        lineHead1.setY(line.getEndY() - arrowLength * Math.sin(rho));
        if(!headPath.getElements().contains(lineHead1)) {
            headPath.getElements().add(lineHead1);
        }

        // Move To 2
        if(moveTo2 == null) {
            moveTo2 = new MoveTo();
        }
        moveTo2.setX(moveTo1.getX());
        moveTo2.setY(moveTo1.getY());
        if(!headPath.getElements().contains(moveTo2)) {
            headPath.getElements().add(moveTo2);
        }

        // Line Head 2
        rho = tetha - arrowAngle;
        if(lineHead2 == null) {
            lineHead2 = new LineTo();
        }
        lineHead2.setX(line.getEndX() - arrowLength * Math.cos(rho));
        lineHead2.setY(line.getEndY() - arrowLength * Math.sin(rho));
        if(!headPath.getElements().contains(lineHead2)) {
            headPath.getElements().add(lineHead2);
        }

        // Line Head 3
        if(lineHead3 == null) {
            lineHead3 = new LineTo();
        }
        lineHead3.setX(lineHead1.getX());
        lineHead3.setY(lineHead1.getY());
        if(!headPath.getElements().contains(lineHead3)) {
            headPath.getElements().add(lineHead3);
        }
    }

    public Dimension2D calculateNodeDimensions(Control control) {
        Stage stage = new Stage();
        Group root = new Group();
        root.getChildren().add(control);
        stage.setScene(new Scene(root));
        stage.show();
        stage.close();

        return new Dimension2D(control.getWidth(), control.getHeight());
    }

    /**
     * Get all the graphic elements used to render the relationship
     * @return
     */
    public Collection<Node> getDrawableNodes() {
        Collection<javafx.scene.Node> drawableNodes = new ArrayList<>();
        drawableNodes.add(line);
        drawableNodes.add(headPath);
        drawableNodes.add(typeLabel);
        return drawableNodes;
    }

    public Relationship getRelationship() {
        return relationship;
    }

    public Line getLine() {
        return line;
    }

    public Path getHeadPath() {
        return headPath;
    }

    public Label getTypeLabel() {
        return typeLabel;
    }

    public EventHandler<MouseEvent> getOnMouseClicked() {
        return onMouseClicked;
    }

    public void setOnMouseClicked(EventHandler<MouseEvent> onMouseClicked) {
        this.onMouseClicked = onMouseClicked;
    }
}
