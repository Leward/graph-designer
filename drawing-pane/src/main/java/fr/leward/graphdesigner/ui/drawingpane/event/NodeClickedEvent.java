package fr.leward.graphdesigner.ui.drawingpane.event;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.input.MouseEvent;

public class NodeClickedEvent extends Event {

    public static final EventType<MouseEvent> EVENT_TYPE = new EventType<>(Event.ANY, "NODE_CLICKED");

    public final long id;
    public final MouseEvent mouseEvent;

    public NodeClickedEvent(long id, MouseEvent mouseEvent) {
        super(mouseEvent.getSource(), mouseEvent.getTarget(), EVENT_TYPE);
        this.id = id;
        this.mouseEvent = mouseEvent;
    }
}
