package fr.leward.graphdesigner.ui.drawingpane.event;

import fr.leward.graphdesigner.core.IdGenerator;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.input.MouseEvent;

/**
 * A Relationship has been drawn by the user on the DrawingPane.
 */
public class RelationshipDrawnEvent extends Event {

    /**
     * ID of the new node (generated with an {@link IdGenerator})
     */
    public final long id;

    public final String type;

    public final long startNodeId;

    public final long endNodeId;

    public RelationshipDrawnEvent(IdGenerator idGenerator, String type, long startNodeId, long endNodeId) {
        super(new EventType<MouseEvent>(Event.ANY, "RELATIONSHIP_DRAWN"));
        this.id = idGenerator.nextId();
        this.type = type;
        this.startNodeId = startNodeId;
        this.endNodeId = endNodeId;
    }
}
