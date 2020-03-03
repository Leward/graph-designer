package fr.leward.graphdesigner.ui.drawingpane.shape;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.Collection;

public class RelationshipBeingDrawn extends Line {

    private final NodeShape start;

    public RelationshipBeingDrawn(NodeShape start) {
        this.start = start;
        setStartX(start.getCenterX());
        setStartY(start.getCenterY());
        setEndX(start.getCenterX());
        setEndY(start.getCenterY());
        setStroke(Color.HOTPINK);
//        toBack();
    }

    public void followMousePointer(double x, double y, Collection<NodeShape> nodeShapes) {
        var startAnchorPoint = start.getOuterPointTowards(new Point2D(x, y));
        setStartX(startAnchorPoint.getX());
        setStartY(startAnchorPoint.getY());
        nodeShapes.stream()
                .filter(nodeShape -> nodeShape.intersects(x, y, 1, 1))
                .findFirst()
                .ifPresentOrElse(this::followNodeShape, () -> followMousePointer(x, y));
    }

    private void followMousePointer(double x, double y) {
        this.setEndX(x);
        this.setEndY(y);
    }

    private void followNodeShape(NodeShape nodeShape) {
        var endAnchorPoint = nodeShape.getOuterPointTowards(start.getCenter());
        setEndX(endAnchorPoint.getX());
        setEndY(endAnchorPoint.getY());
    }
}
