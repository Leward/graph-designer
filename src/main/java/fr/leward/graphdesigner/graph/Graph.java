package fr.leward.graphdesigner.graph;

import fr.leward.graphdesigner.event.GraphUpdatedEvent;
import fr.leward.graphdesigner.event.LabelAddedEvent;
import fr.leward.graphdesigner.event.bus.EventStreams;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

import java.util.*;

/**
 * Created by Paul-Julien on 01/02/2015.
 */
public class Graph {

    private long lastIdUsed = 0;
    private Collection<Node> nodes = new ArrayList<Node>();
    private Collection<Relationship> relationships = new ArrayList<Relationship>();
    private Map<String, Label> labels = new HashMap<String, Label>();
    private ObservableList<RelationshipType> relationshipTypes = FXCollections.observableArrayList();

    public Graph() {
        Label userLabel = new Label("User");
        userLabel.setColor(Color.DARKGREEN);
        addLabel(userLabel);

        relationshipTypes.add(new RelationshipType("FILESPACE"));
        relationshipTypes.add(new RelationshipType("CONTAINS"));
    }

    private void notifyGraphUpdated() {
        EventStreams.graphUpdatedEventStream.publish(new GraphUpdatedEvent(this));
    }

    public void addNode(Node node) {
        nodes.add(node);
        notifyGraphUpdated();
    }

    public void removeNode(Node node) {
        nodes.remove(node);
        notifyGraphUpdated();
    }

    public Collection<Node> getNodes() {
        return nodes;
    }

    public void addRelationship(Relationship relationship) {
        relationships.add(relationship);
        notifyGraphUpdated();
    }

    public void removeRelationship(Relationship relationship) {
        relationships.remove(relationship);
        notifyGraphUpdated();
    }

    public Collection<Relationship> getRelationships() {
        return relationships;
    }

    public List<Label> getLabels() {
        return new ArrayList<Label>(labels.values());
    }

    public Label getLabel(String label) {
        return labels.get(label);
    }

    public int countLabels() {
        return labels.size();
    }

    public void addLabel(Label label) {
        if(!labels.containsKey(label.getLabel())) {
            labels.put(label.getLabel(), label);
            notifyGraphUpdated();
            EventStreams.labelAddedEventStream.publish(new LabelAddedEvent(label));
        }
    }

    public Color pickUnusedColorForLabel() {
        List<Color> availableColors = new ArrayList<Color>(Arrays.asList(Label.colors));
        for(Label label : labels.values()) {
            if(availableColors.contains(label.getColor())) {
                availableColors.remove(label.getColor());
            }
        }

        if(availableColors.size() > 1) {
            Random random = new Random();
            int index = random.nextInt(availableColors.size() - 1);
            return availableColors.get(index);
        }
        else {
            return Label.pickRandomColor();
        }
    }

    public void refreshNodeColorForLabel(Label label) {
        for(Node node : nodes) {
            if(node.hasLabel(label.getLabel())) {
                node.refreshNodeColor();
            }
        }
    }

    /**
     * Generate a new ID for the graph objects
     * @return the generated ID
     */
    public Long generateId() {
        return ++lastIdUsed;
    }

    /**
     * Returns true if the graph already contains a given relationship type
     * defined as a String
     * @param type The name of the relationship type
     * @return whether the relationship type already exists or not
     */
    public boolean containsRelationshipType(String type) {
        for(RelationshipType relationshipType : relationshipTypes) {
            if(relationshipType.getName().equals(type)) {
                return true;
            }
        }
        return false;
    }

    public RelationshipType getRelationshipTypeByName(String name) {
        for(RelationshipType relationshipType : relationshipTypes) {
            if(relationshipType.getName().equals(name)) {
                return relationshipType;
            }
        }
        return null;
    }

    /**
     * Add a relationship type. If the object is already in the list it is ignored.
     * @param relationshipType
     */
    public void addRelationshipType(RelationshipType relationshipType) {
        if(!relationshipTypes.contains(relationshipType) && containsRelationshipType(relationshipType.getName())) {
            throw new IllegalArgumentException("Duplicate of relationship type used. ");
        }
        if(!relationshipTypes.contains(relationshipType)) {
            relationshipTypes.add(relationshipType);
        }
    }

    public ObservableList<RelationshipType> getRelationshipTypes() {
        return relationshipTypes;
    }
}
