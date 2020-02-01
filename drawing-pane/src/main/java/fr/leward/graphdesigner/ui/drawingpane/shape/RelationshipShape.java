package fr.leward.graphdesigner.ui.drawingpane.shape;

import javafx.scene.Node;
import javafx.scene.shape.Line;

import java.util.Collection;
import java.util.Collections;

public class RelationshipShape {

    public final long id;

    private final NodeShape start;
    private final NodeShape end;

    private final Line line;

    public RelationshipShape(long id, NodeShape start, NodeShape end) {
        this.id = id;
        this.start = start;
        this.end = end;
        line = new Line(start.getCenter().getX(), start.getCenter().getY(), end.getCenter().getX(), end.getCenter().getY());
    }

    public Collection<Node> getDrawables() {
        return Collections.singletonList(line);
    }
}
