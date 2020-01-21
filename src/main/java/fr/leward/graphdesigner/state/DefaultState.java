package fr.leward.graphdesigner.state;

import fr.leward.graphdesigner.MainController;
import fr.leward.graphdesigner.event.LeaveCurrentStateEvent;
import fr.leward.graphdesigner.event.MouseEnterNodeEvent;
import fr.leward.graphdesigner.event.MouseLeaveNodeEvent;
import fr.leward.graphdesigner.event.NodeDraggedEvent;
import fr.leward.graphdesigner.event.bus.EventConsumer;
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
//        EventStreams.nodeClickedEventStream.subscribe(nodeClickedEventConsumer);
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
//        EventStreams.nodeClickedEventStream.unsubscribe(nodeClickedEventConsumer);
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

            // Calculate deltas
            double dx = mouseEvent.getX() - circle.getCenterX();
            double dy = mouseEvent.getY() - circle.getCenterY();

            // Update dragged nodes
            circle.setCenterX(mouseEvent.getX());
            circle.setCenterY(mouseEvent.getY());

            // Ctrl is pressed we also move the other selected nodes
            if(mouseEvent.isControlDown()) {
                for(Node node : MainController.getInstance().getSelection().getNodes()) {
                    if(node != event.getNode()) {
                        Circle nodeCircle = node.getCircle();
                        nodeCircle.setCenterX(nodeCircle.getCenterX() + dx);
                        nodeCircle.setCenterY(nodeCircle.getCenterY() + dy);
                    }
                }
            }

            // Update lines
            Node movedNode = event.getNode();
            for(Relationship relationship : movedNode.getOutRelationships()) {
                relationship.updateLine();
            }
            for(Relationship relationship : movedNode.getInRelationships()) {
                relationship.updateLine();
            }

            // Ctrl is press, we also update the other selected nodes relationship's lines
            if(mouseEvent.isControlDown()) {
                for(Node node : selectedNodes) {
                    if(node != event.getNode()) {
                        for(Relationship relationship : node.getOutRelationships()) {
                            relationship.updateLine();
                        }
                        for(Relationship relationship : node.getInRelationships()) {
                            relationship.updateLine();
                        }
                    }
                }
            }

            // We remember that we are dragging the node to avoid conflicts with some other event
            isDraging = true;
            MainController.getInstance().setDraggedNodeDetected(true);
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

    /**
     * Get the first selected node
     * @return the first selected node from the selected nodes list, null if none
     */
    public Node getSelectedNode() {
        return (selectedNodes.isEmpty()) ? null : selectedNodes.get(0);
    }
}
