package fr.leward.graphdesigner.event;

import fr.leward.graphdesigner.event.bus.Event;
import javafx.scene.input.MouseEvent;

/**
 * Created by Paul-Julien on 12/02/2015.
 */
public class GraphPaneMouseMovedEvent implements Event {

    private MouseEvent mouseEvent;

    public GraphPaneMouseMovedEvent(MouseEvent mouseEvent) {
        this.mouseEvent = mouseEvent;
    }

    public MouseEvent getMouseEvent() {
        return mouseEvent;
    }

    public void setMouseEvent(MouseEvent mouseEvent) {
        this.mouseEvent = mouseEvent;
    }
}
