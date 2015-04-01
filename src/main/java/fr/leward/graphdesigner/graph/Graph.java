package fr.leward.graphdesigner.graph;

import fr.leward.graphdesigner.event.GraphUpdatedEvent;
import fr.leward.graphdesigner.event.LabelAddedEvent;
import fr.leward.graphdesigner.event.bus.EventStreams;
import javafx.scene.paint.Color;

import java.util.*;

/**
 * Created by Paul-Julien on 01/02/2015.
 */
public class Graph {

    private Collection<Node> nodes = new ArrayList<Node>();
    private Collection<Relationship> relationships = new ArrayList<Relationship>();
    private Map<String, Label> labels = new HashMap<String, Label>();

    public Graph() {
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
}
