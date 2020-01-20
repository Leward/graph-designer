package fr.leward.graphdesigner.event.bus;

import fr.leward.graphdesigner.event.*;

/**
 * Created by Paul-Julien on 02/02/2015.
 */
public class EventStreams {

    public static EventStream<EnterAddNodeStateEvent> enterAddNodeStateEventStream = new EventStream<EnterAddNodeStateEvent>();
    public static EventStream<EnterAddRelationshipStateEvent> enterAddRelationshipStateEventStream = new EventStream<EnterAddRelationshipStateEvent>();
    public static EventStream<EnterDefaultStateEvent> enterDefaultStateEventStream = new EventStream<EnterDefaultStateEvent>();
    public static EventStream<GraphPaneClickedEvent> graphPaneClickedEventStream = new EventStream<GraphPaneClickedEvent>();
    public static EventStream<GraphPaneMouseMovedEvent> graphPaneMouseMovedEventStream = new EventStream<GraphPaneMouseMovedEvent>();
    public static EventStream<GraphUpdatedEvent> graphUpdatedEventStream = new EventStream<GraphUpdatedEvent>();
    public static EventStream<LabelAddedEvent> labelAddedEventStream = new EventStream<LabelAddedEvent>();
    public static EventStream<LabelRemovedEvent> labelRemovedEventStream = new EventStream<LabelRemovedEvent>();
    public static EventStream<LeaveAddNodeStateEvent> leaveAddNodeStateEventStream = new EventStream<LeaveAddNodeStateEvent>();
    public static EventStream<LeaveCurrentStateEvent> leaveCurrentStateEventStream = new EventStream<LeaveCurrentStateEvent>();
    public static EventStream<MouseEnterNodeEvent> mouseEnterNodeEventStream = new EventStream<MouseEnterNodeEvent>();
    public static EventStream<MouseLeaveNodeEvent> mouseLeaveNodeEventEventStream = new EventStream<MouseLeaveNodeEvent>();
    public static EventStream<NodeClickedEvent> nodeClickedEventStream = new EventStream<NodeClickedEvent>();
    public static EventStream<NodeDraggedEvent> nodeDraggedEventStream = new EventStream<NodeDraggedEvent>();
    public static EventStream<NodeSelectedEvent> nodeSelectedEventStream = new EventStream<NodeSelectedEvent>();
    public static EventStream<NodeUnselectedEvent> nodeUnselectedEventStream = new EventStream<NodeUnselectedEvent>();

}
