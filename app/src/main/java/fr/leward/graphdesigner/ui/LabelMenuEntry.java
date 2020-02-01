package fr.leward.graphdesigner.ui;

import fr.leward.graphdesigner.graph.Label;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

/**
 * Created by Paul-Julien on 19/02/2015.
 * TODO: Make this class to extend HBox for easier use
 */
public class LabelMenuEntry {

    private Label label;
    private EventHandler<MouseEvent> onLabelHBoxClickedEvent;

    private HBox container;
    private javafx.scene.control.Label indicatorLabel;
    private javafx.scene.control.Label titleLabel;

    public LabelMenuEntry(Label label) {
        this(label, null);
    }

    public LabelMenuEntry(Label label, EventHandler<MouseEvent> onLabelHBoxClickedEvent) {
        this.label = label;
        this.onLabelHBoxClickedEvent = onLabelHBoxClickedEvent;

        container = new HBox();
        container.getStyleClass().add("left-menu-item");
        container.setPadding(new Insets(3.0, 10.0, 3.0, 10.0));
        if(onLabelHBoxClickedEvent != null) {
            container.setOnMouseClicked(onLabelHBoxClickedEvent);
        }

        indicatorLabel = new javafx.scene.control.Label("");
        container.getChildren().add(indicatorLabel);

        titleLabel = new javafx.scene.control.Label(label.getLabel());
        container.getChildren().add(titleLabel);
    }

    public Label getLabel() {
        return label;
    }

    public HBox getContainer() {
        return container;
    }

    public javafx.scene.control.Label getIndicatorLabel() {
        return indicatorLabel;
    }

    public javafx.scene.control.Label getTitleLabel() {
        return titleLabel;
    }
}
