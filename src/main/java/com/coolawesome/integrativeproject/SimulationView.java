package com.coolawesome.integrativeproject;

import com.coolawesome.integrativeproject.physics.Planet;
import javafx.scene.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import java.util.ArrayList;
import java.util.Map;

public class SimulationView extends Group {
    private Map<String, Planet> planetMap;
    private PerspectiveCamera camera;
    private SubScene subScene;

    public SimulationView(AnchorPane pane, Map<String, Planet> planetMap) {
        initializeCamera();
        this.planetMap = planetMap;
        // Set up the subscene for 3D content
        subScene = new SubScene(this, pane.getWidth(), pane.getHeight(), true, SceneAntialiasing.BALANCED);
        subScene.widthProperty().bind(pane.widthProperty());
        subScene.heightProperty().bind(pane.heightProperty());
        subScene.setFill(Color.BLACK);
        subScene.setCamera(camera);
        pane.getChildren().add(subScene);

        // Add planet nodes here...
        update(0.0);
    }

    private void initializeCamera() {
        camera = new PerspectiveCamera(true);
//        camera.getTransforms().addAll(
//                new Translate(0, 0, -1000),
//                new Rotate(-45, Rotate.Y_AXIS),
//                new Rotate(-45, Rotate.X_AXIS)
//        );
        camera.setTranslateZ(-100);
        camera.setFarClip(5000.0);
        camera.setNearClip(0.1);
    }

    public void update(double dt) {
        //check if there are any planets not currently in the scene
        for (var planet : planetMap.values()) {
            if (!this.getChildren().contains(planet.planetNode)) {
                this.getChildren().add(planet.planetNode);
            }
        }
        //update the position of the planets
        for (var planet : planetMap.values()) {
            planet.planetNode.setTranslateX(planet.position.x);
            planet.planetNode.setTranslateY(planet.position.y);
            planet.planetNode.setTranslateZ(planet.position.z);
        }
    }
}
