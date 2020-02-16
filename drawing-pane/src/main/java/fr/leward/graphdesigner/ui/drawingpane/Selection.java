package fr.leward.graphdesigner.ui.drawingpane;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

// TODO: This can be made observable
public class Selection {

    Set<Long> nodes = new HashSet<>();
    Set<Long> relationships = new HashSet<>();

    public Selection selectNode(long node) {
        nodes = Set.of(node);
        relationships = new HashSet<>();
        return this;
    }

    public Selection selectRelationship(long relationship) {
        nodes = new HashSet<>();
        relationships = Set.of(relationship);
        return this;
    }

    public Selection addNodeToSelection(long node) {
        nodes.add(node);
        return this;
    }

    public Selection addRelationshipToSelection(long relationship) {
        relationships.add(relationship);
        return this;
    }

    public boolean hasNode(long node) {
        return nodes.contains(node);
    }

    public boolean hasRelationship(long relationship) {
        return relationships.contains(relationship);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Selection selection = (Selection) o;
        return nodes.equals(selection.nodes) &&
                relationships.equals(selection.relationships);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodes, relationships);
    }
}
