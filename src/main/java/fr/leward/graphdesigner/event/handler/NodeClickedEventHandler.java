package fr.leward.graphdesigner.event.handler;

import fr.leward.graphdesigner.MainController;
import fr.leward.graphdesigner.event.NodeClickedEvent;
import fr.leward.graphdesigner.event.bus.EventStreams;
import fr.leward.graphdesigner.graph.Node;
import fr.leward.graphdesigner.ui.SelectableItem;
import fr.leward.graphdesigner.ui.Selection;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler for node clicked event.
 * When a node is clicked it should select or deselect the node.
 */
public class NodeClickedEventHandler implements EventHandler<MouseEvent> {

    private static final Logger log = LoggerFactory.getLogger(NodeClickedEventHandler.class);

    private MainController mainController;
    private Node node;

    public NodeClickedEventHandler(Node node) {
        mainController = MainController.getInstance();
        this.node = node;
    }

    @Override
    public void handle(MouseEvent event) {
        // Click originates from a drag: leave
        if(mainController.isDraggedNodeDetected()) {
            mainController.setDraggedNodeDetected(false);
            return;
        }

        Selection selection = mainController.getSelection();
        if(!selection.isSelectionLocked()) {
            if (event.isControlDown()) {
                handleCtrlClick();
            } else {
                handleRegularClick();
            }
        }

        // TODO: We still need to propagate the event, the old way, this should be removed later on.
        EventStreams.nodeClickedEventStream.publish(new NodeClickedEvent(node, event));
    }

    /**
     * Handle a Click with the CTRL button pressed
     */
    private void handleCtrlClick() {
        Selection selection = mainController.getSelection();
        if(selection.contains(node)) {
            selection.remove(node);
        }
        else {
            selection.add(node);
        }
    }

    /**
     * Handle a regular click: without CTRL button pressed
     */
    private void handleRegularClick() {
        Selection selection = mainController.getSelection();
        // Click on a node, when there is no node selected
        if(selection.size() == 0) {
            selection.add(node);
        }
        // Click on a node that is currently selected
        else if(selection.contains(node)) {
            selection.clearBut(node);
        }
        // Click on a node that is not currently selected
        else {
            selection.clear();
            selection.add(node);
        }
    }

}
