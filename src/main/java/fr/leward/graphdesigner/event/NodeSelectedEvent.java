package fr.leward.graphdesigner.event;

import fr.leward.graphdesigner.event.bus.Event;
import fr.leward.graphdesigner.graph.Node;

/**
 * Created by Paul-Julien on 18/02/2015.
 */
public class NodeSelectedEvent implements Event {

    private Node node;

    public NodeSelectedEvent(Node node) {
        this.node = node;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }
}
