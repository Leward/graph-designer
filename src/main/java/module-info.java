module graph.designer {
    requires javafx.controls;
    requires javafx.fxml;
//    requires org.slf4j;
//    requires org.apache.logging.log4j;

    opens fr.leward.graphdesigner to javafx.fxml;
    exports fr.leward.graphdesigner;
}