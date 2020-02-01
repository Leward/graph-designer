package fr.leward.graphdesigner.state;

import fr.leward.graphdesigner.MainController;
import fr.leward.graphdesigner.event.GraphPaneMouseMovedEvent;
import fr.leward.graphdesigner.event.LeaveCurrentStateEvent;
import fr.leward.graphdesigner.event.NodeClickedEvent;
import fr.leward.graphdesigner.event.bus.EventConsumer;
import fr.leward.graphdesigner.event.bus.EventStreams;
import fr.leward.graphdesigner.event.handler.RelationshipClickedEventHandler;
import fr.leward.graphdesigner.event.handler.RelationshipTypeSelectedHandler;
import fr.leward.graphdesigner.graph.Node;
import fr.leward.graphdesigner.graph.Relationship;
import fr.leward.graphdesigner.graph.RelationshipType;
import fr.leward.graphdesigner.math.Arrow;
import fr.leward.graphdesigner.ui.AddRelationshipTypeSelection;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddRelationshipState implements State {

    private static final Logger log = LoggerFactory.getLogger(AddRelationshipState.class);

    private Relationship relationship;
    private Node startNode;
    private Node endNode;
    private RelationshipType relationshipType;
    private Line line;
    private AddRelationshipTypeSelection addRelationshipTypeSelection;

    @Override
    public void enterState() {
        log.debug("Enter AddRelationshipState");

        // Lock the selection
        MainController.getInstance().getSelection().lock();

        // Listen for exit state event
        EventStreams.leaveCurrentStateEventStream.subscribe(leaveCurrentStateEventConsumer);

        // Listen for clicks on nodes
        EventStreams.nodeClickedEventStream.subscribe(nodeClickedEventConsumer);

        // Listen for mouse move to update the line of the relationship that is being added
        EventStreams.graphPaneMouseMovedEventStream.subscribe(graphPaneMouseMovedEventConsumer);
    }

    @Override
    public void leaveState() {
        log.debug("Leave AddRelationshipState");
        // If add relationship is not complete when leaving the state remove the line we drew
        if(relationshipType == null) {
            MainController.getInstance().getPane().getChildren().removeAll(line, addRelationshipTypeSelection);
        }

        // Unlock the selection
        MainController.getInstance().getSelection().unlock();

        // Unsubscribe registered events
        EventStreams.leaveCurrentStateEventStream.unsubscribe(leaveCurrentStateEventConsumer);
        EventStreams.nodeClickedEventStream.unsubscribe(nodeClickedEventConsumer);
        EventStreams.graphPaneMouseMovedEventStream.unsubscribe(graphPaneMouseMovedEventConsumer);

        // Restore UI state
        MainController.getInstance().getCreateRelationshipButton().setSelected(false);
        MainController.getInstance().leaveCurrentState();
    }

    private EventConsumer<LeaveCurrentStateEvent> leaveCurrentStateEventConsumer = event -> leaveState();

    private EventConsumer<NodeClickedEvent> nodeClickedEventConsumer = (NodeClickedEvent event) -> {
        if (startNode == null) {
            startNode = event.getNode();
            double lineX = startNode.getCircle().getCenterX();
            double lineY = startNode.getCircle().getCenterY();
            line = new Line(lineX, lineY, lineX, lineY);
            MainController mainController = MainController.getInstance();
            mainController.getPane().getChildren().add(line);
            line.toBack();
        } else if (endNode == null) {
            endNode = event.getNode();
            line.setEndX(endNode.getCircle().getCenterX());
            line.setEndY(endNode.getCircle().getCenterY());

            selectRelationshipType(event);
        }
    };

    public RelationshipTypeSelectedHandler relationshipTypeSelectedHandler = (selectedRelationshipType) -> {
        relationshipType = selectedRelationshipType;

        // Add the relationship to the graph
        relationship = new Relationship(startNode, endNode, relationshipType);
        startNode.getOutRelationships().add(relationship);
        endNode.getInRelationships().add(relationship);
        MainController.getInstance().getGraph().addRelationship(relationship);

        // Clean pane drawings
        Pane pane = MainController.getInstance().getPane();
        pane.getChildren().remove(line);
        pane.getChildren().remove(addRelationshipTypeSelection);

        // Create and set shape of the arrow
        Pane graphPane = MainController.getInstance().getPane();
        Arrow arrow = new Arrow(relationship);
        graphPane.getChildren().add(arrow.getLine());
        graphPane.getChildren().add(arrow.getHeadPath());
        graphPane.getChildren().add(arrow.getTypeLabel());
        relationship.setArrow(arrow);

        relationship.getArrow().setOnMouseClicked(new RelationshipClickedEventHandler(relationship));

        // Relationship properly created, leave state
        leaveState();
    };

    public void selectRelationshipType(NodeClickedEvent nodeClickedEvent) {
        MouseEvent mouseEvent = nodeClickedEvent.getMouseEvent();
        addRelationshipTypeSelection = new AddRelationshipTypeSelection(MainController.getInstance().getGraph(), relationship);
        addRelationshipTypeSelection.setOnRelationshipSelectedHandler(relationshipTypeSelectedHandler);
        addRelationshipTypeSelection.setLayoutX(mouseEvent.getX());
        addRelationshipTypeSelection.setLayoutY(mouseEvent.getY());
        MainController.getInstance().getPane().getChildren().add(addRelationshipTypeSelection);
        addRelationshipTypeSelection.requestFocus(); // can only be done after adding the node to a scene
    }

    private EventConsumer<GraphPaneMouseMovedEvent> graphPaneMouseMovedEventConsumer = event -> {
        if (isSelectingEndNode()) {
            moveLineToMouseCursor(event.getMouseEvent());
        }
    };

    public boolean isSelectingEndNode() {
        return startNode != null && endNode == null;
    }

    public void moveLineToMouseCursor(MouseEvent mouseEvent) {
        line.setEndX(mouseEvent.getX());
        line.setEndY(mouseEvent.getY());
    }

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
