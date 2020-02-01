package fr.leward.graphdesigner.graph;


public class RelationshipType {

    private String name;

    public RelationshipType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "[RelationshipType] " + name;
    }
}
