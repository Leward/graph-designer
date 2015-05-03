package fr.leward.graphdesigner.graph;

import fr.leward.graphdesigner.math.Arrow;
import fr.leward.graphdesigner.ui.SelectableItem;
import org.apache.commons.lang.NotImplementedException;

public class Relationship implements SelectableItem {

    private Node startNode;
    private Node endNode;
    private RelationshipType relationshipType;
    private Arrow arrow;

    public Relationship(Node startNode, Node endNode, RelationshipType relationshipType) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.relationshipType = relationshipType;
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

    public RelationshipType getRelationshipType() {
        return relationshipType;
    }

    public Arrow getArrow() {
        return arrow;
    }

    public void setArrow(Arrow arrow) {
        this.arrow = arrow;
    }

    @Override
    public void select() {
        throw new NotImplementedException("Select relationship is not implemented yet");
    }

    @Override
    public void unselect() {
        // TODO: Implement relationship selection
        //throw new NotImplementedException("Unselect relationship is not implemented yet");
    }
}
