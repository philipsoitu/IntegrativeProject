package com.coolawesome.integrativeproject;

import com.coolawesome.integrativeproject.utils.Constants;
import com.coolawesome.integrativeproject.utils.Vector3D;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class MainController {
    public static double g = 0.001;
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
    private Button playPauseBTN;
    @FXML
    private ChoiceBox<String> algoChoiceBox;

    @FXML
    private Button TextureBTN;

    @FXML
    private Button createBTN;

    @FXML
    private Slider massSLD;

    @FXML
    private TextField massTXTF;

    @FXML
    private Slider radiusSLD;

    @FXML
    private TextField radiusTXTF;

    @FXML
    private CheckBox sunCheckB;

    public AnchorPane viewport;
    private Simulation simulation;
    private int secondsElapsed = 0;
    Timeline timeline;
    private ObservableList<String> simulationListContent = FXCollections.observableArrayList(
            Constants.TIME_ELAPSED_PREFIX,
            Constants.PLANET_COUNT_PREFIX,
            Constants.AVERAGE_FORCE_PREFIX,
            Constants.NUMBER_OF_COLLISIONS_PREFIX
    );


    /*
    mass
    radius
    isSun
    import texture
     */

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

    void controllerSetup(Simulation simulation) {
        if (!isNull(simulation)) {
            this.simulation = simulation;
            updateSimInfo();
        } else {
            System.out.println("Simulation is null");
        }

        if (!simulationListContent.isEmpty()) {
            simulationInfoList.setItems(simulationListContent);
        } else {
            System.out.println("simulation list is empty");
        }

        initializeTime();
        sliderSetup();
    }

    public void sliderSetup() {
        //g constant
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

        //mass
        if (!isNull(massSLD) && !isNull(massTXTF)) {
            massSLD.valueProperty().addListener(((observableValue, oldValue, newValue) -> {
                String valueString = String.valueOf(newValue);
                int endIndex = Math.min(valueString.length(), 4);
                massTXTF.setText(valueString.substring(0, endIndex));
            }));

            massTXTF.textProperty().addListener((observableValue, oldValue, newValue) -> {
                if (!newValue.isEmpty() && !isValidDouble(newValue)) {
                    massTXTF.setText(oldValue);
                }
            });
        }

        //radius
        if (!isNull(radiusSLD) && !isNull(radiusTXTF)) {
            radiusSLD.valueProperty().addListener(((observableValue, oldValue, newValue) -> {
                String valueString = String.valueOf(newValue);
                int endIndex = Math.min(valueString.length(), 4);
                radiusTXTF.setText(valueString.substring(0, endIndex));
            }));

            radiusTXTF.textProperty().addListener((observableValue, oldValue, newValue) -> {
                if (!newValue.isEmpty() && !isValidDouble(newValue)) {
                    radiusTXTF.setText(oldValue);
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
        simulationListContent.set(1, Constants.PLANET_COUNT_PREFIX + getPlanetCount());
    }

    private void setInitialValues() {
        if (!isNull(gConstSLD)) {
            gConstSLD.setValue(g);
        } else {
            System.out.println("Gravity Constant Slider is null");
        }

        massSLD.setValue(0);
        massTXTF.setText("0");
        radiusSLD.setValue(0);
        radiusTXTF.setText("0");

        algoChoiceBox.getItems().addAll(Constants.ALGORITHM_CHOICES);

        algoChoiceBox.setValue(Constants.ALGORITHM_CHOICES[0]);

        gConstantTXTF.setText(g + "");
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

        simulationListContent.set(0, Constants.TIME_ELAPSED_PREFIX + String.format("%02d:%02d:%02d", hours, minutes, seconds));
    }

    private int getPlanetCount() {
        return simulation.planetMap.size();
    }

    private void updateGConst() {
        g = gConstSLD.getValue();
    }

    private boolean isNull(Object obj) {
        return obj == null;
    }

    @FXML
    void playPauseSim(ActionEvent event) {
        simulation.isPaused = !simulation.isPaused;

        if (simulation.isPaused) {
            System.out.println("Simulation is paused");
            playPauseBTN.setText("Play");
        } else {
            System.out.println("Simulation is playing");
            playPauseBTN.setText("Pause");
        }
    }

    @FXML
    void onTXTFUpdate(ActionEvent event) {
        Slider source = (Slider) event.getSource();

        if (source.equals(gConstSLD)) {
            try {
                gConstSLD.setValue(Double.parseDouble(gConstantTXTF.getText()));
                updateGConst();
            } catch (NumberFormatException e) {
                System.out.println("Input Corrected in G Constant Slider");
            }

        } else if (source.equals(massSLD)) {
            try {
                massSLD.setValue(Double.parseDouble(massTXTF.getText()));
            } catch (NumberFormatException e) {
                System.out.println("Input corrected in mass slider");
            }
        } else if (source.equals(radiusSLD)) {
            try {
                massSLD.setValue(Double.parseDouble(massTXTF.getText()));
                updateGConst();
            } catch (NumberFormatException e) {
                System.out.println("Input Corrected in mass Slider");
            }

        }
    }
    @FXML
    void createCustomPlanet(ActionEvent event) {

        double Vx = Math.random() * 2;
        double Vy = Math.random() * 2;
        double Vz = Math.random() * 2;

        Vector3D position = simulation.simulationView.getPositionInFrontOfCamera(50);
        Vector3D velocity = new Vector3D(Vx,Vy,Vz);

        double radius = Double.parseDouble(radiusTXTF.getText());
        double mass = Double.parseDouble(massTXTF.getText());
        boolean isSun = sunCheckB.isSelected();

        String uniqueID = UUID.randomUUID().toString().replaceAll("-", "");

        Planet planet = new Planet(uniqueID, position, velocity, radius, mass, isSun);

        simulation.planetMap.put(uniqueID, planet);

    }

    public void updateSelectedPlanetInfo(Planet planet) {
        ObservableList<String> planetInfo = FXCollections.observableArrayList(
                "ID: " + planet.name,
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

    public void saveJson(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Planet Data");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            simulation.saveToJson(file.getPath());
        }
    }

    public void loadJson(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Planet Data");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            simulation.loadFromJson(file.getPath());
        }
    }
}