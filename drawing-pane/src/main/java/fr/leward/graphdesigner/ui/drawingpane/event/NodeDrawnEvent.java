package fr.leward.graphdesigner.ui.drawingpane.event;

import fr.leward.graphdesigner.core.IdGenerator;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.input.MouseEvent;

/**
 * A Node has been drawn by the user on the DrawingPane.
 */
public class NodeDrawnEvent extends Event {

    static final EventType<MouseEvent> EVENT_TYPE = new EventType<MouseEvent>(Event.ANY, "NODE_DRAWN");

    public final long id;

    public NodeDrawnEvent(long id) {
        super(EVENT_TYPE);
        this.id = id;
    }
}
