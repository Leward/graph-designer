package fr.leward.graphdesigner.ui.drawingpane.demo;

import fr.leward.graphdesigner.core.IdGenerator;
import fr.leward.graphdesigner.ui.drawingpane.DrawingPane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class DrawingPaneDemo extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        AtomicInteger nextID = new AtomicInteger(0);
        IdGenerator generator = nextID::incrementAndGet;

        var drawingPane = new DrawingPane(generator); // this will the the root pane
        var scene = new Scene(drawingPane, 400, 400);

        String cssPath = "fr.leward.graphdesigner.styles/base-styles.css";
        URL cssResource = ClassLoader.getSystemResources(cssPath).nextElement();
        scene.getStylesheets().add(cssResource.toExternalForm());

        var a = drawingPane.addNode(30, 30);
        var b = drawingPane.addNode(150, 90);
        var c = drawingPane.addNode(135, 240);

        drawingPane.addRelationship(a, b);
        drawingPane.addRelationship(b, c);

        primaryStage.setTitle("Drawing Pane Demo");
        primaryStage.setScene(scene);

        primaryStage.show();
    }
}
