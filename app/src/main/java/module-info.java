module graph.designer.app {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.slf4j;

    requires graph.editor.core;
    requires graph.designer.drawing.pane;

    opens fr.leward.graphdesigner to javafx.fxml;
    opens fr.leward.graphdesigner.debug to javafx.fxml;
    exports fr.leward.graphdesigner;
    exports fr.leward.graphdesigner.debug;
}