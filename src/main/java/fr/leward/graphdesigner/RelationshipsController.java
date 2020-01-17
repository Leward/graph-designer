package fr.leward.graphdesigner;

import fr.leward.graphdesigner.event.handler.RelationshipTypeSelectedHandler;
import fr.leward.graphdesigner.graph.RelationshipType;
import fr.leward.graphdesigner.ui.RelationshipMenuEntry;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class RelationshipsController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(RelationshipsController.class);

    @FXML private Parent root;
    @FXML private VBox relationshipsMenuContainer;
    @FXML private HBox newRelationshipHBox;

    @FXML private Label relationshipTypeName;
    @FXML private TextField relationshipTypeTextField;
    @FXML private Button okButton;
    @FXML private Button cancelButton;
    @FXML private Button closeButton;

    /**
     * The relationship that is currently selected in the UI
     */
    private RelationshipType selectedRelationshipType;

    /**
     * The relationship types of the graph (direct reference)
     */
    private ObservableList<RelationshipType> relationshipTypes;

    /**
     * Entries in the menu for each Relationship TYpe
     */
    private Map<RelationshipType, RelationshipMenuEntry> relationshipMenuEntries = new HashMap<>();

    /**
     * Tells whether the selected relationship type has been modified
     */
    private BooleanProperty currentRelationshipTypeModified = new SimpleBooleanProperty();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Put the relationship types in the menu on the left
        relationshipTypes = MainController.getInstance().getGraph().getRelationshipTypes();
        for(RelationshipType relationshipType : relationshipTypes) {
            addMenuEntryForRelationshipType(relationshipType);
        }

        // By default the "new relationship type" box is selected
        selectHBox(newRelationshipHBox);
        okButton.setDisable(true);
        cancelButton.setDisable(true);

        // Close button behaviour
        closeButton.setOnAction((event) -> {
            ((Stage) root.getScene().getWindow()).close();
        });

        // OK & Cancel buttons behaviour
        okButton.setOnAction(event -> okButtonClicked());
        cancelButton.setOnAction(event -> cancelButtonClicked());

        // New Relationship Type button
        newRelationshipHBox.setOnMouseClicked(event -> onNewRelationshipTypeClicked());

        // Some form fields triggers the checking to determine whether the currently selected
        // relationship type has been modified or not.
        relationshipTypeTextField.textProperty().addListener( observable -> checkCurrentRelationshipTypeModified());

        // When the flag identifying that the rel. type changed, trigger an update of the UI.
        currentRelationshipTypeModified.addListener(observable -> updateUIToAccomodateRelationshipTypeModified());
    }

    /**
     * When a relationship type is selected in the left menu.
     */
    public RelationshipTypeSelectedHandler relationshipTypeSelected = (relationshipType) -> {
        // Mark the clicked HBox selected in the UI
        selectHBox(relationshipMenuEntries.get(relationshipType));

        // Update some variables and properties to match the selected relationship type
        selectedRelationshipType = relationshipType;
        relationshipTypeName.setText(relationshipType.getName());
        relationshipTypeTextField.setText(relationshipType.getName());

        // Relationship type names are not editable
        relationshipTypeTextField.setDisable(true);
    };

    /**
     *
     * @param hBox
     */
    private void selectHBox(HBox hBox) {
        // Clean old selection
        RelationshipMenuEntry selectedMenuEntry = relationshipMenuEntries.get(selectedRelationshipType);
        HBox currentlySelectedHBox = (selectedMenuEntry != null) ? selectedMenuEntry : newRelationshipHBox;
        currentlySelectedHBox.getStyleClass().remove("left-menu-item-selected");

        // Apply new selection
        hBox.getStyleClass().add("left-menu-item-selected");
    }

    /**
     *
     */
    private void onNewRelationshipTypeClicked() {
        selectHBox(newRelationshipHBox);
        selectedRelationshipType = null;
        relationshipTypeTextField.setDisable(false);
        relationshipTypeTextField.setText("");
    }

    /**
     * THis method do all the checking necessary to determine whether the selected
     * relationship type has been modified or not.
     */
    protected void checkCurrentRelationshipTypeModified() {
        boolean hasBeenModified = false;
        if(selectedRelationshipType == null) {
            if(!relationshipTypeTextField.getText().trim().isEmpty()) {
                hasBeenModified = true;
            }
        }
        else {
            if(!relationshipTypeTextField.getText().equals(selectedRelationshipType.getName())) {
                hasBeenModified = true;
            }
        }
        currentRelationshipTypeModified.setValue(hasBeenModified);
    }

    /**
     * Update the User Interface to the correct state accoring to whether to
     * currently selected relationship type has been modified or not.
     */
    protected void updateUIToAccomodateRelationshipTypeModified() {
        okButton.setDisable(!currentRelationshipTypeModified.getValue());
        cancelButton.setDisable(!currentRelationshipTypeModified.getValue());
    }

    /**
     *
     * @param relationshipType
     */
    protected void addMenuEntryForRelationshipType(RelationshipType relationshipType) {
        RelationshipMenuEntry menuEntry = new RelationshipMenuEntry(relationshipType, relationshipTypeSelected);
        relationshipMenuEntries.put(relationshipType, menuEntry);
        relationshipsMenuContainer.getChildren().add(menuEntry);
    }

    protected void okButtonClicked() {
        if(selectedRelationshipType == null) {
            RelationshipType relationshipType = new RelationshipType(relationshipTypeTextField.getText());
            relationshipTypes.add(relationshipType);
            addMenuEntryForRelationshipType(relationshipType);
            relationshipTypeTextField.setText("");
        }
    }

    protected void cancelButtonClicked() {

    }

}
