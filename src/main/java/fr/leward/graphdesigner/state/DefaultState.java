package fr.leward.graphdesigner.state;

import fr.leward.graphdesigner.MainController;
import fr.leward.graphdesigner.event.*;
import fr.leward.graphdesigner.event.bus.EventConsumer;
import fr.leward.graphdesigner.event.bus.EventStream;
import fr.leward.graphdesigner.event.bus.EventStreams;
import fr.leward.graphdesigner.graph.Node;
import fr.leward.graphdesigner.graph.Relationship;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paul-Julien on 12/02/2015.
 */
public class DefaultState implements State {

    private static final Logger log = LoggerFactory.getLogger(DefaultState.class);

    private List<Node> selectedNodes = new ArrayList<Node>();
    private boolean isDraging = false;

    @Override
    public void enterState() {
        log.debug("Enter Default State");
        EventStreams.leaveCurrentStateEventStream.subscribe(leaveCurrentStateEventConsumer);
        EventStreams.nodeDraggedEventStream.subscribe(nodeDraggedEventConsumer);
        EventStreams.nodeClickedEventStream.subscribe(nodeClickedEventConsumer);
        EventStreams.mouseEnterNodeEventStream.subscribe(mouseEnterNodeEventConsumer);
        EventStreams.mouseLeaveNodeEventEventStream.subscribe(mouseLeaveNodeEventConsumer);
    }

    @Override
    public void leaveState() {
        log.debug("Leave Default State");
        if(selectedNodes.size() > 0) {
            for(Node node : selectedNodes) {
                node.unselectNode();
            }
            selectedNodes.clear();
        }

        EventStreams.leaveCurrentStateEventStream.unsubscribe(leaveCurrentStateEventConsumer);
        EventStreams.nodeDraggedEventStream.unsubscribe(nodeDraggedEventConsumer);
        EventStreams.nodeClickedEventStream.unsubscribe(nodeClickedEventConsumer);
        EventStreams.mouseEnterNodeEventStream.unsubscribe(mouseEnterNodeEventConsumer);
        EventStreams.mouseLeaveNodeEventEventStream.unsubscribe(mouseLeaveNodeEventConsumer);
    }

    private EventConsumer<LeaveCurrentStateEvent> leaveCurrentStateEventConsumer = new EventConsumer<LeaveCurrentStateEvent>() {
        @Override
        public void consume(LeaveCurrentStateEvent event) {
            leaveState();
        }
    };

    private EventConsumer<NodeDraggedEvent> nodeDraggedEventConsumer = new EventConsumer<NodeDraggedEvent>() {
        @Override
        public void consume(NodeDraggedEvent event) {
            MouseEvent mouseEvent = event.getMouseEvent();
            Circle circle = event.getNode().getCircle();
            circle.setCenterX(mouseEvent.getX());
            circle.setCenterY(mouseEvent.getY());

            // Update lines
            Node movedNode = event.getNode();
            for(Relationship relationship : movedNode.getOutRelationships()) {
                relationship.updateLine();
            }

            for(Relationship relationship : movedNode.getInRelationships()) {
                relationship.updateLine();
            }
            isDraging = true;
        }
    };

    private EventConsumer<NodeClickedEvent> nodeClickedEventConsumer = new EventConsumer<NodeClickedEvent>() {
        @Override
        public void consume(NodeClickedEvent event) {
            MouseEvent mouseEvent = event.getMouseEvent();
            Node targetNode = event.getNode();
            if(isDraging) {
                isDraging = false;
                return;
            }

            // CTRL has been pressed
            if(mouseEvent.isControlDown()) {
                if(selectedNodes.contains(targetNode)) {
                    selectedNodes.remove(targetNode);
                    targetNode.unselectNode();
                    EventStreams.nodeSelectedEventStream.publish(new NodeSelectedEvent(targetNode));
                }
                else {
                    selectedNodes.add(targetNode);
                    targetNode.selectNode();
                    EventStreams.nodeUnselectedEventStream.publish(new NodeUnselectedEvent(targetNode));
                }
            }
            // Regular selection, CTRL button has not been pressed
            else {
                // Click on a node when there is no node selected
                if(selectedNodes.size() == 0) {
                    selectedNodes.add(targetNode);
                    targetNode.selectNode();
                    EventStreams.nodeSelectedEventStream.publish(new NodeSelectedEvent(targetNode));
                }
                // Click on a node that is currently selected
                else if(selectedNodes.contains(targetNode)) {
                    for(Node node : selectedNodes) {
                        if(node != targetNode) {
                            node.unselectNode();
                            EventStreams.nodeSelectedEventStream.publish(new NodeSelectedEvent(node));
                        }
                    }
                    // Keep only the targetNode
                    selectedNodes.clear();
                    selectedNodes.add(targetNode);
                }
                // Click on a node is not currently selected
                else {
                    for(Node node : selectedNodes) {
                        node.unselectNode();
                        EventStreams.nodeSelectedEventStream.publish(new NodeSelectedEvent(node));
                    }
                    selectedNodes.clear();
                    selectedNodes.add(targetNode);
                    targetNode.selectNode();
                    EventStreams.nodeSelectedEventStream.publish(new NodeSelectedEvent(targetNode));
                }
            }
        }
    };

    private EventConsumer<MouseEnterNodeEvent> mouseEnterNodeEventConsumer = new EventConsumer<MouseEnterNodeEvent>() {
        @Override
        public void consume(MouseEnterNodeEvent event) {
            event.getNode().setHovered(true);
        }
    };

    private EventConsumer<MouseLeaveNodeEvent> mouseLeaveNodeEventConsumer = new EventConsumer<MouseLeaveNodeEvent>() {
        @Override
        public void consume(MouseLeaveNodeEvent event) {
            event.getNode().setHovered(false);
        }
    };

    public List<Node> getSelectedNodes() {
        return selectedNodes;
    }
}
