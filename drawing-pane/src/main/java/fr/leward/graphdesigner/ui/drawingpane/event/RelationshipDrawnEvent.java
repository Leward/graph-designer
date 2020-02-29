package fr.leward.graphdesigner.ui.drawingpane.event;

import fr.leward.graphdesigner.core.IdGenerator;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.input.MouseEvent;

/**
 * A Relationship has been drawn by the user on the DrawingPane.
 */
public class RelationshipDrawnEvent extends Event {

    public static final EventType<MouseEvent> EVENT_TYPE = new EventType<MouseEvent>(Event.ANY, "RELATIONSHIP_DRAWN");

    public final long id;
    public final String type;
    public final long startNodeId;
    public final long endNodeId;

    public RelationshipDrawnEvent(long id, String type, long startNodeId, long endNodeId) {
        super(EVENT_TYPE);
        this.id = id;
        this.type = type;
        this.startNodeId = startNodeId;
        this.endNodeId = endNodeId;
    }
}
