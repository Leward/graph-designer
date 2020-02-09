package fr.leward.graphdesigner.ui.drawingpane.shape;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;

public class Arrow {

    private static final Logger log = LoggerFactory.getLogger(Arrow.class);

    private Point2D from;
    private Point2D to;

    private Line line;

    private Path headPath;
    private MoveTo moveTo1;
    private LineTo lineHead1;
    private MoveTo moveTo2;
    private LineTo lineHead2;
    private LineTo lineHead3;

    private double arrowAngle = Math.toRadians(30);

    /**
     * The length of the arrow head
     */
    private double headLength = 10;

    private EventHandler<MouseEvent> onMouseClicked;

    public Arrow(Point2D from, Point2D to) {
        this.from = from;
        this.to = to;
        buildShapes();
    }

    public void buildShapes() {
        buildLine();
        buildHeadPath();
    }

    /**
     * Build the Line shape of the arrow that links the two nodes
     */
    public void buildLine() {
        if(line == null) {
            line = new Line();
            line.setOnMouseClicked(this::handleMouseClick);
        }
        line.setStartX(from.getX());
        line.setStartY(from.getY());
        line.setEndX(to.getX());
        line.setEndY(to.getY());
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
            headPath.setOnMouseClicked(this::handleMouseClick);
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
        lineHead1.setX(line.getEndX() - headLength * Math.cos(rho));
        lineHead1.setY(line.getEndY() - headLength * Math.sin(rho));
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
        lineHead2.setX(line.getEndX() - headLength * Math.cos(rho));
        lineHead2.setY(line.getEndY() - headLength * Math.sin(rho));
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

    public Collection<Node> getDrawables() {
        Collection<Node> drawableNodes = new ArrayList<>();
        drawableNodes.add(line);
        drawableNodes.add(headPath);
        return drawableNodes;
    }

    private void handleMouseClick(MouseEvent event) {
        if(onMouseClicked != null) {
            onMouseClicked.handle(event);
        }
    }

    public void setOnMouseClicked(EventHandler<MouseEvent> onMouseClicked) {
        this.onMouseClicked = onMouseClicked;
    }
}
