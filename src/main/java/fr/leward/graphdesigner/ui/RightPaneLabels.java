package fr.leward.graphdesigner.ui;

import fr.leward.graphdesigner.MainController;
import fr.leward.graphdesigner.graph.Graph;
import fr.leward.graphdesigner.graph.Node;
import fr.leward.graphdesigner.utils.ColorUtils;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class RightPaneLabels {

//    private static final Logger log = LoggerFactory.getLogger(RightPaneLabels.class);

    private Selection selection;
    private Button addLabelButton;
    private AddLabelComboBox addLabelComboBox; // This is actually a HBox

    private HBox body;

    /**
     * Contained within labelsRow
     */
    private HBox labelsContainer;

    /**
     * Contained within labelsRow
     */
    private HBox addLabelControlsContainer;

    public RightPaneLabels(Selection selection) {
        this.selection = selection;
    }

    public void build(Pane rightPane) {
        HBox labelsHeader = new HBox();
        labelsHeader.setAlignment(Pos.CENTER);
        Label label = new Label("Labels");
        label.setStyle("-fx-label-padding: 5px;");
        labelsHeader.getChildren().add(label);
        rightPane.getChildren().add(labelsHeader);

        body = new HBox();
        rightPane.getChildren().add(body);

        addLabelControlsContainer = new HBox(); // Definition need to be done before labelsContainer
        labelsContainer = new HBox();
        labelsContainer.setSpacing(10);
        buildUiLabels();
        body.getChildren().add(labelsContainer);
        selection.getLabelsInCommon().addListener(selectedLabelsChanged);


        addLabelButton = new Button("+");
        addLabelButton.setOnAction(addNewLabelButtonClickHandler);
        addLabelControlsContainer.getChildren().add(addLabelButton);
        body.getChildren().add(addLabelControlsContainer);
    }

    private Label buildUiLabelForNodeLabel(fr.leward.graphdesigner.graph.Label nodeLabel) {
        Label uiLabel = new Label(nodeLabel.getLabel());
        Color greyScaledNodeLabelColor = nodeLabel.getColor().grayscale();
        Color textColor = (greyScaledNodeLabelColor.getRed() > 0.5) ? Color.BLACK : Color.WHITE;
        uiLabel.setStyle("-fx-border-width: 1px; " +
                "-fx-border-radius: 5px; " +
                "-fx-background-radius: 5px;" +
                "-fx-border-style: solid;" +
                "-fx-border-color: darkgray;" +
                "-fx-pref-height: 35px; " +
                "-fx-padding: 5px; " +
                "-fx-background-color: " + ColorUtils.colorToCSSHexaCode(nodeLabel.getColor()) + ";" +
                "-fx-text-fill: " + ColorUtils.colorToCSSHexaCode(textColor) + ";");
        nodeLabel.getColor().grayscale().getRed();

        ContextMenu contextMenu = new ContextMenu();
        MenuItem makeMainLabelMenuItem = new MenuItem("Set a main label");
        makeMainLabelMenuItem.setOnAction((actionEvent) -> {
            for(Node node : selection.getNodes()) {
                node.setMainLabel(nodeLabel);
            }
        });
        contextMenu.getItems().addAll(makeMainLabelMenuItem);
        uiLabel.setContextMenu(contextMenu);

        return uiLabel;
    };

    private ChangeListener<fr.leward.graphdesigner.graph.Label> addLabelComboBoxChanged = (observable, oldValue, newValue) -> {
        if(newValue != null) {
            for (Node node : selection.getNodes()) {
                node.addLabel(newValue);
            }
        }
        addLabelControlsContainer.getChildren().clear();
        addLabelControlsContainer.getChildren().add(addLabelButton);
    };

    private EventHandler labelComboBoxCanceled = (event) -> {
        addLabelControlsContainer.getChildren().clear();
        addLabelControlsContainer.getChildren().add(addLabelButton);
    };

    private EventHandler<ActionEvent> addNewLabelButtonClickHandler = (event) -> {
        Graph graph = MainController.getInstance().getGraph();
        addLabelComboBox = new AddLabelComboBox(graph, selection, addLabelComboBoxChanged, labelComboBoxCanceled);
        addLabelControlsContainer.getChildren().clear();
        addLabelControlsContainer.getChildren().add(addLabelComboBox);
    };

    private void buildUiLabels() {
        labelsContainer.getChildren().clear();
        boolean moreThanOneLabel = false;
        for(fr.leward.graphdesigner.graph.Label nodeLabel : selection.getLabelsInCommon()) {
            moreThanOneLabel = true;
            labelsContainer.getChildren().add(buildUiLabelForNodeLabel(nodeLabel));
        }
        if(moreThanOneLabel) {
            addLabelControlsContainer.setPadding(new Insets(4, 0, 0, 8));
        }
        else {
            addLabelControlsContainer.setPadding(new Insets(4, 0, 0, 0));
        }
    }

    /**
     * The labels in the selection have changed
     */
    private InvalidationListener selectedLabelsChanged = (observable) -> {
        buildUiLabels();
    };


}
