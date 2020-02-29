package fr.leward.graphdesigner;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

public class MainApp extends Application {

    private MainController controller;
    private Scene scene;

    private static final Logger log = LoggerFactory.getLogger(MainApp.class);

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    public void start(Stage stage) throws Exception {

        log.info("Starting Hello JavaFX and Maven demonstration application");

        String fxmlFile = "/fxml/main.fxml";
        log.debug("Loading FXML for main view from: {}", fxmlFile);
        FXMLLoader loader = new FXMLLoader();
        Parent rootNode = (Parent) loader.load(getClass().getResourceAsStream(fxmlFile));

        controller = loader.getController();

        log.debug("Showing JFX scene");
        scene = new Scene(rootNode, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());

        stage.setTitle("Graph Editor");
        stage.getIcons().add(new Image(getClass().getResource("/images/social_graph-256.png").toExternalForm()));
        stage.setScene(scene);
        stage.show();
    }

    public MainController getController() {
        return controller;
    }

    public Scene getScene() {
        return scene;
    }
}
