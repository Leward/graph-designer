package fr.leward.graphdesigner;

import fr.leward.graphdesigner.event.*;
import fr.leward.graphdesigner.event.bus.EventConsumer;
import fr.leward.graphdesigner.event.bus.EventStreams;
import fr.leward.graphdesigner.graph.Graph;
import fr.leward.graphdesigner.graph.Node;
import fr.leward.graphdesigner.state.AddNodeState;
import fr.leward.graphdesigner.state.DefaultState;
import fr.leward.graphdesigner.state.StateManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
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

    // UI Elements
    @FXML private Parent root;
    @FXML private Pane pane;
    @FXML private ToggleButton createNodeButton;
    @FXML private ToggleButton createRelationshipButton;
    @FXML private Button manageLabelsButton;
    @FXML private Label graphDataLabel;
    @FXML private VBox rightPane;
    private ComboBox<fr.leward.graphdesigner.graph.Label> rightPanelLabelsComboBox;

    private StateManager stateManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Init State
        instance = this;
        graph = new Graph();
        stateManager = new StateManager();

        // Init some elements in the UI
        restoreRightPaneToEmpty();

        // Register some events from UI
        createNodeButton.setOnAction(onCreateNodeButtonAction);
        createRelationshipButton.setOnAction(onCreateRelationshipButtonAction);
        manageLabelsButton.setOnAction(onManageLabelsButtonAction);
        pane.setOnMouseClicked(onPaneClicked);
        pane.setOnMouseMoved(onMouseMovedOnPane);

        // Register some events from Event Streams
        EventStreams.graphUpdatedEventStream.subscribe(graphUpdatedEventConsumer);
        EventStreams.nodeSelectedEventStream.subscribe(nodeSelectedEventConsumer);
        EventStreams.labelAddedEventStream.subscribe(labelAddedEventEventConsumer);
    }

    private EventHandler<ActionEvent> onCreateNodeButtonAction = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            log.debug("onCreateNodeButtonAction (" + createNodeButton.isSelected() + ")");
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
            log.debug("onCreateRelationshipButtonAction (" + createRelationshipButton.isSelected() + ")");
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

    private EventHandler<MouseEvent> onPaneClicked = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            log.debug("onPaneClicked");
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
            graphDataLabel.setText(sb.toString());
        }
    };

    private EventConsumer<NodeSelectedEvent> nodeSelectedEventConsumer = new EventConsumer<NodeSelectedEvent>() {
        @Override
        public void consume(NodeSelectedEvent event) {
            updateRightPane();
            // TODO: Reimplement here
//            HBox firstLine = new HBox();
//            firstLine.setAlignment(Pos.CENTER_LEFT);
//
//            Label label = new Label("Label: ");
//            firstLine.getChildren().add(label);
//
//            ObservableList observableList = FXCollections.observableList(graph.getLabels());
//            rightPanelLabelsComboBox = new ComboBox(observableList);
//            final Node selectedNode = ((DefaultState) stateManager.getState()).getSelectedNode();
//            rightPanelLabelsComboBox.setValue(selectedNode.getLabel());
//
//            rightPanelLabelsComboBox.valueProperty().addListener(new ChangeListener<fr.leward.graphdesigner.graph.Label>() {
//                @Override
//                public void changed(ObservableValue<? extends fr.leward.graphdesigner.graph.Label> observable, fr.leward.graphdesigner.graph.Label oldValue, fr.leward.graphdesigner.graph.Label newValue) {
//                    selectedNode.setLabel(newValue);
//                }
//            });
//            firstLine.getChildren().add(rightPanelLabelsComboBox);
//
//            rightPane.getChildren().clear();
//            rightPane.getChildren().add(firstLine);
        }
    };

    private EventConsumer<NodeUnselectedEvent> nodeUnselectedEventConsumer = new EventConsumer<NodeUnselectedEvent>() {
        @Override
        public void consume(NodeUnselectedEvent event) {
            updateRightPane();
        }
    };

    private void updateRightPane() {
        final DefaultState defaultState = (stateManager.getState() instanceof DefaultState) ? (DefaultState) stateManager.getState() : null;
        boolean noNodeSelected = defaultState == null || defaultState.getSelectedNodes().size() == 0;
        if(noNodeSelected) {
            restoreRightPaneToEmpty();
            return;
        }

        HBox firstLine = new HBox();
        firstLine.setAlignment(Pos.CENTER_LEFT);

        Label label = new Label("Label: ");
        firstLine.getChildren().add(label);

        ObservableList observableList = FXCollections.observableList(graph.getLabels());
        rightPanelLabelsComboBox = new ComboBox(observableList);
//      final Node selectedNode = ((DefaultState) stateManager.getState()).getSelectedNode();
//      rightPanelLabelsComboBox.setValue(selectedNode.getLabel());

        rightPanelLabelsComboBox.valueProperty().addListener(new ChangeListener<fr.leward.graphdesigner.graph.Label>() {
            @Override
            public void changed(ObservableValue<? extends fr.leward.graphdesigner.graph.Label> observable, fr.leward.graphdesigner.graph.Label oldValue, fr.leward.graphdesigner.graph.Label newValue) {
                for(Node selectedNode : defaultState.getSelectedNodes()) {
                    selectedNode.setLabel(newValue);
                }
            }
        });
        firstLine.getChildren().add(rightPanelLabelsComboBox);

        // Replace the right pane with the UI elements we just built
        rightPane.getChildren().clear();
        rightPane.getChildren().add(firstLine);
    };

    private EventConsumer<LabelAddedEvent> labelAddedEventEventConsumer = new EventConsumer<LabelAddedEvent>() {
        @Override
        public void consume(LabelAddedEvent event) {
            if(rightPanelLabelsComboBox != null) {
                rightPanelLabelsComboBox.getItems().add(event.getLabel());
            }
        }
    };

    private void restoreRightPaneToEmpty() {
        rightPane.getChildren().clear();
        Label label = new Label("Select a node or a relationship");
        rightPane.getChildren().add(label);
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

    public ToggleButton getCreateNodeButton() {
        return createNodeButton;
    }

    public ToggleButton getCreateRelationshipButton() {
        return createRelationshipButton;
    }

}

