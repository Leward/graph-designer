package fr.leward.graphdesigner.debug;

import fr.leward.graphdesigner.LabelsController;
import fr.leward.graphdesigner.graph.Graph;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LabelManagerDemo extends Application {

    private static final Logger log = LoggerFactory.getLogger(LabelManagerDemo.class);

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    public void start(Stage stage) throws Exception {
        Graph graph = new Graph();
        LabelsController controller = new LabelsController(graph);

        FXMLLoader loader = new FXMLLoader();
        loader.setController(controller);
        Parent modalRoot = loader.load(getClass().getResourceAsStream("/fxml/labels.fxml"));

        Scene scene = new Scene(modalRoot, 600, 400);
        scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Labels Manager");
        stage.setMinWidth(450.0);
        stage.setMinHeight(250.0);
        stage.show();
    }
}
