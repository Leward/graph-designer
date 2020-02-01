package fr.leward.graphdesigner.shapes;

import javafx.scene.Node;

public interface ComplexShape {
    Iterable<Node> getDrawableNodes();
}
