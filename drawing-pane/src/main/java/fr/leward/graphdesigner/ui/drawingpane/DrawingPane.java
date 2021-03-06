package fr.leward.graphdesigner.ui.drawingpane;

import fr.leward.graphdesigner.core.IdGenerator;
import fr.leward.graphdesigner.ui.drawingpane.event.NodeClickedEvent;
import fr.leward.graphdesigner.ui.drawingpane.event.NodeDrawnEvent;
import fr.leward.graphdesigner.ui.drawingpane.event.RelationshipClickedEvent;
import fr.leward.graphdesigner.ui.drawingpane.event.RelationshipDrawnEvent;
import fr.leward.graphdesigner.ui.drawingpane.shape.NodeShape;
import fr.leward.graphdesigner.ui.drawingpane.shape.RelationshipBeingDrawn;
import fr.leward.graphdesigner.ui.drawingpane.shape.RelationshipShape;
import javafx.collections.SetChangeListener;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class DrawingPane extends Pane implements SelectionTrait {

    private static final Logger LOGGER = LoggerFactory.getLogger(DrawingPane.class);

    private IdGenerator idGenerator;

    private DrawingPaneMode mode = DrawingPaneMode.DEFAULT;

    /**
     * Used when drawing a relationship ({@link #mode} == {@link DrawingPaneMode#ADD_RELATIONSHIP}) to keep track
     * of the first node having been selected, while proceeding to the selection of the next node.
     */
    private long startNode;

    private Map<Long, NodeShape> nodeShapes = new HashMap<>();

    Optional<RelationshipBeingDrawn> relationshipBeingDrawn = Optional.empty();

    private Set<RelationshipShape> relationshipShapes = new HashSet<>();

    /**
     * Handler to call when a node has been clicked.
     */
    private EventHandler<NodeClickedEvent> onNodeClickedHandler;

    /**
     * Handler to call when a relationship has been clicked.
     */
    private EventHandler<RelationshipClickedEvent> onRelationshipClickedHandler;

    /**
     * Handler to call when a new Node has been drawn by the user.
     */
    private EventHandler<NodeDrawnEvent> onNodeDrawnHandler;

    /**
     * Handler to call when a new Relationship has been drawn by the user.
     */
    private EventHandler<RelationshipDrawnEvent> onRelationshipDrawnHandler;

    private boolean ctrlKeyPressed;

    public DrawingPane() {
        var nextId = new AtomicLong(1);
        idGenerator = nextId::getAndIncrement;
        init();
    }

    public DrawingPane(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
        init();
    }

    private void init() {
        setFocusTraversable(true);
        setOnMouseClicked(this::handlePaneClick);
        setOnMouseMoved(this::handleMouseMoved);
        setOnKeyPressed(this::handleKeyPressed);
        setOnKeyReleased(this::handleKeyReleased);
        selection.addListener(this::handleSelectionChanged);
        requestFocus();
    }

    /**
     * Programmatically add a node to the pane (not drawn manually by the user).
     * The position of the node will be picked randomly in the visible screen.
     * <p>
     * Since the node is added programmatically, this will not raise a {@link NodeDrawnEvent}
     *
     * @return the ID the the new Node
     */
    public long addNode(double x, double y) {
        var nodeShape = new NodeShape(idGenerator.nextId(), x, y);
        nodeShapes.put(nodeShape.id, nodeShape);
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
        relationshipShapes.add(relationshipShape);
        getChildren().addAll(relationshipShape.getDrawables());
        relationshipShape.setOnRelationshipClicked(this::handleRelationshipClicked);
        return relationshipShape.id;
    }

    private NodeShape getNodeShape(long id) throws IllegalArgumentException {
        return findNode(nodeShape -> nodeShape.id == id)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Node of ID %d not found in Drawing Pane", id)));
    }

    public void switchMode(DrawingPaneMode mode) {
        this.mode = mode;

        // Reset some values
        startNode = 0;
        resetRelationshipBeingDrawn();
    }

    public void leaveMode() {
        this.switchMode(DrawingPaneMode.DEFAULT);
    }

    public void handleSelectionChanged(SetChangeListener.Change<? extends Long> change) {
        var added = change.getElementAdded();
        if (added != null) {
            findNode(nodeShape -> nodeShape.id == added).ifPresent(NodeShape::markSelected);
        }

        var removed = change.getElementRemoved();
        if (removed != null) {
            findNode(nodeShape -> nodeShape.id == removed).ifPresent(NodeShape::markSelected);
        }
    }

    public void handlePaneClick(MouseEvent event) {
        if (mode == DrawingPaneMode.ADD_NODE) {
            handlePaneClickInAddNodeMode(event);
        }
    }

    public void handleMouseMoved(MouseEvent event) {
        if (mode == DrawingPaneMode.ADD_RELATIONSHIP && startNode != 0) {
            relationshipBeingDrawn.ifPresent(it -> it.followMousePointer(event.getX(), event.getY(), nodeShapes.values()));
        }
    }

    private void handlePaneClickInAddNodeMode(MouseEvent event) {
        long id = addNode(event.getX(), event.getY());
        if (onNodeDrawnHandler != null) {
            onNodeDrawnHandler.handle(new NodeDrawnEvent(id));
        }
    }

    public void handleNodeClicked(NodeClickedEvent event) {
        LOGGER.debug("Node {} clicked (ctrlKeyPressed={}, currentMode={})", event.id, ctrlKeyPressed, mode);
        if (onNodeClickedHandler != null) {
            onNodeClickedHandler.handle(event);
        }

        switch (mode) {
            case DEFAULT:
                handleNodeClickInDefaultMode(event);
                break;
            case ADD_RELATIONSHIP:
                handleNodeClickInAddRelationshipMode(event);
                break;
        }
    }

    private void handleNodeClickInDefaultMode(NodeClickedEvent event) {
        if (ctrlKeyPressed) {
            addToSelection(event.id);
        } else {
            select(event.id);
        }
        updateNodeStyles();
    }

    private void handleNodeClickInAddRelationshipMode(NodeClickedEvent event) {
        if (startNode == 0) {
            startNode = event.id;
            initRelationshipBeingDrawn(getNodeShape(startNode));
        } else {
            long endNode = event.id;
            var id = addRelationship(startNode, endNode);
            startNode = 0;
            resetRelationshipBeingDrawn();
            if (onRelationshipDrawnHandler != null) {
                var relDrawnEvent = new RelationshipDrawnEvent(id, "DEFAULT", startNode, endNode);
                onRelationshipDrawnHandler.handle(relDrawnEvent);
            }
        }
    }

    public void updateNodeStyles() {
        findNodes(nodeShape -> selection.contains(nodeShape.id)).forEach(NodeShape::markSelected);
        findNodes(nodeShape -> !selection.contains(nodeShape.id)).forEach(NodeShape::markUnselected);
    }

    public void handleNodeDrag(NodeShape nodeShape, MouseEvent event) {
        // Calculate deltas
        double dx = event.getX() - nodeShape.getCenterX();
        double dy = event.getY() - nodeShape.getCenterY();
        nodeShape.setCenterX(event.getX());
        nodeShape.setCenterY(event.getY());

        if (event.isControlDown()) {
            selection.stream()
                    .filter(nodeShapes::containsKey)
                    .filter(selectedId -> !selectedId.equals(nodeShape.id))
                    .map(nodeShapes::get)
                    .forEach(selectedNodeShape -> {
                        selectedNodeShape.setCenterX(selectedNodeShape.getCenterX() + dx);
                        selectedNodeShape.setCenterY(selectedNodeShape.getCenterY() + dy);
                    });
        }
    }

    public void handleRelationshipClicked(RelationshipClickedEvent event) {
        if (onRelationshipClickedHandler != null) {
            onRelationshipClickedHandler.handle(event);
        }
    }

    public void handleKeyPressed(KeyEvent event) {
        LOGGER.debug("Key {} pressed", event.getCode());
        if (event.getCode() == KeyCode.CONTROL) {
            ctrlKeyPressed = true;
        }
    }

    public void handleKeyReleased(KeyEvent event) {
        LOGGER.debug("Key {} released", event.getCode());
        if (event.getCode() == KeyCode.CONTROL) {
            ctrlKeyPressed = false;
        }
    }

    // Helpers for Relationship Being Drawn

    private void resetRelationshipBeingDrawn() {
        relationshipBeingDrawn.ifPresent(it -> {
            getChildren().remove(it);
            relationshipBeingDrawn = Optional.empty();
        });
    }

    private void initRelationshipBeingDrawn(NodeShape startNodeShape) {
        relationshipBeingDrawn.ifPresent(it -> resetRelationshipBeingDrawn());
        relationshipBeingDrawn = Optional.of(new RelationshipBeingDrawn(startNodeShape));
        getChildren().add(relationshipBeingDrawn.get());
    }

    // Setters from External Event Handlers

    public void setOnNodeClickedHandler(EventHandler<NodeClickedEvent> onNodeClickedHandler) {
        this.onNodeClickedHandler = onNodeClickedHandler;
    }

    public void setOnRelationshipClickedHandler(EventHandler<RelationshipClickedEvent> onRelationshipClickedHandler) {
        this.onRelationshipClickedHandler = onRelationshipClickedHandler;
    }

    public void setOnNodeDrawnHandler(EventHandler<NodeDrawnEvent> onNodeDrawnHandler) {
        this.onNodeDrawnHandler = onNodeDrawnHandler;
    }

    public void setOnRelationshipDrawnHandler(EventHandler<RelationshipDrawnEvent> onRelationshipDrawnHandler) {
        this.onRelationshipDrawnHandler = onRelationshipDrawnHandler;
    }

    private Optional<NodeShape> findNode(Predicate<NodeShape> predicate) {
        return findNodes(predicate).findFirst();
    }

    private Stream<NodeShape> findNodes(Predicate<NodeShape> predicate) {
        return nodeShapes.values()
                .stream()
                .filter(predicate);
    }

    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    public DrawingPaneMode getMode() {
        return mode;
    }
}
