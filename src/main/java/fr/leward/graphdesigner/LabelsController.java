package fr.leward.graphdesigner;

import fr.leward.graphdesigner.graph.Graph;
import fr.leward.graphdesigner.graph.Label;
import fr.leward.graphdesigner.ui.LabelMenuEntry;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.*;
import java.util.List;

/**
 * Created by Paul-Julien on 17/02/2015.
 */
public class LabelsController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(LabelsController.class);

    @FXML private Parent root;
    @FXML private VBox labelsMenuContainer;
    @FXML private HBox newLabelHBox;
    @FXML private Button okButton;
    @FXML private Button cancelButton;
    @FXML private Button closeButton;

    @FXML private TextField labelNameTextField;
    @FXML private ColorPicker labelColorPicker;

    /**
     * Keep track of whether the selected label is being edited and has some value changed
     */
    private BooleanProperty selectedLabelHasChanged = new SimpleBooleanProperty(false);

    /**
     * List of UI menu entry for each label
     */
    private Map<Label, LabelMenuEntry> labelMenuEntries = new HashMap<Label, LabelMenuEntry>();

    /**
     * The label that is currently selected in the UI
     */
    private Label selectedLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Bind Events
        newLabelHBox.setOnMouseClicked(newLabelHBoxClicked);
        okButton.setOnAction(onOkButtonAction);
        closeButton.setOnAction(onCloseButtonAction);
        labelNameTextField.textProperty().addListener(labelFormChangeListener);
        labelColorPicker.valueProperty().addListener(labelFormChangeListener);

        // Init labels and left menu
        Collection<Label> labels = MainController.getInstance().getGraph().getLabels();
        for(Label label : labels) {
            LabelMenuEntry labelMenuEntry = new LabelMenuEntry(label, onLabelHBoxClicked);
            labelMenuEntries.put(label, labelMenuEntry);
            labelsMenuContainer.getChildren().add(labelMenuEntry.getContainer());
        }
        selectHBox(newLabelHBox);
        labelColorPicker.setValue(null);

        // Watch some properties
        selectedLabelHasChanged.addListener(selectedLabelHasChangedPropertyListener);
    }

    private void selectHBox(HBox hBox) {
        LabelMenuEntry currentlySelectedLabelMenuEntry = labelMenuEntries.get(selectedLabel);
        HBox currentlySelectedHBox;
        if(currentlySelectedLabelMenuEntry != null) {
            currentlySelectedLabelMenuEntry.getIndicatorLabel().setText("");
            currentlySelectedHBox = currentlySelectedLabelMenuEntry.getContainer();
        }
        else {
            currentlySelectedHBox = newLabelHBox;
        }
        List<String> cssClasses = currentlySelectedHBox.getStyleClass();
        cssClasses.remove("left-menu-item-selected");
        hBox.getStyleClass().add("left-menu-item-selected");
    }

    private ChangeListener labelFormChangeListener = new ChangeListener() {
        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            checkSelectedLabelHasChanged();
//            if(selectedLabel != null) {
//                boolean equals = selectedLabel.getColor().equals(labelColorPicker.getValue());
//                LabelMenuEntry labelMenuEntry = labelMenuEntries.get(selectedLabel);
//                if(equals) {
//                    labelMenuEntry.getIndicatorLabel().setText("");
//                    if(!okButton.isDisabled()) {
//                        okButton.setDisable(true);
//                    }
//                }
//                else {
//                    labelMenuEntry.getIndicatorLabel().setText(" * ");
//                    if(okButton.isDisabled()) {
//                        okButton.setDisable(false);
//                    }
//                }
//            }
//            else {
//                if(okButton.isDisabled()) {
//                    okButton.setDisable(false);
//                }
//            }
        }
    };

    private ChangeListener<Boolean> selectedLabelHasChangedPropertyListener = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if(selectedLabel != null) {
                LabelMenuEntry labelMenuEntry = labelMenuEntries.get(selectedLabel);
                if(newValue == true) {
                    labelMenuEntry.getIndicatorLabel().setText(" * ");
                    okButton.setDisable(false);
                    cancelButton.setDisable(false);
                }
                else {
                    labelMenuEntry.getIndicatorLabel().setText("");
                    okButton.setDisable(true);
                    cancelButton.setDisable(true);
                }
            }
            else {
                okButton.setDisable(false);
                cancelButton.setDisable(false);
            }
        }
    };

    private void checkSelectedLabelHasChanged() {
        if(selectedLabel == null) {
            selectedLabelHasChanged.setValue(false);
        }
        else {
            boolean equals = selectedLabel.getColor().equals(labelColorPicker.getValue());
            selectedLabelHasChanged.setValue(!equals);
        }
    }

    private EventHandler<MouseEvent> newLabelHBoxClicked = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            if(selectedLabel != null) {
                selectHBox(newLabelHBox);
                selectedLabel = null;
                labelNameTextField.setText("");
                labelNameTextField.setDisable(false);
                labelColorPicker.setValue(null);
            }
        }
    };

    private EventHandler<MouseEvent> onLabelHBoxClicked = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            HBox targetedHBox = (HBox) event.getSource();
            LabelMenuEntry targetedLabelMenuEntry = null;
            for(LabelMenuEntry labelMenuEntry : labelMenuEntries.values()) {
                if(targetedHBox == labelMenuEntry.getContainer()) {
                    targetedLabelMenuEntry = labelMenuEntry;
                }
            }
            Label targetedLabel = targetedLabelMenuEntry.getLabel();
            selectHBox(targetedHBox);
            selectedLabel = targetedLabel;
            labelNameTextField.setText(targetedLabel.getLabel());
            labelNameTextField.setDisable(true);
            labelColorPicker.setValue(targetedLabel.getColor());
        }
    };

    private EventHandler<ActionEvent> onOkButtonAction = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            Graph graph = MainController.getInstance().getGraph();
            if(selectedLabel == null) {
                Label label = new Label(labelNameTextField.getText(), labelColorPicker.getValue());
                graph.addLabel(label);
                // Add an entry to the menu for the new label
                LabelMenuEntry labelMenuEntry = new LabelMenuEntry(label, onLabelHBoxClicked);
                labelsMenuContainer.getChildren().add(labelMenuEntry.getContainer());
                labelMenuEntries.put(label, labelMenuEntry);
                // Reset form values
                labelNameTextField.setText("");
                labelColorPicker.setValue(null);
            }
            else {
                // Update the selected label
                Label label = selectedLabel;
                // Only update the color when the color has actually changed. It avoid the refresh of the nodes with the new color
                if(!selectedLabel.getColor().equals(labelColorPicker.getValue())) {
                    selectedLabel.setColor(labelColorPicker.getValue());
                    graph.refreshNodeColorForLabel(label);
                }
                checkSelectedLabelHasChanged();
            }
        }
    };

    private EventHandler<ActionEvent> onCloseButtonAction = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            Stage stage = (Stage) root.getScene().getWindow();
            stage.close();
        }
    };

    public EventHandler<ActionEvent> onCancelButtonAction = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {

        }
    };

}
