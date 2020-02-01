module graph.designer {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.slf4j;

    opens fr.leward.graphdesigner to javafx.fxml;
    opens fr.leward.graphdesigner.debug to javafx.fxml;
    exports fr.leward.graphdesigner;
    exports fr.leward.graphdesigner.debug;
}