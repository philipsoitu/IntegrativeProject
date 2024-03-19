package com.coolawesome.integrativeproject;

import com.coolawesome.integrativeproject.utils.Constants;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

/*
TODO

exception handling
make sure that frame rate and g constant manipulation is accurate
fix focusing bug where once you click anywhere you can no longer move the camera
 */

public class MainController {
    public static double G = 0.00001;
    public static double timeStep = 0.016;
    @FXML
    private ListView<String> simulationInfoList;
    @FXML
    private Slider GConstSLD;
    @FXML
    private TextField GConstantTXTF;
    @FXML
    private Slider timeStepSLD;
    @FXML
    private TextField timeStepTXTF;
    @FXML
    private Button pauseBTN;
    @FXML
    private Button playBTN;
    @FXML
    private ChoiceBox<String> algoChoiceBox;
    public AnchorPane viewport;
    private Simulation simulation;
    private long lastTime = 0;
    private int frameCount = 0;
    private int secondsElapsed = 0;
    private MainApplication main;
    private AnimationTimer timer;
    Timeline timeline;


    private ObservableList<String> listContent = FXCollections.observableArrayList(
            Constants.frameRatePrefix, Constants.timeElapsedPrefix, Constants.planetCountPrefix
    );

    void controllerSetup(Simulation simulation, MainApplication main) {

        if(simulation != null) {
            this.simulation = simulation;
            updateSimInfo();
        } else {
            throw new NullPointerException();
        }

        if(main != null) {
            this.main = main;
        } else {
            throw new NullPointerException();
        }

        if(!listContent.isEmpty()) {
            simulationInfoList.setItems(listContent);
        } else {
            throw new IllegalStateException();
        }

        playBTN.setDisable(true);

        algoChoiceBox.getItems().addAll(Constants.algorithms);

        algoChoiceBox.setValue(Constants.algorithms[0]);

        initializeTime();
        displayFrameRate();
        sliderSetup();
    }

    public void sliderSetup() {

        if(timeStepSLD != null) {
            timeStepSLD.valueProperty().addListener(((observableValue, oldValue, newValue) -> {
                String valueString = String.valueOf(newValue);
                int endIndex = Math.min(valueString.length(), 4);
                timeStepTXTF.setText(valueString.substring(0, endIndex));
                updateTimeStep();
            }));
        }

        if(GConstSLD != null) {
            GConstSLD.valueProperty().addListener(((observableValue, oldValue, newValue) -> {
                String valueString = String.valueOf(newValue);
                int endIndex = Math.min(valueString.length(), 5);
                GConstantTXTF.setText(valueString.substring(0, endIndex));
                updateGConst();
            }));
        }

        //initial values


            GConstSLD.setValue(G);
            GConstantTXTF.setText(G+"");

            timeStepSLD.setValue(timeStep);
            timeStepTXTF.setText(timeStep+"");




    }

    public void updateSimInfo() {
        listContent.set(2, Constants.planetCountPrefix + getPlanetCount());
    }


    private void initializeTime() {

         timeline = new Timeline(new KeyFrame(
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

    //needs to be redone
    private void displayFrameRate() {
         timer = new AnimationTimer() {
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

    private void updateGConst() {
        G = GConstSLD.getValue();
    }

    private void updateTimeStep() {
        timeStep = timeStepSLD.getValue();
    }

    @FXML
    void pauseSim(ActionEvent event) {
        main.timeline.pause();
        timer.stop();
        timeline.pause();
        pauseBTN.setDisable(true);
        playBTN.setDisable(false);
    }

    @FXML
    void playSim(ActionEvent event) {
        main.timeline.play();
        timer.start();
        timeline.play();
        playBTN.setDisable(true);
        pauseBTN.setDisable(false);
    }

    @FXML
    void updateGConstSLD(ActionEvent event) {
        GConstSLD.setValue(Double.parseDouble(GConstantTXTF.getText()));
        updateGConst();
    }

    @FXML
    void updateTimeStepSLD(ActionEvent event) {
        timeStepSLD.setValue(Double.parseDouble(timeStepTXTF.getText()));
        updateTimeStep();
    }
}