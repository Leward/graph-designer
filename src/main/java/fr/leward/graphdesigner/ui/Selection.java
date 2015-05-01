package fr.leward.graphdesigner.ui;

import fr.leward.graphdesigner.graph.Label;
import fr.leward.graphdesigner.graph.Node;
import fr.leward.graphdesigner.utils.ListUtils;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.transformation.FilteredList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Selection implements Observable {

    private static final Logger log = LoggerFactory.getLogger(Selection.class);

    private ObservableList<SelectableItem> selectedItems = FXCollections.observableArrayList();
    private ObservableList<Label> selectedLabels = FXCollections.observableArrayList();
    private ObservableList<Label> labelsInCommon = FXCollections.observableArrayList();

    /**
     * Is the selection locked? It means it cannot be modified.
     */
    private boolean selectionLocked;

    public Selection() {
        selectedItems.addListener(listChangeListener);
    }

    public void clearSelection() {
        selectedItems.clear();
    }

    public void selectItem(SelectableItem selectableItem) {
        if(selectionLocked) {
            return;
        }
        if(!selectedItems.contains(selectableItem)) {
            selectedItems.add(selectableItem);
        }
    }

    public void deselectItem(SelectableItem selectableItem) {
        if(selectionLocked) {
            return;
        }
        if(selectedItems.contains(selectableItem)) {
            selectedItems.remove(selectableItem);
        }
    }

    public boolean contains(SelectableItem selectableItem) {
        return selectedItems.contains(selectableItem);
    }

    public void add(SelectableItem selectableItem) {
        selectedItems.add(selectableItem);
        selectableItem.select();
    }

    public void remove(SelectableItem selectableItem) {
        selectedItems.remove(selectableItem);
        selectableItem.unselect();
    }

    public int size() {
        return selectedItems.size();
    }

    public void clear() {
        selectedItems.forEach(item -> { item.unselect(); });
        selectedItems.clear();
    }

    public void clearBut(SelectableItem selectableItemToKeep) {
        List<SelectableItem> itemsToBeRemoved = new ArrayList<>();
        for(SelectableItem item : selectedItems) {
            if(item != selectableItemToKeep) {
                itemsToBeRemoved.add(item);
            }
        }
        for(SelectableItem item : itemsToBeRemoved) {
            remove(item);
        }
    }

    /**
     * Get nodes in the selection
     * @return
     */
    public List<Node> getNodes() {
        return selectedItems
                .filtered(item -> { return item instanceof Node; })
                .stream()
                .map(item -> { return (Node) item; })
                .collect(Collectors.toList());
    }

    @Override
    public void addListener(InvalidationListener listener) {
        selectedItems.addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        selectedItems.removeListener(listener);
    }

    public boolean isSelectionLocked() {
        return selectionLocked;
    }

    public void setSelectionLocked(boolean selectionLocked) {
        this.selectionLocked = selectionLocked;
    }

    public void lock() {
        setSelectionLocked(true);
    }

    public void unlock() {
        setSelectionLocked(false);
    }

    public ObservableList<SelectableItem> getSelectedItems() {
        return selectedItems;
    }

    /**
     * Whenever a label in the selection is added or removed. Is used to
     * maintained the overall list of labels selected.
     */
    private InvalidationListener nodeLabelsListener = (observable) -> {
        recomputeLabels();
    };

    /**
     * When a node is added or removed in the selection apply the following operation:
     *  - add or remove the listener for the labels of this node.
     *  - update the list of labels
     */
    private ListChangeListener<SelectableItem> listChangeListener = (change) -> {
        while(change.next()) {
            // Hook nodes added to selection
            for(SelectableItem addedItem : change.getAddedSubList()) {
                if(addedItem instanceof Node) {
                    Node addedNode = (Node) addedItem;
                    addedNode.getLabels().addListener(nodeLabelsListener);
                }
            }
            // Remove hook from nodes removed from the selection
            for(SelectableItem removedItem : change.getRemoved()) {
                if(removedItem instanceof Node) {
                    Node removedNode = (Node) removedItem;
                    removedNode.getLabels().removeListener(nodeLabelsListener);
                }
            }
        }
        recomputeLabels();
    };

    /**
     * Update the list of selected labels. Does not overwrite the whole list,
     * but instead remove or add the missing or deleted elements.
     */
    private void recomputeLabels() {
        // Make a clean list with all the selected labels
        List<Label> foundLabels = new ArrayList<>();
        for(Node node : getNodes()) {
            for(Label nodeLabel : node.getLabelsAsList()) {
                if(!foundLabels.contains(nodeLabel)) {
                    foundLabels.add(nodeLabel);
                }
            }
        }
        ListUtils.syncLists(foundLabels, selectedLabels);

        // Detect labels common to the whole selection
        List<Label> labelsInCommon = new ArrayList<>();
        for(Label label : foundLabels) {
            boolean isLabelCommon = true;
            for(Node node : getNodes()) {
                if(!node.getLabelsAsList().contains(label)) {
                    isLabelCommon = false;
                }
            }
            if(isLabelCommon) {
                labelsInCommon.add(label);
            }
        }
        ListUtils.syncLists(labelsInCommon, this.labelsInCommon);
    }

    /**
     * The the list of all the labels present in the selection
     * @return
     */
    public ObservableList<Label> getSelectedLabels() {
        return selectedLabels;
    }

    /**
     * The list of labels shared by all the node present in the selection
     * @return
     */
    public ObservableList<Label> getLabelsInCommon() {
        return labelsInCommon;
    }
}
