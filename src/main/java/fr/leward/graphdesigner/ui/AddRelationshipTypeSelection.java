package fr.leward.graphdesigner.ui;

import fr.leward.graphdesigner.event.handler.RelationshipTypeSelectedHandler;
import fr.leward.graphdesigner.graph.Graph;
import fr.leward.graphdesigner.graph.Relationship;
import fr.leward.graphdesigner.graph.RelationshipType;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddRelationshipTypeSelection extends VBox {

    private static final Logger log = LoggerFactory.getLogger(AddRelationshipTypeSelection.class);

    private Graph graph;
    private Relationship relationship;
    private RelationshipTypeSelectedHandler onRelationshipSelectedHandler;

    private HBox firstLine;
    private VBox secondLine;

    private TextField textField;
    private Button okButton;

    public AddRelationshipTypeSelection(Graph graph, Relationship relationship) {
        this.graph = graph;
        this.relationship = relationship;

        getStyleClass().add("relationship-type-selection");
        setStyle("-fx-padding: 5px; " +
                "-fx-background-color: lightblue;" +
                "-fx-border-color: #999999;" +
                "-fx-border-width: 1px;");

        // First Line
        firstLine = new HBox();
        firstLine.setStyle("-fx-padding: 0 0 5px 0;");
        getChildren().add(firstLine);
        textField = new TextField();
        textField.setPromptText("RELATED_TO");
        okButton = new Button("Ok");
        okButton.setOnAction((event) -> {
            log.debug("Ok Clicked");
            ok();
        });
        firstLine.getChildren().addAll(textField, okButton);

        // Second Line
        secondLine = new VBox();
        secondLine.getStyleClass().add("relationship-types");
        graph.getRelationshipTypes().forEach(relationshipType -> {
            HBox relTypeContainer = new HBox();
            relTypeContainer.getStyleClass().add("relationship");

            Label relTypeLabel = new Label(relationshipType.getName());
            relTypeContainer.getChildren().add(relTypeLabel);
            secondLine.getChildren().add(relTypeContainer);

            relTypeContainer.setOnMouseClicked(mouseEvent -> {
                ok(relationshipType);
            });
        });
        getChildren().add(secondLine);
    }

    protected void ok(String selectedType) {
        if(selectedType.trim().isEmpty()) {
            return;
        }
        RelationshipType relationshipType = graph.getRelationshipTypeByName(selectedType);
        if(relationshipType == null) {
            relationshipType = new RelationshipType(textField.getText());
            graph.addRelationshipType(relationshipType);
        }
        ok(relationshipType);
    }

    protected void ok(RelationshipType relationshipType) {
        if(onRelationshipSelectedHandler != null) {
            onRelationshipSelectedHandler.handle(relationshipType);
        }
    }

    protected void ok() {
        ok(textField.getText());
    }

    public void setOnRelationshipSelectedHandler(RelationshipTypeSelectedHandler onRelationshipSelectedHandler) {
        this.onRelationshipSelectedHandler = onRelationshipSelectedHandler;
    }
}
