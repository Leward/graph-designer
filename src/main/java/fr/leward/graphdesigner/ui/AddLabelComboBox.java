package fr.leward.graphdesigner.ui;

import fr.leward.graphdesigner.graph.Graph;
import fr.leward.graphdesigner.graph.Label;
import fr.leward.graphdesigner.graph.Node;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Paul-Julien on 06/04/2015.
 */
public class AddLabelComboBox extends HBox {

    private ComboBox<Label> comboBox;
    private Button cancelButton;

    private static final Logger log = LoggerFactory.getLogger(AddLabelComboBox.class);

    private Graph graph;
    private Selection selection;
    private ChangeListener<Label> changeListener;

    public AddLabelComboBox(Graph graph, Selection selection, ChangeListener<Label> changeListener, EventHandler cancelListener) {
        super();
        this.graph = graph;
        this.selection = selection;
        this.changeListener = changeListener;
        setSpacing(5);

        // Combo Box
        comboBox = new ComboBox<>(generateObservableList());
        comboBox.valueProperty().addListener(changeListener);
        getChildren().add(comboBox);

        // Cancel Button
        cancelButton = new Button("X");
        cancelButton.setOnMouseClicked((mouseEvent) -> {
            cancelListener.handle(mouseEvent);
        });
        getChildren().add(cancelButton);
    }

    private ObservableList<Label> generateObservableList() {
        ObservableList observableList = FXCollections.observableArrayList(graph.getLabels());
        // TODO: implementation compatible with selection
        return observableList;
    }

    public ObservableList<Label> getItems() {
        return comboBox.getItems();
    }

    public ComboBox<Label> getComboBox() {
        return comboBox;
    }

    public Button getCancelButton() {
        return cancelButton;
    }
}
