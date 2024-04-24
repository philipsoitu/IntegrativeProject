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
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
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
    public static double theta;
    @FXML
    public ListView<String> selectedPlanetInfoList;
    @FXML
    public Button viewRandBtn;
    @FXML
    public TextField SearchBar;
    @FXML
    public Button SearchBtn;
    @FXML
    public Button RenameBtn;
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
    private Button resetBTN;
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
    @FXML
    private ColorPicker planetColourPicker;

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
        previewSetup();
        viewport.requestFocus();

        Scene scene = viewport.getScene();
        if (scene != null) {
            scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == KeyCode.ESCAPE) {
                    viewport.requestFocus();
                    event.consume();
                }
            });
        } else {
            // Scene might not be set yet, set up a listener to add the filter when it's available
            viewport.sceneProperty().addListener((observableScene, oldScene, newScene) -> {
                if (newScene != null) {
                    newScene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                        if (event.getCode() == KeyCode.ESCAPE) {
                            viewport.requestFocus();
                            event.consume();
                        }
                    });
                }
            });
        }
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

        TextField[] textFields = {gConstantTXTF, thetaTXTF, massTXTF, radiusTXTF};
        Slider[] sliders = {gConstSLD, thetaSLD, massSLD, radiusSLD};

        for (int i = 0; i < textFields.length; i++) {
            listenerSetup(textFields[i], sliders[i]);
        }
        setInitialValues();
    }

    private void listenerSetup(TextField t1, Slider s1) {
        if (!isNull(s1) && !isNull(t1)) {

            //sliders
            s1.valueProperty().addListener((observableValue, oldValue, newValue) -> {
                resetBTN.setDisable(false);

                if (!t1.equals(massTXTF)) {
                    t1.setText(String.format("%.3f", newValue.doubleValue()));
                } else {
                    t1.setText(String.format("%.1f", newValue.doubleValue()));
                }

                if (s1.equals(radiusSLD)) {
                    previewSphere.setRadius(newValue.doubleValue() * 10);
                } else if (s1.equals(gConstSLD)) {
                    updateGConst();
                } else if (s1.equals(thetaSLD)) {
                    updateTheta();
                }
            });

            //textFields
            t1.textProperty().addListener((observableValue, oldValue, newValue) -> {
                if (newValue.isEmpty() || isValidDouble(newValue)) {
                    t1.setText(newValue);
                } else {
                    t1.setText(oldValue);
                }
            });
        }
    }


    private void updateTheta() {
        theta = thetaSLD.getValue();
    }

    public void updateSimInfo() {
        simulationListContent.set(1, Constants.PLANET_COUNT_PREFIX + getPlanetCount());
        simulationListContent.set(2, Constants.NUMBER_OF_COLLISIONS_PREFIX + Simulation.collisionCount);
    }

    private void setInitialValues() {
        setInitialControls();
        setDefaultPlanetParameters();
    }

    private void setDefaultPlanetParameters() {
        massSLD.setValue(massSLD.getMax() / 2);
        massTXTF.setText(massSLD.getValue() + "");
        radiusSLD.setValue(5);
        radiusTXTF.setText(radiusSLD.getValue() + "");
        resetBTN.setDisable(true);
    }

    private void setInitialControls() {
        if (!isNull(gConstSLD) && !isNull(gConstantTXTF)) {
            gConstSLD.setValue(g);
            gConstantTXTF.setText(gConstSLD.getValue() + "");
        } else {
            System.out.println("Gravity Constant Slider is null");
        }

        if (!isNull(thetaSLD) && !isNull(thetaTXTF)) {
            thetaSLD.setValue(0.5);
            thetaTXTF.setText(thetaSLD.getValue() + "");
        } else {
            System.out.println("Theta Slider is null");
        }
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
            playPauseBTN.setText("Play");
        } else {
            playPauseBTN.setText("Pause");
        }
    }

    @FXML
    void onBTNUpdate(ActionEvent event) {
        Button btn = (Button) event.getSource();

        if (btn.equals(playPauseBTN)) {
            playPauseSim();
        } else if (btn.equals(viewRandBtn)) {
            viewRandPlanet();
        } else if (btn.equals(textureBTN)) {
            chooseCustomTexture();
        } else if (btn.equals(resetBTN)) {
            resetCustomPlanetInputs();
        } else if (btn.equals(spawnRandomPlanetBTN)) {
            spawnRandomPlanet();
        } else if (btn.equals(createBTN)) {
            createCustomPlanet();
        } else if (btn.equals(originBTN)) {
            simulation.simulationView.setGoingToOrigin(true);
        }
        viewport.requestFocus();
    }

    @FXML
    void onTXTFUpdate(ActionEvent event) {
        TextField source = (TextField) event.getSource();

        if (source.equals(gConstantTXTF)) {
            updateTextFieldAndSlider(gConstantTXTF, gConstSLD);
            updateGConst();
        } else if (source.equals(massTXTF)) {
            updateTextFieldAndSlider(massTXTF, massSLD);
        } else if (source.equals(radiusTXTF)) {
            updateTextFieldAndSlider(radiusTXTF, radiusSLD);
        } else if (source.equals(thetaTXTF)) {
            updateTextFieldAndSlider(thetaTXTF, thetaSLD);
            updateTheta();
        }

        viewport.requestFocus();
    }

    private void updateTextFieldAndSlider(TextField textField, Slider slider) {
        try {

            if (textField.getText().isEmpty()) {
                textField.setText(slider.getMin() + "");
            }


            double newValue = Double.parseDouble(textField.getText());


            if (newValue >= slider.getMin() && newValue <= slider.getMax()) {
                slider.setValue(newValue);
            } else if (newValue > slider.getMax()) {
                textField.setText(slider.getMax() + "");
            } else if (newValue < slider.getMin()) {
                textField.setText(slider.getMin() + "");
            } else if (String.valueOf(newValue).isEmpty()) {

                textField.setText(slider.getMin() + "");

            }

            slider.setValue(Double.parseDouble(textField.getText()));
        } catch (NumberFormatException e) {
            System.out.println("Invalid input");
        }
    }


    private boolean isValidDouble(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @FXML
    void setPlanetColour(ActionEvent event) {
        if (!isNull(planetColourPicker)) {
            previewSphereMaterial.setDiffuseColor(planetColourPicker.getValue());
            previewSphere.setMaterial(previewSphereMaterial);
            if (!isNull(customTexture)) {
                previewSphereMaterial.setDiffuseMap(customTexture);
            }
            resetBTN.setDisable(false);
        } else {
            System.out.println("Planet color picker is null");
        }
        viewport.requestFocus();
    }

    private void createCustomPlanet() {
        Planet planet;

        Vector3D position = getPositionInFrontOfCamera();
        Vector3D velocity = new Vector3D();

        double radius = Double.parseDouble(radiusTXTF.getText());
        double mass = Double.parseDouble(massTXTF.getText());
        boolean isSun = sunCheckB.isSelected();

        String uniqueID = UUID.randomUUID().toString().replaceAll("-", "");

        if (!isNull(customTexture)) {
            planet = new Planet(uniqueID, position, velocity, radius, mass, isSun, customTexture, planetColourPicker.getValue());
            previewSphereMaterial.setDiffuseMap(customTexture);
        } else {
            planet = new Planet(uniqueID, position, velocity, radius, mass, isSun, defaultCustomPlanetTexture, planetColourPicker.getValue());
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
            Color color = AverageColourGenerator.getAverageColor(customTexture);
            previewSphereMaterial.setDiffuseColor(color);
            previewSphereMaterial.setDiffuseMap(customTexture);
            resetBTN.setDisable(false);
        } else {
            if (isNull(customTexture)) {
                previewSphereMaterial.setDiffuseMap(defaultCustomPlanetTexture);
            } else {
                previewSphereMaterial.setDiffuseMap(customTexture);
            }
        }
        previewSphereMaterial.setDiffuseColor(planetColourPicker.getValue());
    }

    private void resetCustomPlanetInputs() {
        customTexture = null;
        previewSphereMaterial.setDiffuseMap(defaultCustomPlanetTexture);
        previewSphereMaterial.setDiffuseColor(Color.WHITE);

        setDefaultPlanetParameters();

        planetColourPicker.setValue(Color.WHITE);
        sunCheckB.setSelected(false);
        textureBTN.setDisable(false);
        resetBTN.setDisable(true);
    }

    @FXML
    void sunCheckBSelected(ActionEvent event) {
        if (sunCheckB.isSelected()) {
            previewSphereMaterial.setDiffuseMap(Planet.sunTexture);
            customTexture = Planet.sunTexture;
        } else if (!sunCheckB.isSelected()) {
            customTexture = defaultCustomPlanetTexture;
            previewSphereMaterial.setDiffuseMap(defaultCustomPlanetTexture);
        }
        resetBTN.setDisable(false);
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
        viewport.requestFocus();
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
        viewport.requestFocus();
    }

    public void handleSearch(ActionEvent actionEvent) {
        String searchText = SearchBar.getText().trim();
        Planet foundPlanet = null;

        for (Planet planet : simulation.planetMap.values()) {
            if (planet.name.equalsIgnoreCase(searchText)) {
                foundPlanet = planet;
                break;
            }
        }
        if (foundPlanet == null) {
            SearchBar.setText("Planet not found");
            return;
        }
        for (String planetKey : simulation.planetMap.keySet()) {
            if (simulation.planetMap.get(planetKey).equals(foundPlanet)) {
                simulation.simulationView.setCurrentCamPlanetID(planetKey);
                break;
            }
        }
        viewport.requestFocus();
        SearchBar.clear();
    }

    public void handleRename(ActionEvent actionEvent) {
        String searchText = SearchBar.getText().trim();
        if (!simulation.simulationView.getCurrentCamPlanetID().isEmpty()) {
            simulation.planetMap.get(simulation.simulationView.getCurrentCamPlanetID()).name = searchText;
        }
        viewport.requestFocus();
        SearchBar.clear();
    }

    public void searchClicked(MouseEvent mouseEvent) {
        if (SearchBar.getText().equals("Planet not found")) {
            SearchBar.clear();
        }
    }
}