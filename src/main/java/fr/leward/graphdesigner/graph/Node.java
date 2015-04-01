package fr.leward.graphdesigner.graph;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Paul-Julien on 01/02/2015.
 */
public class Node {

    private Circle circle;
    private Collection<Relationship> outRelationships = new ArrayList<Relationship>();
    private Collection<Relationship> inRelationships = new ArrayList<Relationship>();
    private Map<String, Label> labels = new HashMap<String, Label>();

    private boolean isSelected = false;
    private boolean isHovered = false;

    public Node() {
    }

    public Collection<Relationship> getOutRelationships() {
        return outRelationships;
    }

    public Collection<Relationship> getInRelationships() {
        return inRelationships;
    }

    public Circle getCircle() {
        return circle;
    }

    public void setCircle(Circle circle) {
        this.circle = circle;
    }

    public void addLabel(Label label) {
        labels.put(label.getLabel(), label);
    }

    public void removeLabel(Label label) {

    }

    public boolean hasLabel(String label) {
        return labels.containsKey(label);
    }

    /**
     * TODO: Tmp method, to be update when multi labels will be handled
     * @return
     */
    public Label getLabel() {
        if(labels.size() == 0) {
            return null;
        }
        // Return first item
        return labels.entrySet().iterator().next().getValue();
    }

    /**
     * TODO: Tmp method, to be update when multi labels will be handled
     * @param label
     */
    public void setLabel(Label label) {
        labels.clear();
        addLabel(label);
        circle.setFill(label.getColor());
    }

    public void selectNode() {
        isSelected = true;
        updateStyle();
    }

    public void unselectNode() {
        isSelected = false;
        updateStyle();
    }

    public void updateStyle() {
        StringBuilder sb = new StringBuilder();
        if(isHovered) {
            sb.append("-fx-opacity: 0.8; ");
        }
        else {
            sb.append("-fx-opacity: 1.0; ");
        }
        if(isSelected) {
            sb.append("-fx-stroke-width: 2px; -fx-stroke: yellow; ");
        }
        else {
            sb.append("-fx-stroke-width: 0px; -fx-stroke: yellow; ");
        }
        circle.setStyle(sb.toString());
    }

    public void setHovered(boolean isHovered) {
        this.isHovered = isHovered;
        updateStyle();
    }

    public void refreshNodeColor() {
        Label label = getLabel(); // TODO: Temporary
        circle.setFill(label.getColor());
    }
}
