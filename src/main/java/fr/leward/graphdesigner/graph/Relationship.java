package fr.leward.graphdesigner.graph;

import fr.leward.graphdesigner.math.Arrow;
import javafx.scene.shape.Line;

/**
 * Created by Paul-Julien on 01/02/2015.
 */
public class Relationship {

    private Node startNode;
    private Node endNode;
    private Arrow arrow;

    public Relationship(Node startNode, Node endNode) {
        this.startNode = startNode;
        this.endNode = endNode;
    }

    public void updateLine() {
        if(arrow != null) {
            arrow.buildShapes();
        }
    }

    public Node getStartNode() {
        return startNode;
    }

    public Node getEndNode() {
        return endNode;
    }

    public Arrow getArrow() {
        return arrow;
    }

    public void setArrow(Arrow arrow) {
        this.arrow = arrow;
    }
}
