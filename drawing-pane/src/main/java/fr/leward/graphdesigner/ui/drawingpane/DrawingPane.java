package fr.leward.graphdesigner.ui.drawingpane;

import fr.leward.graphdesigner.core.IdGenerator;
import fr.leward.graphdesigner.ui.drawingpane.event.NodeClickedEvent;
import fr.leward.graphdesigner.ui.drawingpane.event.RelationshipClickedEvent;
import fr.leward.graphdesigner.ui.drawingpane.shape.NodeShape;
import fr.leward.graphdesigner.ui.drawingpane.shape.RelationshipShape;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.util.HashSet;
import java.util.Set;

public class DrawingPane extends Pane implements SelectionTrait {

    private IdGenerator idGenerator;

    private DrawingPaneMode mode = DrawingPaneMode.DEFAULT;

    /**
     * Used when drawing a relationship ({@link #mode} == {@link DrawingPaneMode#ADD_RELATIONSHIP}) to keep track
     * of the first node having been selected, while proceeding to the selection of the next node.
     */
    private long startNode;

    private Set<NodeShape> nodeShapes = new HashSet<>();

    /**
     * Handler to call when a node has been clicked.
     */
    private EventHandler<NodeClickedEvent> onNodeClickedHandler;

    /**
     * Handler to call when a relationship has been clicked.
     */
    private EventHandler<RelationshipClickedEvent> onRelationshipClickedHandler;

    private boolean ctrlKeyPressed;

    public DrawingPane(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
        setFocusTraversable(true);
        setOnMouseClicked(this::handlePaneClick);
        setOnKeyPressed(this::handleKeyPressed);
        setOnKeyReleased(this::handleKeyReleased);
        selection.addListener(this::handleSelectionChanged);
        requestFocus();
    }

    /**
     * Programmatically add a node to the pane (not drawn manually by the user).
     * The position of the node will be picked randomly in the visible screen.
     *
     * @return the ID the the new Node
     */
    public long addNode(double x, double y) {
        var nodeShape = new NodeShape(idGenerator.nextId(), x, y);
        nodeShapes.add(nodeShape);
        getChildren().add(nodeShape);
        nodeShape.setOnNodeClicked(this::handleNodeClicked);
        nodeShape.setOnMouseDragged(event -> this.handleNodeDrag(nodeShape, event));
        return nodeShape.id;
    }

    /**
     * Programmatically add a relationship to the pane (not drawn manually by the user)
     */
    public long addRelationship(long startId, long endId) {
        NodeShape startNode = getNodeShape(startId);
        NodeShape endNode = getNodeShape(endId);
        var relationshipShape = new RelationshipShape(idGenerator.nextId(), startNode, endNode);
        getChildren().addAll(relationshipShape.getDrawables());
        relationshipShape.setOnRelationshipClicked(this::handleRelationshipClicked);
        return relationshipShape.id;
    }

    private NodeShape getNodeShape(long id) throws IllegalArgumentException {
        return nodeShapes.stream()
                .filter(nodeShape -> nodeShape.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Node of ID %d not found in Drawing Pane", id)));
    }

    public void switchMode(DrawingPaneMode mode) {
        this.mode = mode;

        // Reset some values
        startNode = 0;
    }

    public void leaveMode() {
        this.switchMode(DrawingPaneMode.DEFAULT);
    }


    public void handleSelectionChanged(SetChangeListener.Change<? extends Long> change) {
        var added = change.getElementAdded();
        if (added != null) {
            nodeShapes.stream()
                    .filter(nodeShape -> nodeShape.id == added)
                    .findFirst()
                    .ifPresent(NodeShape::markSelected);
        }

        var removed = change.getElementRemoved();
        if (removed != null) {
            nodeShapes.stream()
                    .filter(nodeShape -> nodeShape.id == removed)
                    .findFirst()
                    .ifPresent(NodeShape::markUnselected);
        }
    }

    public void handlePaneClick(MouseEvent event) {
        if (mode == DrawingPaneMode.ADD_NODE) {
            addNode(event.getX(), event.getY());
        }
    }

    public void handleNodeClicked(NodeClickedEvent event) {
        if (onNodeClickedHandler != null) {
            onNodeClickedHandler.handle(event);
        }

        if (mode == DrawingPaneMode.DEFAULT) {
            if (ctrlKeyPressed) {
                addToSelection(event.id);
            } else {
                select(event.id);
            }
            updateNodeStyles();
        }

        if (mode == DrawingPaneMode.ADD_RELATIONSHIP) {
            if (startNode == 0) {
                startNode = event.id;
            } else {
                long endNode = event.id;
                addRelationship(startNode, endNode);
            }
        }
    }

    public void updateNodeStyles() {
        nodeShapes.stream()
                .filter(nodeShape -> selection.contains(nodeShape.id))
                .forEach(NodeShape::markSelected);
        nodeShapes.stream()
                .filter(nodeShape -> !selection.contains(nodeShape.id))
                .forEach(NodeShape::markUnselected);
    }

    public void handleNodeDrag(NodeShape nodeShape, MouseEvent event) {
        nodeShape.setCenterX(event.getX());
        nodeShape.setCenterY(event.getY());
    }

    public void handleRelationshipClicked(RelationshipClickedEvent event) {
        if (onRelationshipClickedHandler != null) {
            onRelationshipClickedHandler.handle(event);
        }
    }

    public void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.CONTROL) {
            ctrlKeyPressed = true;
        }
    }

    public void handleKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.CONTROL) {
            ctrlKeyPressed = false;
        }
    }

    // Setters from External Event Handlers

    public void setOnNodeClickedHandler(EventHandler<NodeClickedEvent> onNodeClickedHandler) {
        this.onNodeClickedHandler = onNodeClickedHandler;
    }

    public void setOnRelationshipClickedHandler(EventHandler<RelationshipClickedEvent> onRelationshipClickedHandler) {
        this.onRelationshipClickedHandler = onRelationshipClickedHandler;
    }
}
