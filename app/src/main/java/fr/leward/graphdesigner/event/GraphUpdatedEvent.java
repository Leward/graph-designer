package fr.leward.graphdesigner.event;

import fr.leward.graphdesigner.event.bus.Event;
import fr.leward.graphdesigner.graph.Graph;

/**
 * Created by Paul-Julien on 15/02/2015.
 */
public class GraphUpdatedEvent implements Event {

    private Graph graph;

    public GraphUpdatedEvent(Graph graph) {
        this.graph = graph;
    }

    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }
}
