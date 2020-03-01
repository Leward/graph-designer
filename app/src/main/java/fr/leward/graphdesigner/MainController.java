package fr.leward.graphdesigner;

import fr.leward.graphdesigner.event.GraphUpdatedEvent;
import fr.leward.graphdesigner.event.LabelAddedEvent;
import fr.leward.graphdesigner.event.bus.EventConsumer;
import fr.leward.graphdesigner.event.bus.EventStreams;
import fr.leward.graphdesigner.graph.Graph;
import fr.leward.graphdesigner.graph.Graph2;
import fr.leward.graphdesigner.graph.Node;
import fr.leward.graphdesigner.graph.Relationship;
import fr.leward.graphdesigner.ui.AddLabelComboBox;
import fr.leward.graphdesigner.ui.RightPaneUpdator;
import fr.leward.graphdesigner.ui.Selection;
import fr.leward.graphdesigner.ui.drawingpane.DrawingPane;
import fr.leward.graphdesigner.ui.drawingpane.DrawingPaneMode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    // Logger
    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    private static MainController instance;
    private Graph graph;

    private Graph2 graph2 = new Graph2();

    // UI Elements
    @FXML
    private Parent root;
    @FXML
    private DrawingPane pane;
    @FXML
    private ToggleButton createNodeButton;
    @FXML
    private ToggleButton createRelationshipButton;
    @FXML
    private Button manageLabelsButton;
    @FXML
    private Button manageRelationshipsButton;
    @FXML
    private Label graphDataLabel;
    @FXML
    private VBox rightPane;

    private Button rightPaneAddLabelButton;
    private AddLabelComboBox rightPanelLabelsComboBox;

    /**
     * Current selection. Multiple elements can be selected at the same time.
     * The selection is Observable
     */
    private Selection selection = new Selection();

    /**
     * Responsible of updating the pane on the right: building and interacting
     * with the UI elements
     */
    private RightPaneUpdator rightPaneUpdator;

    /**
     * Used to keep track during click event if a node is being dragged
     * or actually clicked
     */
    private boolean draggedNodeDetected = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Init State
        instance = this;
        graph = new Graph();

        // Init some elements in the UI
        rightPaneUpdator = new RightPaneUpdator(rightPane, selection);

        // Register some events from UI
        createNodeButton.setOnAction(this::handleCreateNodeButtonAction);
        createRelationshipButton.setOnAction(this::handleCreateRelationshipButtonAction);
        manageLabelsButton.setOnAction(onManageLabelsButtonAction);
        manageRelationshipsButton.setOnAction(onManageRelationshipsButtonAction);

        // Register some events from Event Streams
        EventStreams.graphUpdatedEventStream.subscribe(graphUpdatedEventConsumer);
