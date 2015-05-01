package fr.leward.graphdesigner.event;

import fr.leward.graphdesigner.event.bus.Event;
import fr.leward.graphdesigner.graph.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;

/**
 * Created by Paul-Julien on 08/02/2015.
 */
public class NodeClickedEvent implements Event {

    private Node node;
    private MouseEvent mouseEvent;

    public NodeClickedEvent(Node node, MouseEvent mouseEvent) {
        this.node = node;
        this.mouseEvent = mouseEvent;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public MouseEvent getMouseEvent() {
        return mouseEvent;
    }

    public void setMouseEvent(MouseEvent mouseEvent) {
        this.mouseEvent = mouseEvent;
    }
}
