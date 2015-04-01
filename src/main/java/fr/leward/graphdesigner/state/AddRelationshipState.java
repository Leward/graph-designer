package fr.leward.graphdesigner.state;

import fr.leward.graphdesigner.MainController;
import fr.leward.graphdesigner.event.GraphPaneMouseMovedEvent;
import fr.leward.graphdesigner.event.LeaveCurrentStateEvent;
import fr.leward.graphdesigner.event.NodeClickedEvent;
import fr.leward.graphdesigner.event.bus.EventConsumer;
import fr.leward.graphdesigner.event.bus.EventStream;
import fr.leward.graphdesigner.event.bus.EventStreams;
import fr.leward.graphdesigner.graph.Node;
import fr.leward.graphdesigner.graph.Relationship;
import fr.leward.graphdesigner.math.Arrow;
import fr.leward.graphdesigner.math.StraightLine;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Paul-Julien on 01/02/2015.
 */
public class AddRelationshipState implements State {

    private static final Logger log = LoggerFactory.getLogger(AddRelationshipState.class);

    private Relationship relationship;
    private Node startNode;
    private Node endNode;
    private Line line;

    @Override
    public void enterState() {
        log.debug("Enter AddRelationshipState");
        // Listen for exit state event
        EventStreams.leaveCurrentStateEventStream.subscribe(leaveCurrentStateEventConsumer);

        // Listen for clicks on nodes
        EventStreams.nodeClickedEventStream.subscribe(nodeClickedEventConsumer);

        // Listen for mouse move to update the line of the relationship that is being added
        EventStreams.graphPaneMouseMovedEventStream.subscribe(graphPaneMouseMovedEventConsumer);
    }

    @Override
    public void leaveState() {
        // If add relationship is not complete when leaving the state remove the line we drew
        if(endNode == null && line != null) {
            MainController.getInstance().getPane().getChildren().removeAll(line);
        }

        // Unsubscribe registered events
        EventStreams.leaveCurrentStateEventStream.unsubscribe(leaveCurrentStateEventConsumer);
        EventStreams.nodeClickedEventStream.unsubscribe(nodeClickedEventConsumer);
        EventStreams.graphPaneMouseMovedEventStream.unsubscribe(graphPaneMouseMovedEventConsumer);

        // Restore UI state
        MainController.getInstance().getCreateRelationshipButton().setSelected(false);
        MainController.getInstance().leaveCurrentState();
    }

    private EventConsumer<LeaveCurrentStateEvent> leaveCurrentStateEventConsumer = new EventConsumer<LeaveCurrentStateEvent>() {
        @Override
        public void consume(LeaveCurrentStateEvent event) {
            leaveState();
        }
    };

    private EventConsumer<NodeClickedEvent> nodeClickedEventConsumer = new EventConsumer<NodeClickedEvent>() {
        @Override
        public void consume(NodeClickedEvent event) {
            if(startNode == null) {
                startNode = event.getNode();
                double lineX = startNode.getCircle().getCenterX();
                double lineY = startNode.getCircle().getCenterY();
                line = new Line(lineX, lineY, lineX, lineY);
                MainController mainController = MainController.getInstance();
                mainController.getPane().getChildren().add(line);
                line.toBack();
            }
            else {
                endNode = event.getNode();
                line.setEndX(endNode.getCircle().getCenterX());
                line.setEndY(endNode.getCircle().getCenterY());

                // Add the relationship to the graph
                relationship = new Relationship(startNode, endNode);
                startNode.getOutRelationships().add(relationship);
                endNode.getInRelationships().add(relationship);
                MainController.getInstance().getGraph().addRelationship(relationship);

                MainController.getInstance().getPane().getChildren().remove(line);

                // Create and set shape of the arrow
                Pane graphPane = MainController.getInstance().getPane();
                Arrow arrow = new Arrow(relationship);
                graphPane.getChildren().add(arrow.getLine());
                graphPane.getChildren().add(arrow.getHeadPath());
                relationship.setArrow(arrow);

                leaveState();
            }
        }
    };

    private EventConsumer<GraphPaneMouseMovedEvent> graphPaneMouseMovedEventConsumer = new EventConsumer<GraphPaneMouseMovedEvent>() {
        @Override
        public void consume(GraphPaneMouseMovedEvent event) {
            if(startNode != null) {
                MouseEvent mouseEvent = event.getMouseEvent();
                line.setEndX(mouseEvent.getX());
                line.setEndY(mouseEvent.getY());
            }
        }
    };

    public Node getStartNode() {
        return startNode;
    }

    public void setStartNode(Node startNode) {
        this.startNode = startNode;
    }

    public Node getEndNode() {
        return endNode;
    }

    public void setEndNode(Node endNode) {
        this.endNode = endNode;
    }

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
    }


}
