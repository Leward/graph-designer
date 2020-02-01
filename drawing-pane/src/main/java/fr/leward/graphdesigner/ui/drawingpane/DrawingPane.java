package fr.leward.graphdesigner.ui.drawingpane;

import fr.leward.graphdesigner.core.IdGenerator;
import fr.leward.graphdesigner.ui.drawingpane.shape.NodeShape;
import fr.leward.graphdesigner.ui.drawingpane.shape.RelationshipShape;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.util.HashSet;
import java.util.Set;

// TODO: Decouple so that it can be tested

public class DrawingPane extends Pane {

    private IdGenerator idGenerator;

    private DrawingPaneMode mode = DrawingPaneMode.DEFAULT;

    // TODO: Selection

    // TODO: Start Node and End Node for relationship

    private Set<NodeShape> nodeShapes = new HashSet<>();

    public DrawingPane(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
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
        getChildren().addAll(nodeShape.getDrawables());
        return nodeShape.id;
    }

    /**
     * Add a node from a user click in the pane
     **/
    private void addNode(MouseEvent event) {
    }

    /**
     * Programmatically add a relationship to the pane (not drawn manually by the user)
     */
    public void addRelationship(long startId, long endId) {
        NodeShape startNode = getNodeShape(startId);
        NodeShape endNode = getNodeShape(endId);
        var relationshipShape = new RelationshipShape(idGenerator.nextId(), startNode, endNode);
        getChildren().addAll(relationshipShape.getDrawables());
    }

    private NodeShape getNodeShape(long id) throws IllegalArgumentException {
        return nodeShapes.stream()
                .filter(nodeShape -> nodeShape.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Node of ID %d not found in Drawing Pane", id)));
    }

    public void switchMode(DrawingPaneMode mode) {
        this.mode = mode;
    }

    public void handlePaneClick(MouseEvent event) {
        if (mode == DrawingPaneMode.ADD_NODE) {
            addNode(event);
        }
    }

    public void handleNodeClick() {
        boolean isCtrlClick = false;
    }

    public void handleNodeClick(MouseEvent event) {
    }

    public void handleNodeDragged() {
    }

    public void handleRelationshipDragged() {
    }

    public void handleMouseMoved(MouseEvent event) {
    }

}
