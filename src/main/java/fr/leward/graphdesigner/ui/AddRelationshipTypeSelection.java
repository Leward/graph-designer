package fr.leward.graphdesigner.ui;

import fr.leward.graphdesigner.MainController;
import fr.leward.graphdesigner.event.handler.RelationshipTypeSelectedHandler;
import fr.leward.graphdesigner.graph.Graph;
import fr.leward.graphdesigner.graph.Relationship;
import fr.leward.graphdesigner.graph.RelationshipType;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * A box which appears after selecting the second node of a relationship.
 * It allows to pick the type of the relationship being created.
 *
 * <pre>
 *      +-------------------------------+
 *      | ________________________      |
 *      | |_______________________|  OK |    <- textbox allow to add a new relationship type on the fly
 *      |                               |
 *      |   Rel Type 1                  |    <- a clickable, existing relationship type
 *      |   Rel Type 2                  |
 *      +-------------------------------|
 *
 * </pre>
 *
 * When the relationship type is selected, `onRelationshipSelectedHandler` is called.
 */
public class AddRelationshipTypeSelection extends VBox {

    private static final Logger log = LoggerFactory.getLogger(AddRelationshipTypeSelection.class);

    /**
     * The Graph object.
     */
    private Graph graph;

    /**
     * The Relationship (edge) being created.
     */
    private Relationship relationship;

    /**
     * Handler to call when the relationship type has been selected.
     */
    private RelationshipTypeSelectedHandler onRelationshipSelectedHandler;

    /**
     * First (upper) block containing the text field and OK button
     */
    private HBox firstLine;

    /**
     * Second (lower) containing the a selectable list of existing relationships
     */
    private VBox secondLine;

    private Map<RelationshipType, AddRelationshipTypeEntry> relationshipTypeEntries = new HashMap<>();

    /**
     * Text field allowing to create a new relationship type on the go
     */
    private TextField textField;

    /**
     * Button to confirm the textField
     */
    private Button okButton;

    /**
     * Relationship type that is being selected by the user.
     * It is used to render the cursor on the relationship type that is being selected.
     */
    private ObjectProperty<RelationshipType> selectedRelastionshipType = new SimpleObjectProperty<>(null);

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
            AddRelationshipTypeEntry relationshipTypeEntry = new AddRelationshipTypeEntry(relationshipType);
            secondLine.getChildren().add(relationshipTypeEntry);
            relationshipTypeEntry.setOnMouseClicked(mouseEvent -> {
                ok(relationshipType);
            });
            relationshipTypeEntries.put(relationshipType, relationshipTypeEntry);
        });
        getChildren().add(secondLine);

        // When the selected relationship changes
        selectedRelastionshipType.addListener((observable) -> {
            textField.setText(selectedRelastionshipType.get().getName());
            for (AddRelationshipTypeEntry relationshipTypeEntry : relationshipTypeEntries.values()) {
                if (relationshipTypeEntry.getRelationshipType() != selectedRelastionshipType.getValue()) {
                    relationshipTypeEntry.unselect();
                }
            }
            relationshipTypeEntries.get(selectedRelastionshipType.getValue()).select();
        });

        // Default selected relationship type
        if(graph.getRelationshipTypes().size() > 0) {
            selectedRelastionshipType.setValue(graph.getRelationshipTypes().get(0));
        }

        // Listen for keayboard events
        log.debug("Listen for keayboard events");
        Node root = MainController.getInstance().getRoot();
        root.addEventHandler(KeyEvent.KEY_PRESSED, onKeyPressedEventHandler);
        addEventHandler(MouseEvent.MOUSE_MOVED, (event) -> {
            log.debug("x:" + event.getX() + ", y:" + event.getY());
        });
    }

    /**
     *
     * @param selectedType
     */
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

    /**
     *
     * @param relationshipType
     */
    protected void ok(RelationshipType relationshipType) {
        if(onRelationshipSelectedHandler != null) {
            onRelationshipSelectedHandler.handle(relationshipType);
        }
    }

    /**
     *
     */
    protected void ok() {
        ok(textField.getText());
    }

    public void setOnRelationshipSelectedHandler(RelationshipTypeSelectedHandler onRelationshipSelectedHandler) {
        this.onRelationshipSelectedHandler = onRelationshipSelectedHandler;
    }

    private EventHandler<KeyEvent> onKeyPressedEventHandler = (event) -> {
        if(event.getCode() == KeyCode.PAGE_DOWN) {
            int indexOfSelected = graph.getRelationshipTypes().indexOf(selectedRelastionshipType.get());
            if(indexOfSelected < relationshipTypeEntries.size() - 1) {
                RelationshipType relationshipTypeToSelect = graph.getRelationshipTypes().get(indexOfSelected + 1);
                selectedRelastionshipType.setValue(relationshipTypeToSelect);
            }
        }
        else if(event.getCode() == KeyCode.PAGE_UP) {
            int indexOfSelected = graph.getRelationshipTypes().indexOf(selectedRelastionshipType.get());
            if(indexOfSelected > 0) {
                RelationshipType relationshipTypeToSelect = graph.getRelationshipTypes().get(indexOfSelected - 1);
                selectedRelastionshipType.setValue(relationshipTypeToSelect);
            }
        }
        else if(event.getCode() == KeyCode.ENTER) {
            ok();
        }
    };
}
