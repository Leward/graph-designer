package fr.leward.graphdesigner.event.handler;

import fr.leward.graphdesigner.MainController;
import fr.leward.graphdesigner.graph.Relationship;
import fr.leward.graphdesigner.ui.Selection;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public class RelationshipClickedEventHandler implements EventHandler<MouseEvent> {

    private MainController mainController;
    private Selection selection;
    private Relationship relationship;

    public RelationshipClickedEventHandler(Relationship relationship)  {
        mainController = MainController.getInstance();
        selection = mainController.getSelection();
        this.relationship = relationship;
    }

    @Override
    public void handle(MouseEvent event) {
        if(selection.isSelectionLocked()) {
            return;
        }
        if(event.isControlDown()) {
            handleCtrlClick();
        }
        else {
            handleRegularClick();
        }
    }

    /**
     * Handle a Click with the CTRL button pressed
     */
    private void handleCtrlClick() {
        if(selection.contains(relationship)) {
            selection.remove(relationship);
        }
        else {
            selection.add(relationship);
        }
    }

    /**
     * Handle a regular click: without CTRL button pressed
     */
    private void handleRegularClick() {
        // Click on a relationship when the is nothing in the selection
        if(selection.size() == 0) {
            selection.add(relationship);
        }
        // Click on a relationship that is currently selected
        else if(selection.contains(relationship)) {
            selection.clearBut(relationship);
        }
        // Click on a relationship that is not currently selected
        else {
            selection.clear();
            selection.add(relationship);
        }
    }
}
