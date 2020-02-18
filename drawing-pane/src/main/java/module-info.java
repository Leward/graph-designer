module graph.designer.drawing.pane {
    requires graph.editor.core;
    requires javafx.controls;
    requires org.slf4j;
    requires graph.designer.style;

    exports fr.leward.graphdesigner.ui.drawingpane.demo;
    exports fr.leward.graphdesigner.ui.drawingpane;
    exports fr.leward.graphdesigner.ui.drawingpane.event;
    exports fr.leward.graphdesigner.ui.drawingpane.shape;
}