package fr.leward.graphdesigner.ui;

import fr.leward.graphdesigner.graph.RelationshipType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Entry for a relationship type in the popup that is shown when a
 * relationship is being created.
 */
public class AddRelationshipTypeEntry extends HBox {

    /**
     * The relationship type of this entry
     */
    private RelationshipType relationshipType;

    /**
     * Text component to be displayed
     */
    private Label text;

    public AddRelationshipTypeEntry(RelationshipType relationshipType) {
        this.relationshipType = relationshipType;
        this.getStyleClass().add("relationship");
        text = new Label(relationshipType.getName());
        getChildren().add(text);
    }

    /**
     * Mark the entry as selected
     */
    public void select() {
        getStyleClass().add("selected");
    }

    /**
     * Mark the entry as not selected
     */
    public void unselect() {
        getStyleClass().remove("selected");
    }

    /**
     * Get the relationship type of this entry
     * @return
     */
    public RelationshipType getRelationshipType() {
        return relationshipType;
    }

}
