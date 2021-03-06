package fr.leward.graphdesigner.state;

import fr.leward.graphdesigner.MainController;
import fr.leward.graphdesigner.event.*;
import fr.leward.graphdesigner.event.bus.EventConsumer;
import fr.leward.graphdesigner.event.bus.EventStreams;
import fr.leward.graphdesigner.event.handler.NodeClickedEventHandler;
import fr.leward.graphdesigner.graph.Node;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;

/**
 * Created by Paul-Julien on 08/02/2015.
 */
public class AddNodeState implements State {

//    private static final Logger log = LoggerFactory.getLogger(AddNodeState.class);

    @Override
    public void enterState() {
        // Listen for exit state event
        EventStreams.leaveCurrentStateEventStream.subscribe(leaveCurrentStateEventConsumer);
        EventStreams.leaveAddNodeStateEventStream.subscribe(leaveAddNodeStateEventConsumer);

        // Listen for click on the graph area
        EventStreams.graphPaneClickedEventStream.subscribe(graphPaneClickedEventConsumer);
    }

    @Override
    public void leaveState() {
        EventStreams.graphPaneClickedEventStream.unsubscribe(graphPaneClickedEventConsumer);
        EventStreams.leaveCurrentStateEventStream.unsubscribe(leaveCurrentStateEventConsumer);
        EventStreams.leaveAddNodeStateEventStream.unsubscribe(leaveAddNodeStateEventConsumer);
        MainController.getInstance().getCreateNodeButton().setSelected(false);
        MainController.getInstance().leaveCurrentState();
    }

    private EventConsumer<LeaveCurrentStateEvent> leaveCurrentStateEventConsumer = event -> leaveState();

    private EventConsumer<LeaveAddNodeStateEvent> leaveAddNodeStateEventConsumer = event -> leaveState();

    private EventConsumer<GraphPaneClickedEvent> graphPaneClickedEventConsumer = graphPaneClickedEvent -> {
        var mouseEvent = graphPaneClickedEvent.getMouseEvent();
        createNode(mouseEvent.getX(), mouseEvent.getY());
        leaveState();
    };

    private void createNode(double x, double y) {
        Circle circle = new Circle(x, y, 20);
        circle.setCursor(Cursor.HAND);

        // Create the node
        final Node node = new Node();
        node.setCircle(circle);

        circle.setOnMouseClicked(new NodeClickedEventHandler(node));
        circle.setOnMouseDragged(event -> EventStreams.nodeDraggedEventStream.publish(new NodeDraggedEvent(node, event)));
        circle.setOnMouseEntered(event -> EventStreams.mouseEnterNodeEventStream.publish(new MouseEnterNodeEvent(node, event)));
        circle.setOnMouseExited(event -> EventStreams.mouseLeaveNodeEventEventStream.publish(new MouseLeaveNodeEvent(node, event)));
        MainController.getInstance().getPane().getChildren().add(circle);
        node.setId(MainController.getInstance().getGraph().generateId());
        MainController.getInstance().getGraph().addNode(node);
    }

}
