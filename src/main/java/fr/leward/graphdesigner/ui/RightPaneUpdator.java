package fr.leward.graphdesigner.ui;

import javafx.beans.InvalidationListener;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RightPaneUpdator {

    private static final Logger log = LoggerFactory.getLogger(RightPaneLabels.class);

    private Pane rightPane;
    private Selection selection;

    public RightPaneUpdator(Pane rightPane, Selection selection) {
        this.rightPane = rightPane;
        this.selection = selection;
        toEmptyState();
        selection.addListener(selectionChangedListener);
    }

    private void toEmptyState() {
        rightPane.getChildren().clear();
        Label label = new Label("Select a node or a relationship");
        rightPane.getChildren().add(label);
    }

    private void singleNodeSelected() {
        rightPane.getChildren().clear();
        log.debug("Selection " + selection.getSelectedItems());
        new RightPaneLabels(selection).build(rightPane);
    }

    private void multipleNodesSelected() {
        rightPane.getChildren().clear();
        Label label = new Label("Multiple node selected");
        rightPane.getChildren().add(label);
    }

// TODO: Handle relationship selection
//    private void singleRelationshipSelected() {
//        rightPane.getChildren().clear();
//        Label label = new Label("One relationship selected");
//        rightPane.getChildren().add(label);
//    }
//
//    private void multipleRelationshipsSelected() {
//        rightPane.getChildren().clear();
//        Label label = new Label("Multiple relationship selected");
//        rightPane.getChildren().add(label);
//    }



    /**
     * This listener is called when the selection change.
     * The right pane reacts according to what is selected on the screen.
     */
    private InvalidationListener selectionChangedListener = (observable) -> {
        if(selection.size() == 0) {
            log.debug("Selection changed: switch right pane to empty state");
            toEmptyState();
        }
        else if(selection.size() == 1) {
            log.debug("Selection changed: switch right pane to single node selected");
            singleNodeSelected();
        }
        else {
            log.debug("Selection changed: switch right pane to multiple nodes selected");
            multipleNodesSelected();
        }
    };

}