//        EventStreams.nodeSelectedEventStream.subscribe(nodeSelectedEventConsumer);
        EventStreams.labelAddedEventStream.subscribe(labelAddedEventEventConsumer);

        // Listen for keyboard keys
        root.setOnKeyPressed(this::handleKeyPressed);

        pane.setOnNodeDrawnHandler(event -> graph2.addNode(event.id));
        pane.setOnRelationshipDrawnHandler(event -> graph2.addRelationship(event.id, event.startNodeId, event.endNodeId));
    }

    private void handleKeyPressed(KeyEvent event) {
        if(event.getCode() == KeyCode.ESCAPE) {
            pane.leaveMode();
            createNodeButton.setSelected(false);
            createRelationshipButton.setSelected(false);
        }
        else if(event.getCode().equals(KeyCode.N)) {
            switchMode(DrawingPaneMode.ADD_NODE);
        }
        else if(event.getCode() == KeyCode.R) {
            switchMode(DrawingPaneMode.ADD_RELATIONSHIP);
        }
    }

    private void switchMode(DrawingPaneMode mode) {
        pane.switchMode(mode);
        resetAllToggleButtons(mode);
    }

    private void leaveMode() {
        pane.leaveMode();
        resetAllToggleButtons(pane.getMode());
    }

    private void resetAllToggleButtons(DrawingPaneMode targetMode) {
        if(targetMode == DrawingPaneMode.ADD_NODE) {
            createNodeButton.setSelected(true);
        }
        if(targetMode == DrawingPaneMode.ADD_RELATIONSHIP) {
            createRelationshipButton.setSelected(true);
        }
        if(targetMode != DrawingPaneMode.ADD_NODE) {
            createNodeButton.setSelected(false);
        }
        if(targetMode != DrawingPaneMode.ADD_RELATIONSHIP) {
            createRelationshipButton.setSelected(false);
        }
    }

    private void handleCreateNodeButtonAction(ActionEvent event) {
        createRelationshipButton.setSelected(false);
        if (pane.getMode() != DrawingPaneMode.ADD_NODE) {
            switchMode(DrawingPaneMode.ADD_NODE);
        } else {
            leaveMode();
        }
    }

    private void handleCreateRelationshipButtonAction(ActionEvent event) {
        createNodeButton.setSelected(false);
        if (pane.getMode() != DrawingPaneMode.ADD_RELATIONSHIP) {
            log.debug("Switching to mode {}", DrawingPaneMode.ADD_RELATIONSHIP);
            switchMode(DrawingPaneMode.ADD_RELATIONSHIP);
        } else {
            log.debug("Leaving mode {}", pane.getMode());
            leaveMode();
        }
    }

    private EventHandler<ActionEvent> onManageLabelsButtonAction = (ActionEvent event) -> {
        var labelsController = new LabelsController(graph);

        FXMLLoader loader = new FXMLLoader();
        loader.setController(labelsController);
        Parent modalRoot;
        try {
            modalRoot = loader.load(getClass().getResourceAsStream("/fxml/labels.fxml"));
        } catch (IOException e) {
            log.error("Can't load labels manager", e);
            return;
        }

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(root.getScene().getWindow());
        stage.setTitle("Labels Manager");
        stage.setScene(new Scene(modalRoot, 600, 400));
        stage.setMinWidth(450.0);
        stage.setMinHeight(250.0);
        stage.show();
    };

    private EventHandler<ActionEvent> onManageRelationshipsButtonAction = (event) -> {
        try {
            String fxmlFile = "/fxml/relationships.fxml";
            FXMLLoader loader = new FXMLLoader();
            Parent modalRoot = (Parent) loader.load(getClass().getResourceAsStream(fxmlFile));
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(root.getScene().getWindow());
            stage.setTitle("Relationships Manager");
            stage.setScene(new Scene(modalRoot, 600, 400));
            stage.setMinWidth(450.0);
            stage.setMinHeight(250.0);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    };

    public void leaveCurrentState() {
        pane.leaveMode();
    }

    private EventConsumer<GraphUpdatedEvent> graphUpdatedEventConsumer = new EventConsumer<GraphUpdatedEvent>() {
        @Override
        public void consume(GraphUpdatedEvent event) {
            StringBuilder sb = new StringBuilder();
            sb.append("Graph data: " + graph.getNodes().size() + " node");
            if (graph.getNodes().size() > 1) {
                sb.append("s");
            }
            sb.append(" | " + graph.getRelationships().size() + " relationship");
            if (graph.getRelationships().size() > 1) {
                sb.append("s");
            }
            sb.append(" | " + graph.countLabels() + " label");
            if (graph.countLabels() > 1) {
                sb.append("s");
            }
            sb.append(" | " + graph.getRelationshipTypes().size() + " relationship type");
            if (graph.getRelationshipTypes().size() > 1) {
                sb.append("s");
            }
            graphDataLabel.setText(sb.toString());
        }
    };

    private EventConsumer<LabelAddedEvent> labelAddedEventEventConsumer = new EventConsumer<LabelAddedEvent>() {
        @Override
        public void consume(LabelAddedEvent event) {
            if (rightPanelLabelsComboBox != null) {
                rightPanelLabelsComboBox.getItems().add(event.getLabel());
            }
        }
    };

    /**
     * Delete the selected items
     */
    public void deleteSelection() {
        // When the selection is locked it should not be possible to remove selected items
        if (selection.isSelectionLocked()) {
            return;
        }

        // Delete selected nodes and associated relationships
        for (Node node : selection.getNodes()) {
            // Remove relationships attached to the node
            for (Relationship relationship : node.getInAndOutRelationships()) {
                pane.getChildren().removeAll(relationship.getArrow().getDrawableNodes());
                graph.getRelationships().remove(relationship);
                selection.remove(relationship);
            }
            pane.getChildren().remove(node.getCircle());
            graph.getNodes().remove(node);
            selection.remove(node);
        }

        // Delete selected relationships
        for (Relationship relationship : selection.getRelationships()) {
            pane.getChildren().removeAll(relationship.getArrow().getDrawableNodes());
            graph.getRelationships().remove(relationship);
            selection.remove(relationship);
        }
    }


    //
    // Getters and Setters
    //

    public static MainController getInstance() {
        return instance;
    }

    public Graph getGraph() {
        return graph;
    }

    public int countNodes() {
        return graph2.countNodes();
    }

    public int countRelationships() {
        return graph2.countRelationships();
    }

    public Pane getPane() {
        return pane;
    }

    public Parent getRoot() {
        return root;
    }

    public ToggleButton getCreateNodeButton() {
        return createNodeButton;
    }

    public ToggleButton getCreateRelationshipButton() {
        return createRelationshipButton;
    }

    public Selection getSelection() {
        return selection;
    }

    public boolean isDraggedNodeDetected() {
        return draggedNodeDetected;
    }

    public void setDraggedNodeDetected(boolean draggedNodeDetected) {
        this.draggedNodeDetected = draggedNodeDetected;
    }
}
