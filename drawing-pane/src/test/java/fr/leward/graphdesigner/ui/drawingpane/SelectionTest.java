package fr.leward.graphdesigner.ui.drawingpane;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SelectionTest {

    @Test
    void testEmptyEquality() {
        assertEquals(new Selection(), new Selection());
    }

    @Test
    void testSimpleEqual() {
        assertEquals(new Selection().selectNode(5), new Selection().selectNode(5));
        assertEquals(new Selection().selectRelationship(4), new Selection().selectRelationship(4));

        assertNotEquals(new Selection().selectNode(5), new Selection().selectRelationship(5));
    }

    @Test
    void testSingleSelect() {
        var selection = new Selection();
        assertFalse(selection.hasNode(5));

        selection.selectNode(5);
        assertTrue(selection.hasNode(5));

        selection.selectNode(3);
        assertFalse(selection.hasNode(5));
        assertTrue(selection.hasNode(3));

        selection.selectRelationship(2);
        assertFalse(selection.hasNode(3));
        assertTrue(selection.hasRelationship(2));
    }

    @Test
    void testMultiSelect() {
        var selection = new Selection()
                .addNodeToSelection(1)
                .addNodeToSelection(2)
                .addRelationshipToSelection(1);

        assertTrue(selection.hasNode(1));
        assertTrue(selection.hasNode(2));
        assertTrue(selection.hasRelationship(1));
    }
}