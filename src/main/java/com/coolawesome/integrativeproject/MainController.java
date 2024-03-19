package com.coolawesome.integrativeproject;

import com.coolawesome.integrativeproject.utils.Constants;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class MainController {

    @FXML
    private ListView<String> simulationInfoList;
    public AnchorPane viewport;
    private Simulation simulation;

    private long lastTime = 0;
    private int frameCount = 0;
    private int secondsElapsed = 0;


    private ObservableList<String> listContent = FXCollections.observableArrayList(
            Constants.frameRatePrefix, Constants.timeElapsedPrefix, Constants.planetCountPrefix
    );

    void controllerSetup(Simulation simulation) {

        if(simulation != null) {
            this.simulation = simulation;
            updateSimInfo();
        } else {
            throw new NullPointerException();
        }

        if(!listContent.isEmpty()) {
            simulationInfoList.setItems(listContent);
        } else {
            throw new IllegalStateException();
        }

        initializeTime();
        displayFrameRate();

    }

    public void updateSimInfo() {

        listContent.set(2, Constants.planetCountPrefix + getPlanetCount());

    }


    private void initializeTime() {

        Timeline timeline = new Timeline(new KeyFrame(
                Duration.seconds(1),
                event -> {
                    secondsElapsed++;
                    updateTimer();
                }
        ));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateTimer() {
        int hours = secondsElapsed / 3600;
        int minutes = (secondsElapsed % 3600) / 60;
        int seconds = secondsElapsed % 60;

        listContent.set(1, Constants.timeElapsedPrefix + String.format("%02d:%02d:%02d", hours, minutes, seconds));
    }

    private int getPlanetCount() {
        return simulation.planetMap.size();
    }

    private void displayFrameRate() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {

                if (now - lastTime >= 1_000_000_000) {
                    double frameRate = frameCount;
                    listContent.set(0, Constants.frameRatePrefix + frameRate);
                    frameCount = 0;
                    lastTime = now;
                }
                frameCount++;
            }
        };
        timer.start();
    }
}