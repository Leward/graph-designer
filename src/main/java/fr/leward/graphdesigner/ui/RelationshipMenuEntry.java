package fr.leward.graphdesigner.ui;

import fr.leward.graphdesigner.event.handler.RelationshipTypeSelectedHandler;
import fr.leward.graphdesigner.graph.RelationshipType;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

public class RelationshipMenuEntry extends HBox {

    private RelationshipType relationshipType;

    private Label label;
    private Label indicatorLabel = new Label("");
    private RelationshipTypeSelectedHandler relationshipTypeSelectedHandler;

    public RelationshipMenuEntry(RelationshipType relationshipType) {
        this(relationshipType, null);
    }

    public RelationshipMenuEntry(RelationshipType relationshipType, RelationshipTypeSelectedHandler relationshipTypeSelectedHandler) {
        this.relationshipType = relationshipType;
        if(relationshipTypeSelectedHandler != null) {
            this.relationshipTypeSelectedHandler = relationshipTypeSelectedHandler;
            setOnMouseClicked(event -> relationshipTypeSelectedHandler.handle(relationshipType));
        }

        getStyleClass().add("left-menu-item");
        setPadding(new Insets(3.0, 10.0, 3.0, 10.0));

        label = new Label(relationshipType.getName());
        getChildren().add(label);
        getChildren().add(indicatorLabel);
    }

    public RelationshipType getRelationshipType() {
        return relationshipType;
    }

    public Label getLabel() {
        return label;
    }

    public Label getIndicatorLabel() {
        return indicatorLabel;
    }
}
