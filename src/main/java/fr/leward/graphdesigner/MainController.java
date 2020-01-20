package fr.leward.graphdesigner;

import fr.leward.graphdesigner.event.*;
import fr.leward.graphdesigner.event.bus.EventConsumer;
import fr.leward.graphdesigner.event.bus.EventStreams;
import fr.leward.graphdesigner.event.handler.GlobalOnKeyPressedEventHandler;
import fr.leward.graphdesigner.graph.Graph;
import fr.leward.graphdesigner.graph.Node;
import fr.leward.graphdesigner.graph.Relationship;
import fr.leward.graphdesigner.state.AddNodeState;
import fr.leward.graphdesigner.state.StateManager;
import fr.leward.graphdesigner.ui.AddLabelComboBox;
import fr.leward.graphdesigner.ui.RightPaneUpdator;
import fr.leward.graphdesigner.ui.Selection;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    // Logger
//    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    private static MainController instance;
    private Graph graph;

    // UI Elements
    @FXML private Parent root;
    @FXML private Pane pane;
    @FXML private ToggleButton createNodeButton;
    @FXML private ToggleButton createRelationshipButton;
    @FXML private Button manageLabelsButton;
    @FXML private Button manageRelationshipsButton;
    @FXML private Label graphDataLabel;
    @FXML private VBox rightPane;

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
     * or actually ciecked
     */
    private boolean draggedNodeDetected = false;

    private StateManager stateManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Init State
        instance = this;
        graph = new Graph();
        stateManager = new StateManager();

        // Init some elements in the UI
        rightPaneUpdator = new RightPaneUpdator(rightPane, selection);

        // Register some events from UI
        createNodeButton.setOnAction(onCreateNodeButtonAction);
        createRelationshipButton.setOnAction(onCreateRelationshipButtonAction);
        manageLabelsButton.setOnAction(onManageLabelsButtonAction);
        manageRelationshipsButton.setOnAction(onManageRelationshipsButtonAction);
        pane.setOnMouseClicked(onPaneClicked);
        pane.setOnMouseMoved(onMouseMovedOnPane);

        // Register some events from Event Streams
        EventStreams.graphUpdatedEventStream.subscribe(graphUpdatedEventConsumer);
//        EventStreams.nodeSelectedEventStream.subscribe(nodeSelectedEventConsumer);
        EventStreams.labelAddedEventStream.subscribe(labelAddedEventEventConsumer);

        // Listen for keyboard keys
        root.setOnKeyPressed(new GlobalOnKeyPressedEventHandler());
    }

    private EventHandler<ActionEvent> onCreateNodeButtonAction = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
//            log.debug("onCreateNodeButtonAction (" + createNodeButton.isSelected() + ")");
            if(stateManager.getState() instanceof AddNodeState) {
                // Leave add node state
                EventStreams.leaveAddNodeStateEventStream.publish(new LeaveAddNodeStateEvent());
            } else {
                // Leave current state
                EventStreams.leaveCurrentStateEventStream.publish(new LeaveCurrentStateEvent());
                EventStreams.enterAddNodeStateEventStream.publish(new EnterAddNodeStateEvent());
                // Enter add node state
            }
        }
    };

    private EventHandler<ActionEvent> onCreateRelationshipButtonAction = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
//            log.debug("onCreateRelationshipButtonAction (" + createRelationshipButton.isSelected() + ")");
            if(createRelationshipButton.isSelected()) {
                EventStreams.leaveCurrentStateEventStream.publish(new LeaveCurrentStateEvent());
                EventStreams.enterAddRelationshipStateEventStream.publish(new EnterAddRelationshipStateEvent());
            }
            else {
                EventStreams.leaveCurrentStateEventStream.publish(new LeaveCurrentStateEvent());
            }
        }
    };

    private EventHandler<ActionEvent> onManageLabelsButtonAction = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            try {
                String fxmlFile = "/fxml/labels.fxml";
                FXMLLoader loader = new FXMLLoader();
                Parent modalRoot = (Parent) loader.load(getClass().getResourceAsStream(fxmlFile));
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initOwner(root.getScene().getWindow());
                stage.setTitle("Labels Manager");
                stage.setScene(new Scene(modalRoot, 600, 400));
                stage.setMinWidth(450.0);
                stage.setMinHeight(250.0);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

    private EventHandler<MouseEvent> onPaneClicked = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            EventStreams.graphPaneClickedEventStream.publish(new GraphPaneClickedEvent(event));
        }
    };

    private EventHandler<MouseEvent> onMouseMovedOnPane = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            EventStreams.graphPaneMouseMovedEventStream.publish(new GraphPaneMouseMovedEvent(event));
        }
    };

    public void leaveCurrentState() {
        stateManager.leaveCurrentState();
    }

    private EventConsumer<GraphUpdatedEvent> graphUpdatedEventConsumer = new EventConsumer<GraphUpdatedEvent>() {
        @Override
        public void consume(GraphUpdatedEvent event) {
            StringBuilder sb = new StringBuilder();
            sb.append("Graph data: " + graph.getNodes().size() + " node");
            if(graph.getNodes().size() > 1) {
                sb.append("s");
            }
            sb.append(" | " + graph.getRelationships().size() + " relationship");
            if(graph.getRelationships().size() > 1) {
                sb.append("s");
            }
            sb.append(" | " + graph.countLabels() + " label");
            if(graph.countLabels() > 1) {
                sb.append("s");
            }
            sb.append(" | " + graph.getRelationshipTypes().size() + " relationship type");
            if(graph.getRelationshipTypes().size() > 1) {
                sb.append("s");
            }
            graphDataLabel.setText(sb.toString());
        }
    };

    private EventConsumer<LabelAddedEvent> labelAddedEventEventConsumer = new EventConsumer<LabelAddedEvent>() {
        @Override
        public void consume(LabelAddedEvent event) {
            if(rightPanelLabelsComboBox != null) {
                rightPanelLabelsComboBox.getItems().add(event.getLabel());
            }
        }
    };

    /**
     * Delete the selected items
     */
    public void deleteSelection() {
        // When the selection is locked it should not be possible to remove selected items
        if(selection.isSelectionLocked()) {
            return;
        }

        // Delete selected nodes and associated relationships
        for(Node node : selection.getNodes()) {
            // Remove relationships attached to the node
            for(Relationship relationship : node.getInAndOutRelationships()) {
                pane.getChildren().removeAll(relationship.getArrow().getDrawableNodes());
                graph.getRelationships().remove(relationship);
                selection.remove(relationship);
            }
            pane.getChildren().remove(node.getCircle());
            graph.getNodes().remove(node);
            selection.remove(node);
        }

        // Delete selected relationships
        for(Relationship relationship : selection.getRelationships()) {
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

    public StateManager getStateManager() {
        return stateManager;
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

