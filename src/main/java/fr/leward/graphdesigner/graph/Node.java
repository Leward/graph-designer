package fr.leward.graphdesigner.graph;

import com.sun.javafx.collections.ObservableMapWrapper;
import fr.leward.graphdesigner.ui.SelectableItem;
import javafx.beans.*;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.util.*;

/**
 * Created by Paul-Julien on 01/02/2015.
 */
public class Node implements SelectableItem {

    private Long id;
    private Circle circle;
    private Collection<Relationship> outRelationships = new ArrayList<Relationship>();
    private Collection<Relationship> inRelationships = new ArrayList<Relationship>();

    /**
     * Labels applied to the node
     */
    private ObservableMap labels = new ObservableMapWrapper(new HashMap<String, Label>());

    /**
     * This boolean property (and observable) tells if further labels can be added to the node
     */
    private BooleanProperty canLabelBeAdded = new SimpleBooleanProperty(false);

    /**
     * The main label is the label that is actually rendered in the UI
     */
    private ObjectProperty<Label> mainLabelProperty = new SimpleObjectProperty<>();

    private boolean isSelected = false;
    private boolean isHovered = false;

    public Node() {
        mainLabelProperty.addListener(mainLabelChangedListener);
    }

    public Collection<Relationship> getOutRelationships() {
        return outRelationships;
    }

    public Collection<Relationship> getInRelationships() {
        return inRelationships;
    }

    public Collection<Relationship> getInAndOutRelationships() {
        Collection<Relationship> relationships = new ArrayList<>();
        relationships.addAll(getInRelationships());
        relationships.addAll(getOutRelationships());
        return relationships;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Circle getCircle() {
        return circle;
    }

    public void setCircle(Circle circle) {
        this.circle = circle;
    }

    public void addLabel(Label label) {
        labels.put(label.getLabel(), label);
        if(mainLabelProperty.getValue() == null) {
            setMainLabel(label);
        }
    }

    public void removeLabel(Label label) {

    }

    public boolean hasLabel(String label) {
        return labels.containsKey(label);
    }

    public ObservableMap<String, Label> getLabels() {
        return labels;
    }

    public List<Label> getLabelsAsList() {
        return new ArrayList<Label>(labels.values());
    }

    public Label getMainLabel() {
        return mainLabelProperty.getValue();
    }

    public void setMainLabel(Label mainLabel) {
        this.mainLabelProperty.setValue(mainLabel);
        refreshNodeColor();
    }

    public ObjectProperty<Label> mainLabelProperty() {
        return mainLabelProperty;
    }

    /**
     * TODO: Tmp method, to be update when multi labels will be handled
     * @param label
     */
    public void setLabel(Label label) {
        labels.clear();
        addLabel(label);
    }

    public void selectNode() {
        isSelected = true;
        updateStyle();
    }

    @Override
    public void select() {
        selectNode();
    }

    public void unselectNode() {
        isSelected = false;
        updateStyle();
    }

    @Override
    public void unselect() {
        unselectNode();
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

    /**
     * When the main label changes
     */
    private InvalidationListener mainLabelChangedListener = (observable) -> {
        circle.setFill(getMainLabel().getColor());
    };

    public void setHovered(boolean isHovered) {
        this.isHovered = isHovered;
        updateStyle();
    }

    public void refreshNodeColor() {
        Label label = getMainLabel();
        circle.setFill(label.getColor());
    }

    @Override
    public String toString() {
        return "[Node id="+id+"]";
    }
}
