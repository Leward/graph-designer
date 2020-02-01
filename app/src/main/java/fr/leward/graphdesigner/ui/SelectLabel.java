package fr.leward.graphdesigner.ui;

import fr.leward.graphdesigner.graph.Graph;
import fr.leward.graphdesigner.graph.Label;
import javafx.collections.FXCollections;
import javafx.scene.control.ListView;

import java.util.Optional;

public class SelectLabel extends ListView<Label> {

    private Graph graph;
    private Optional<OnLabelSelected> callback = Optional.empty();

    public SelectLabel(Graph graph) {
        this.graph = graph;
        setItems(FXCollections.observableArrayList(graph.getLabels()));
        setPrefSize(150, 300);
    }

    public SelectLabel setCallback(OnLabelSelected callback) {
        this.callback = Optional.of(callback);
        return this;
    }
}

@FunctionalInterface
interface OnLabelSelected {
    void onLabelSelected(Label label);
}