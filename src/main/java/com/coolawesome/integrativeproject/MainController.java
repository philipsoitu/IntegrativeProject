package com.coolawesome.integrativeproject;

import com.coolawesome.integrativeproject.utils.AverageColourGenerator;
import com.coolawesome.integrativeproject.utils.Constants;
import com.coolawesome.integrativeproject.utils.Vector3D;
import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.PerspectiveCamera;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileNotFoundException;
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
    private Button textureBTN;
    @FXML
    private Button resetTextureBTN;
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

    @FXML
    private AnchorPane previewViewport;

    @FXML
    private Button createBTN;

    @FXML
    private Button spawnRandomPlanetBTN;

    @FXML
    private Button originBTN;

    @FXML
    public Label xPosLBL;

    @FXML
    public Label yPosLBL;

    @FXML
    public Label zPosLBL;

    @FXML
    private Slider thetaSLD;

    @FXML
    private TextField thetaTXTF;

    File SelectedImgFile;
    Image customTexture;
    public AnchorPane viewport;
    private Simulation simulation;
    private int secondsElapsed = 0;
    private Sphere previewSphere;
    private PhongMaterial previewSphereMaterial;
    Timeline timeline;
    private final ObservableList<String> simulationListContent = FXCollections.observableArrayList(Constants.TIME_ELAPSED_PREFIX, Constants.PLANET_COUNT_PREFIX, Constants.NUMBER_OF_COLLISIONS_PREFIX);
    public final Image defaultCustomPlanetTexture = new Image(getClass().getResourceAsStream(Constants.defaultCustomPlanetTextureFilePath));

    Color defaultColor;

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
        previewSetup();
    }

    void previewSetup() {
        PerspectiveCamera camera = new PerspectiveCamera(true);

        previewSphere = new Sphere();

        defaultColor = AverageColourGenerator.getAverageColor(defaultCustomPlanetTexture);

        previewSphereMaterial = new PhongMaterial(defaultColor);
        previewSphere.setTranslateX(previewViewport.getPrefWidth() / 2);
        previewSphere.setTranslateY(previewViewport.getPrefHeight() / 2);
        previewSphere.setMaterial(previewSphereMaterial);
        previewSphereMaterial.setDiffuseMap(defaultCustomPlanetTexture);

        AmbientLight ambientLight = new AmbientLight(Color.rgb(255, 255, 255, 0.5));

        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(20), previewSphere);
        rotateTransition.setByAngle(-360);
        rotateTransition.setCycleCount(RotateTransition.INDEFINITE);
        rotateTransition.setAutoReverse(false);

        rotateTransition.setAxis(new Point3D(-1, 5, 0).normalize());

        rotateTransition.play();

        previewViewport.getChildren().addAll(previewSphere, camera, ambientLight);
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

        listenerSetup(gConstantTXTF, gConstSLD);
        listenerSetup(thetaTXTF, thetaSLD);
        listenerSetup(massTXTF, massSLD);
        listenerSetup(radiusTXTF, radiusSLD);

        setInitialValues();

    }


    private void listenerSetup(TextField t1, Slider s1) {
        if (!isNull(s1) && !isNull(t1)) {
            s1.valueProperty().addListener(((observableValue, oldValue, newValue) -> {
                String valueString = String.valueOf(newValue);

                int endIndex = Math.min(valueString.length(), 6);

                if(s1.equals(radiusSLD)) {
                    previewSphere.setRadius(radiusSLD.getValue() * 10);
                }

                if(Double.parseDouble(valueString) > s1.getMin() || Double.parseDouble(valueString) < s1.getMax()) {
                    t1.setText(valueString.substring(0, endIndex));
                } else {
                    t1.setText(String.format(oldValue + ""));
                }
            }));
        }

    }

    private boolean isValidDouble(String str) {
        if (isNull(str)) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void updateSimInfo() {
        simulationListContent.set(1, Constants.PLANET_COUNT_PREFIX + getPlanetCount());
        simulationListContent.set(2, Constants.NUMBER_OF_COLLISIONS_PREFIX + Simulation.collisionCount);
    }

    private void setInitialValues() {
        if (!isNull(gConstSLD)) {
            gConstSLD.setValue(g);
        } else {
            System.out.println("Gravity Constant Slider is null");
        }

        massSLD.setValue(massSLD.getMax() / 2);
        massTXTF.setText(massSLD.getValue() + "");
        radiusSLD.setValue(5);
        radiusTXTF.setText(radiusSLD.getValue() + "");
        thetaSLD.setValue(0.5);
        thetaTXTF.setText(thetaSLD.getValue() + "");

        resetTextureBTN.setDisable(true);

        gConstantTXTF.setText(g + "");
    }

    private void initializeTime() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            secondsElapsed++;
            updateTimer();
        }));
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

    private void playPauseSim() {
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
    void onBTNUpdate(ActionEvent event) throws FileNotFoundException {

        Button btn = (Button) event.getSource();

        if(btn.equals(playPauseBTN)) {
            playPauseSim();
        } else if(btn.equals(viewRandBtn)) {
            viewRandPlanet();
        } else if(btn.equals(textureBTN)) {
            chooseCustomTexture();
        } else if(btn.equals(resetTextureBTN)) {
            resetTexture();
        } else if(btn.equals(spawnRandomPlanetBTN)) {
            spawnRandomPlanet();
        } else if(btn.equals(createBTN)) {
            createCustomPlanet();
        } else if(btn.equals(originBTN)) {
            simulation.simulationView.goOrigin();
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

    private void createCustomPlanet() {
        Vector3D position = getPositionInFrontOfCamera();
        Vector3D velocity = new Vector3D();

        double radius = Double.parseDouble(radiusTXTF.getText());
        double mass = Double.parseDouble(massTXTF.getText());
        boolean isSun = sunCheckB.isSelected();

        String uniqueID = UUID.randomUUID().toString().replaceAll("-", "");

        Planet planet;

        if (!isNull(customTexture)) {
            planet = new Planet(uniqueID, position, velocity, radius, mass, isSun, customTexture);
            previewSphereMaterial.setDiffuseMap(customTexture);
        } else {

            planet = new Planet(uniqueID, position, velocity, radius, mass, isSun, defaultCustomPlanetTexture);
        }
        simulation.planetMap.put(uniqueID, planet);
    }

    private void spawnRandomPlanet() {
        String uniqueID = UUID.randomUUID().toString().replaceAll("-", "");

        Planet planet = simulation.createRandomPlanet(uniqueID, getPositionInFrontOfCamera());

        simulation.planetMap.put(uniqueID, planet);

    }

    public void updateSelectedPlanetInfo(Planet planet) {
        ObservableList<String> planetInfo = FXCollections.observableArrayList("ID: " + planet.name, "Position: " + String.format("%.3f, %.3f, %.3f", planet.position.x, planet.position.y, planet.position.z), "Velocity: " + String.format("%.3f, %.3f, %.3f", planet.velocity.x, planet.velocity.y, planet.velocity.z), "Accel: " + String.format("%.1e, %.1e, %.1e", planet.acceleration.x, planet.acceleration.y, planet.acceleration.z), "Radius: " + String.format("%.3f", planet.radius), "Mass: " + String.format("%.3f", planet.mass), "Color: " + planet.color);
        selectedPlanetInfoList.setItems(planetInfo);
    }

    private void viewRandPlanet() {
        // Get a random planet from the planetMap
        List<String> keys = new ArrayList<>(simulation.planetMap.keySet());
        String randomKey = keys.get(new Random().nextInt(keys.size()));
        Planet randomPlanet = simulation.planetMap.get(randomKey);

        // Update the selected planet info
        updateSelectedPlanetInfo(randomPlanet);

        // Update the current camera planet in the SimulationView
        simulation.simulationView.setCurrentCamPlanetID(randomKey);
    }

    private void chooseCustomTexture() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));

        SelectedImgFile = fileChooser.showOpenDialog(new Stage());

        if (SelectedImgFile != null) {
            customTexture = new Image(SelectedImgFile.toURI().toString());
            //for preview sphere
            Color color =  AverageColourGenerator.getAverageColor(customTexture);
            previewSphereMaterial.setDiffuseColor(color);

            previewSphereMaterial.setDiffuseMap(customTexture);
            resetTextureBTN.setDisable(false);
        } else {
            if (isNull(customTexture)) {
                previewSphereMaterial.setDiffuseMap(defaultCustomPlanetTexture);
            } else {
                previewSphereMaterial.setDiffuseMap(customTexture);
            }
        }
    }

    private void resetTexture() {
        customTexture = null;
        previewSphereMaterial.setDiffuseMap(defaultCustomPlanetTexture);
        sunCheckB.setSelected(false);
        textureBTN.setDisable(false);
        resetTextureBTN.setDisable(true);

    }

    @FXML
    void sunCheckBSelected(ActionEvent event) {
        if (sunCheckB.isSelected()) {
            previewSphereMaterial.setDiffuseMap(Planet.sunTexture);
            customTexture = Planet.sunTexture;
            resetTextureBTN.setDisable(false);
        } else if (!sunCheckB.isSelected()) {
            customTexture = defaultCustomPlanetTexture;
            previewSphereMaterial.setDiffuseMap(defaultCustomPlanetTexture);
        }
        textureBTN.setDisable(sunCheckB.isSelected());
    }

    private Vector3D getPositionInFrontOfCamera() {
        return simulation.simulationView.getPositionInFrontOfCamera(50);
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