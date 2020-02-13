package fr.leward.graphdesigner.ui.drawingpane.shape;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Arrow extends Group {

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

    public Arrow(Point2D from, Point2D to) {
        this.from = from;
        this.to = to;

        line = new Line();

        headPath = new Path();
        headPath.setFill(Color.BLACK);
        headPath.setFillRule(FillRule.EVEN_ODD);

        moveTo1 = new MoveTo();
        lineHead1 = new LineTo();
        moveTo2 = new MoveTo();
        lineHead2 = new LineTo();
        lineHead3 = new LineTo();
        headPath.getElements().addAll(moveTo1, lineHead1, moveTo2, lineHead2, lineHead3);

        buildShapes();

        getChildren().addAll(line, headPath);
    }

    public void buildShapes() {
        buildLine();
        buildHeadPath();
    }

    /**
     * Build the Line shape of the arrow that links the two nodes
     */
    public void buildLine() {
        line.setStartX(from.getX());
        line.setStartY(from.getY());
        line.setEndX(to.getX());
        line.setEndY(to.getY());
    }

    /**
     * Build the head of the arrow
     */
    public void buildHeadPath() {
        // Move To 1
        moveTo1.setX(line.getEndX());
        moveTo1.setY(line.getEndY());

        // Calculate angle
        double dx = line.getEndX() - line.getStartX();
        double dy = line.getEndY() - line.getStartY();
        double tetha = Math.atan2(dy, dx);
        double rho;

        // Line Head 1
        rho = tetha + arrowAngle;
        lineHead1.setX(line.getEndX() - headLength * Math.cos(rho));
        lineHead1.setY(line.getEndY() - headLength * Math.sin(rho));

        // Move To 2
        moveTo2.setX(moveTo1.getX());
        moveTo2.setY(moveTo1.getY());

        // Line Head 2
        rho = tetha - arrowAngle;
        lineHead2.setX(line.getEndX() - headLength * Math.cos(rho));
        lineHead2.setY(line.getEndY() - headLength * Math.sin(rho));

        // Line Head 3
        lineHead3.setX(lineHead1.getX());
        lineHead3.setY(lineHead1.getY());
    }

}
