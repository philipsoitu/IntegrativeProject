package com.coolawesome.integrativeproject;

import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);

        AnchorPane viewport = (AnchorPane) scene.lookup("#viewport");
        Simulation simulation = new Simulation(viewport);

        stage.setTitle("Space Sim");
        stage.setScene(scene);

        stage.setResizable(false);
        stage.show();

        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(new javafx.animation.KeyFrame(javafx.util.Duration.millis(16), e -> {
            simulation.update(0.016);
        }));
        timeline.play();

    }

    public static void main(String[] args) {
        launch();
    }
}