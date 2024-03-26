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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
    TODO

    redo frame rate

    ideas:

    time until next collision
    number of collisions
    average force done by planets

 */

public class MainController {
    public static double g = 0.001;
    public static double timeStep = 0.016;
    @FXML
    public ListView<String> selectedPlanetInfoList;
    @FXML
    public Button viewRandBtn;
    @FXML
    private ListView<String> simulationInfoList;
    @FXML
    private Slider gConstSLD;
    @FXML
    private TextField gConstantTXTF;
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
    private ObservableList<String> simulationListContent = FXCollections.observableArrayList(
            Constants.frameRatePrefix, Constants.timeElapsedPrefix, Constants.planetCountPrefix
    );

    @FXML
    public void initialize() {
        viewport.sceneProperty().addListener((observableScene, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.focusOwnerProperty().addListener((observable, oldFocusOwner, newFocusOwner) -> {
                    if (newFocusOwner != viewport) {
                        viewport.requestFocus();
                    }
                });
            }
        });
    }

    void controllerSetup(Simulation simulation, MainApplication main) {

        if (!isNull(simulation)) {
            this.simulation = simulation;
            updateSimInfo();
        } else {
            throw new NullPointerException();
        }

        if (!isNull(main)) {
            this.main = main;
        } else {
            throw new NullPointerException();
        }

        if (!simulationListContent.isEmpty()) {
            simulationInfoList.setItems(simulationListContent);
        } else {
            throw new IllegalStateException();
        }

        initializeTime();
        displayFrameRate();
        sliderSetup();
    }

    public void sliderSetup() {

        if (!isNull(timeStepSLD) && !isNull(timeStepTXTF)) {
            timeStepSLD.valueProperty().addListener(((observableValue, oldValue, newValue) -> {
                String valueString = String.valueOf(newValue);
                int endIndex = Math.min(valueString.length(), 5);
                timeStepTXTF.setText(valueString.substring(0, endIndex));
                updateTimeStep();
            }));

            //does not allow invalid inputs to be typed
            timeStepTXTF.textProperty().addListener((observableValue, oldValue, newValue) -> {
                if (!timeStepTXTF.getText().isEmpty() && !isValidDouble(newValue)) {

                    try {
                        timeStepTXTF.setText(oldValue);
                        if (Double.parseDouble(newValue) < Double.parseDouble(String.valueOf(timeStepSLD.getMax()))) {
                            timeStepTXTF.setText(String.valueOf(timeStepSLD.getMax()));
                        } else if (Double.parseDouble(newValue) > Double.parseDouble(String.valueOf(timeStepSLD.getMin()))) {
                            timeStepTXTF.setText(String.valueOf(timeStepSLD.getMin()));
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Input Corrected");
                    }
                }
            });
        }

        if (!isNull(gConstSLD) && !isNull(gConstantTXTF)) {
            gConstSLD.valueProperty().addListener(((observableValue, oldValue, newValue) -> {
                String valueString = String.valueOf(newValue);
                int endIndex = Math.min(valueString.length(), 4);
                gConstantTXTF.setText(valueString.substring(0, endIndex));
                updateGConst();
            }));

            gConstantTXTF.textProperty().addListener((observableValue, oldValue, newValue) -> {
                if (!newValue.isEmpty() && !isValidDouble(newValue)) {
                    gConstantTXTF.setText(oldValue);
                }
            });
        }
        setInitialValues();
    }

    private boolean isValidDouble(String str) {
        if (isNull(str)) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            System.out.println("Input Corrected");
            return false;
        }

    }

    public void updateSimInfo() {
        simulationListContent.set(2, Constants.planetCountPrefix + getPlanetCount());
    }

    private void setInitialValues() {
        if (gConstSLD != null) {
            gConstSLD.setValue(g);
        } else {
            throw new NullPointerException();
        }

        if (playBTN != null) {
            playBTN.setDisable(true);
        }

        algoChoiceBox.getItems().addAll(Constants.algorithms);

        algoChoiceBox.setValue(Constants.algorithms[0]);

        gConstantTXTF.setText(g + "");

        timeStepSLD.setValue(timeStep);

        timeStepTXTF.setText(timeStep + "");
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

        simulationListContent.set(1, Constants.timeElapsedPrefix + String.format("%02d:%02d:%02d", hours, minutes, seconds));
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
                    simulationListContent.set(0, Constants.frameRatePrefix + frameRate);
                    frameCount = 0;
                    lastTime = now;
                }
                frameCount++;
            }
        };
        timer.start();
    }

    private void updateGConst() {
        g = gConstSLD.getValue();
    }

    private void updateTimeStep() {
        timeStep = timeStepSLD.getValue();
    }

    private boolean isNull(Object obj) {
        return obj == null;
    }

    @FXML
    void pauseSim(ActionEvent event) {
        if (!isNull(main.timeline)) {
            main.timeline.pause();
            timer.stop();
            timeline.pause();
            pauseBTN.setDisable(true);
            playBTN.setDisable(false);
        } else {
            throw new NullPointerException();
        }
    }

    @FXML
    void playSim(ActionEvent event) {
        if (isNull(main.timeline)) {
            main.timeline.play();
            timer.start();
            timeline.play();
            playBTN.setDisable(true);
            pauseBTN.setDisable(false);
        } else {
            throw new NullPointerException();
        }
    }

    @FXML
    void updateGConstSLD(ActionEvent event) {
        try {
            gConstSLD.setValue(Double.parseDouble(gConstantTXTF.getText()));
            updateGConst();
        } catch (NumberFormatException e) {
            System.out.println("Input Corrected in G Constant Slider");
        }
    }

    @FXML
    void updateTimeStepSLD(ActionEvent event) {
        try {
            timeStepSLD.setValue(Double.parseDouble(timeStepTXTF.getText()));
            updateTimeStep();

        } catch (NumberFormatException e) {
            System.out.println("Input Corrected in Time Step Slider");
        }
    }

    public void updateSelectedPlanetInfo(Planet planet) {
        ObservableList<String> planetInfo = FXCollections.observableArrayList(
                "Position: " + String.format("%.3f, %.3f, %.3f", planet.position.x, planet.position.y, planet.position.z),
                "Velocity: " + String.format("%.3f, %.3f, %.3f", planet.velocity.x, planet.velocity.y, planet.velocity.z),
                "Accel: " + String.format("%.1e, %.1e, %.1e", planet.acceleration.x, planet.acceleration.y, planet.acceleration.z),
                "Radius: " + String.format("%.3f", planet.radius),
                "Mass: " + String.format("%.3f", planet.mass),
                "Color: " + planet.color
        );
        selectedPlanetInfoList.setItems(planetInfo);
    }

    @FXML
    public void viewRandPlanet(ActionEvent event) {
        // Get a random planet from the planetMap
        List<String> keys = new ArrayList<>(simulation.planetMap.keySet());
        String randomKey = keys.get(new Random().nextInt(keys.size()));
        Planet randomPlanet = simulation.planetMap.get(randomKey);

        // Update the selected planet info
        updateSelectedPlanetInfo(randomPlanet);

        // Update the current camera planet in the SimulationView
        simulation.simulationView.setCurrentCamPlanetID(randomKey);
    }
}