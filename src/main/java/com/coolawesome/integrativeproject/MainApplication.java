package com.coolawesome.integrativeproject;

import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main application class that sets up and starts the JavaFX application.
 */
public class MainApplication extends Application {

    Timeline timeline;

    /**
     * The main entry point for all JavaFX applications.
     *
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
        launch();
    }

    /**
     * Starts and sets up the primary stage of the application.
     *
     * @param stage The primary stage for this application, onto which
     *              the application scene can be set.
     * @throws IOException if loading the FXML file fails.
     */
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);

        MainController controller = fxmlLoader.getController();

        AnchorPane viewport = (AnchorPane) scene.lookup("#viewport");
        Simulation simulation = new Simulation(viewport, controller);

        controller.controllerSetup(simulation);

        stage.setTitle("Space Sim");
        stage.setScene(scene);

        stage.setResizable(false);
        stage.show();

        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(new javafx.animation.KeyFrame(javafx.util.Duration.millis(16), e -> {
            simulation.update(0.016);
            controller.updateSimInfo();
        }));
        timeline.play();
    }
}