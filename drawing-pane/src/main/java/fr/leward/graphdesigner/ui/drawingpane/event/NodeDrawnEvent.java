package fr.leward.graphdesigner.ui.drawingpane.event;

import fr.leward.graphdesigner.core.IdGenerator;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.input.MouseEvent;

/**
 * A Node has been drawn by the user on the DrawingPane.
 */
public class NodeDrawnEvent extends Event {

    /**
     * ID of the new node (generated with an {@link IdGenerator})
     */
    public final long id;

    public NodeDrawnEvent(IdGenerator idGenerator) {
        super(new EventType<MouseEvent>(Event.ANY, "NODE_DRAWN"));
        this.id = idGenerator.nextId();
    }
}
