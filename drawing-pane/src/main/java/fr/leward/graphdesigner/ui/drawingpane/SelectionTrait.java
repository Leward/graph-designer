package fr.leward.graphdesigner.ui.drawingpane;

import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

public interface SelectionTrait {
    /**
     * An observable set of the currently selected items.
     */
    ObservableSet<Long> selection = FXCollections.observableSet();

    /**
     * Select a single Node or Relationship by its ID
     */
    default void select(long id) {
        selection.removeIf(it -> it != id);
        selection.add(id);
    }

    /**
     * Add a single Node or Relationship to the existing selection by its ID
     */
    default void addToSelection(long id) {
        selection.add(id);
    }

    /**
     * Whether a Node or Relationship with a given ID is part of the selection
     */
    default boolean isSelected(long node) {
        return selection.contains(node);
    }
}
