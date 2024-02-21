package com.coolawesome.integrativeproject;

import com.coolawesome.integrativeproject.physics.Planet;
import com.coolawesome.integrativeproject.physics.Vector3D;
import javafx.scene.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SimulationView extends Group {

    private SubScene subScene;
    private Set<KeyCode> keysPressed = new HashSet<>();
    private Map<String, Planet> planetMap;
    private String currentCamPlanetID = "";
    private double deltaMouseX, deltaMouseY = 0;
    private PerspectiveCamera camera;
    Vector3D camPos = new Vector3D(0, 0, -100);
    Vector3D camVel = new Vector3D();
    private Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    private Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
    private double orbitDistance = 100;


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

        PointLight pl = new PointLight(Color.WHITE);
        pl.setTranslateX(0);
        pl.setTranslateY(0);
        pl.setTranslateZ(0);

        AmbientLight al = new AmbientLight(Color.rgb(100, 100, 100));

        this.getChildren().addAll(pl, al);

        subScene.setOnMousePressed(event -> {
            deltaMouseX = event.getSceneX();
            deltaMouseY = event.getSceneY();
        });

        subScene.setOnMouseDragged(event -> {
            currentCamPlanetID = "";
            deltaMouseX = event.getSceneX() - deltaMouseX;
            deltaMouseY = event.getSceneY() - deltaMouseY;

            rotateX.setAngle(rotateX.getAngle() - deltaMouseY / 4);
            rotateY.setAngle(rotateY.getAngle() + deltaMouseX / 4);

            deltaMouseX = event.getSceneX();
            deltaMouseY = event.getSceneY();
        });

        subScene.setOnScroll(event -> {
            orbitDistance += event.getDeltaY() / 4;
        });

        subScene.setOnKeyPressed(event -> {
            keysPressed.add(event.getCode());
        });

        subScene.setOnKeyReleased(event -> {
            keysPressed.remove(event.getCode());
        });

        subScene.requestFocus();

        update(0.0);
    }

    private void initializeCamera() {
        camera = new PerspectiveCamera(true);
        camera.setTranslateX(camPos.x);
        camera.setTranslateY(camPos.y);
        camera.setTranslateZ(camPos.z);
        camera.setFarClip(5000.0);
        camera.setNearClip(0.1);
        camera.getTransforms().add(0, rotateY);
        camera.getTransforms().add(1, rotateX);
    }

    public void update(double dt) {
        // check if there are any planets not currently in the scene
        planetMap.forEach((id, planet) -> {
            if (!this.getChildren().contains(planet.planetNode)) {
                this.getChildren().add(planet.planetNode);
                planet.planetNode.setOnMouseClicked(event -> {
                    currentCamPlanetID = id;
                });
            }
        });

        //update the position of the planets
        for (var planet : planetMap.values()) {
            planet.planetNode.setTranslateX(planet.position.x);
            planet.planetNode.setTranslateY(planet.position.y);
            planet.planetNode.setTranslateZ(planet.position.z);
        }

        if (!currentCamPlanetID.isEmpty()) {

            Planet camPlanet = planetMap.get(currentCamPlanetID);
            Vector3D camPlanetPos = camPlanet.position;

            Vector3D camDir = Vector3D.difference(camPlanetPos, camPos);
            camDir.normalize();

            if (planetMap.get(currentCamPlanetID).position.distance(camPos) > orbitDistance + 1){
                // move towards the planet
                camVel.add(Vector3D.multiplication(0.1, camDir));
            } else if (planetMap.get(currentCamPlanetID).position.distance(camPos) < orbitDistance - 1){
                // move away from the planet
                camVel.subtract(Vector3D.multiplication(0.1, camDir));
            } else {
                // orbit around the planet
                Vector3D direction = Vector3D.difference(camPlanet.position, camPos);
                direction.normalize();

                // Calculate the cross product of the direction vector and the up vector (0, 1, 0) to get the perpendicular vector
                Vector3D perpendicular = Vector3D.crossProduct(direction, new Vector3D(0, 1, 0));
                perpendicular.normalize();

                // Set the camera's velocity to the perpendicular vector multiplied by a speed factor
                camVel = Vector3D.multiplication(0.2, perpendicular);
            }

            // Calculate the direction vector from the camera to the planet
            Vector3D direction = Vector3D.difference(camPlanet.position, camPos);

            // Normalize the direction vector
            direction.normalize();

            // Calculate the pitch (rotation around the X-axis) and yaw (rotation around the Y-axis) angles
            double pitch = -Math.toDegrees(Math.asin(direction.y));
            double yaw = Math.toDegrees(Math.atan2(direction.x, direction.z));

            // Set the angles of rotateX and rotateY to the calculated pitch and yaw respectively
            rotateX.setAngle(pitch);
            rotateY.setAngle(yaw);
        }

        camPos.add(camVel);

        camVel.multiply(0.9);

        camera.setTranslateX(camPos.x);
        camera.setTranslateY(camPos.y);
        camera.setTranslateZ(camPos.z);
    }
}
