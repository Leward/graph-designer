package fr.leward.graphdesigner.math;

import fr.leward.graphdesigner.graph.Relationship;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

/**
 * Created by Paul-Julien on 26/02/2015.
 */
public class Arrow {

    private Relationship relationship;
    private Line line;

    private Path headPath;
    private MoveTo moveTo1;
    private LineTo lineHead1;
    private MoveTo moveTo2;
    private LineTo lineHead2;
    private LineTo lineHead3;

    private double arrowAngle = Math.toRadians(30);
    private double arrowLength = 10;

    public Arrow(Relationship relationship) {
        this.relationship = relationship;
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
        Circle startNodeShape = relationship.getStartNode().getCircle();
        Circle endNodeShape = relationship.getEndNode().getCircle();
        // Define the line that goes through the center of both nodes. The StraightLine object will calculate the equation of the line
        StraightLine straightLine = new StraightLine(startNodeShape.getCenterX(), startNodeShape.getCenterY(), endNodeShape.getCenterX(), endNodeShape.getCenterY());
        double lineAngle = Math.atan(straightLine.getA()); // atan of the elevation of the lines equals its angle

        if(line == null) {
            line = new Line();
        }
        double direction = (startNodeShape.getCenterX() > endNodeShape.getCenterX()) ? -1 : 1;
        line.setStartX(direction * (startNodeShape.getRadius() * Math.cos(lineAngle)) + startNodeShape.getCenterX());
        line.setStartY(straightLine.calculateY(line.getStartX()));
        line.setEndX(-1 * direction * (endNodeShape.getRadius() * Math.cos(lineAngle)) + endNodeShape.getCenterX());
        line.setEndY(straightLine.calculateY(line.getEndX()));
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

    public Relationship getRelationship() {
        return relationship;
    }

    public Line getLine() {
        return line;
    }

    public Path getHeadPath() {
        return headPath;
    }
}
