package fr.leward.graphdesigner.graph;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class Graph2 {

    private static final Logger log = LoggerFactory.getLogger(Graph2.class);

    private Set<Long> nodes = new HashSet<>();
    private Set<Relationship2> relationships = new HashSet<>();

    public Graph2() {
    }

    public void addNode(long id) {
        nodes.add(id);
        log.debug("Node {} added to graph (new node count: {})", id, countNodes());
    }

    public void addRelationship(long id, long start, long end) {
        var relationship = new Relationship2(id, start, end);
        relationships.add(relationship);
        log.debug("Relations {} ({} -> {}) added to graph (new rels count: {})", id, start, end, countRelationships());
    }

    public int countNodes() {
        return nodes.size();
    }

    public int countRelationships() {
        return relationships.size();
    }
}
