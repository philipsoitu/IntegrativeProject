package com.coolawesome.integrativeproject;

import com.coolawesome.integrativeproject.utils.Constants;
import com.coolawesome.integrativeproject.utils.PIDController;
import com.coolawesome.integrativeproject.utils.Vector3D;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Sphere;
import lombok.Getter;
import lombok.Setter;
import org.fxyz3d.scene.Skybox;
import org.fxyz3d.utils.CameraTransformer;

import java.util.HashSet;
import java.util.Set;

/**
 * Class that represents the view of the simulation.
 */
public class SimulationView extends Group {

    private static final Image
            backImage = new Image("file:src/main/resources/images/skybox/back.png"),
            bottomImage = new Image("file:src/main/resources/images/skybox/bottom.png"),
            frontImage = new Image("file:src/main/resources/images/skybox/front.png"),
            leftImage = new Image("file:src/main/resources/images/skybox/left.png"),
            rightImage = new Image("file:src/main/resources/images/skybox/right.png"),
            topImage = new Image("file:src/main/resources/images/skybox/top.png");
    private final Set<KeyCode> keysPressed = new HashSet<>();
    private final Simulation simulation;
    private final MainController mainController;
    private final CameraTransformer cameraTransform = new CameraTransformer();
    private final Vector3D cameraVelocity = new Vector3D();
    @Setter
    @Getter
    private String currentCamPlanetID = "";
    private double deltaMouseX, deltaMouseY = 0;

    @Setter
    @Getter
    private boolean goingToOrigin;

    /**
     * Constructor for the SimulationView class.
     *
     * @param pane           The AnchorPane to add the simulation view to.
     * @param simulation     The simulation to display.
     * @param mainController The main controller for the simulation.
     */
    public SimulationView(AnchorPane pane, Simulation simulation, MainController mainController) {
        this.simulation = simulation;
        this.mainController = mainController;

        // Set up the sub scene for 3D content
        SubScene subScene = new SubScene(this, pane.getWidth(), pane.getHeight(), true, SceneAntialiasing.BALANCED);
        subScene.widthProperty().bind(pane.widthProperty());
        subScene.heightProperty().bind(pane.heightProperty());
        subScene.setFill(Color.BLACK);
        pane.getChildren().add(subScene);
        // Set up the camera
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(100000.0);
        camera.setTranslateX(0);
        camera.setTranslateY(0);
        camera.setTranslateZ(0);
        camera.setFieldOfView(Constants.CAM_FOV);
        cameraTransform.getChildren().add(camera);
        cameraTransform.t.setX(0);
        cameraTransform.t.setY(0);
        cameraTransform.t.setZ(0);
        cameraTransform.rx.setPivotZ(0);
        cameraTransform.ry.setPivotZ(0);
        subScene.setCamera(camera);
        cameraTransform.t.setZ(-100);

        AmbientLight al = new AmbientLight(Color.rgb(100, 100, 100));

        Skybox skyBox = new Skybox(topImage, bottomImage, rightImage, leftImage, frontImage, backImage, 100000, camera);

        this.getChildren().addAll(al, skyBox);

        // Set up the event handlers
        subScene.setOnMousePressed(event -> {
            deltaMouseX = event.getSceneX();
            deltaMouseY = event.getSceneY();
        });
        subScene.setOnMouseDragged(event -> {
            currentCamPlanetID = "";
            deltaMouseX = event.getSceneX() - deltaMouseX;
            deltaMouseY = event.getSceneY() - deltaMouseY;

            cameraTransform.ry.setAngle(cameraTransform.ry.getAngle() + deltaMouseX * 0.2);
            cameraTransform.rx.setAngle(cameraTransform.rx.getAngle() - deltaMouseY * 0.2);

            deltaMouseX = event.getSceneX();
            deltaMouseY = event.getSceneY();
        });
        pane.setOnKeyPressed(event -> keysPressed.add(event.getCode()));
        pane.setOnKeyReleased(event -> keysPressed.remove(event.getCode()));

        subScene.requestFocus();
    }

    /**
     * Updates the simulation view.
     */
    public void update() {
        // Update the selected planet info list
        if (!currentCamPlanetID.isEmpty()) {
            this.mainController.updateSelectedPlanetInfo(simulation.planetMap.get(currentCamPlanetID));
        }

        // check if there are any planets not currently in the scene
        simulation.planetMap.forEach((id, planet) -> {
            if (!this.getChildren().contains(planet.planetNode)) {
                this.getChildren().add(planet.planetNode);
                planet.planetNode.setOnMouseClicked(event -> currentCamPlanetID = id);
            }
            if (planet.isSun && !this.getChildren().contains(planet.sunLight)) {
                this.getChildren().add(planet.sunLight);
            }
        });

        // check if there are any planets in the scene that are not in the map
        Set<Node> nodesToRemove = new HashSet<>();
        for (Node node : this.getChildren()) {
            if (node instanceof Sphere) {
                boolean found = false;
                for (Planet planet : simulation.planetMap.values()) {
                    if (planet.planetNode.equals(node)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    nodesToRemove.add(node);
                    if (currentCamPlanetID.equals(node.getId())) {
                        currentCamPlanetID = "";
                    }
                }
            }
            if (node instanceof PointLight) {
                boolean found = false;
                for (Planet planet : simulation.planetMap.values()) {
                    if (planet.isSun && planet.sunLight.equals(node)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    nodesToRemove.add(node);
                }
            }
        }
        this.getChildren().removeAll(nodesToRemove);

        //update the position of the planets
        for (var planet : simulation.planetMap.values()) {
            planet.planetNode.setTranslateX(planet.position.x);
            planet.planetNode.setTranslateY(planet.position.y);
            planet.planetNode.setTranslateZ(planet.position.z);
            if (planet.isSun) {
                planet.sunLight.setTranslateX(planet.position.x);
                planet.sunLight.setTranslateY(planet.position.y);
                planet.sunLight.setTranslateZ(planet.position.z);
            }
        }

        // Move the camera
        double yaw = Math.toRadians(cameraTransform.ry.getAngle());
        double pitch = Math.toRadians(cameraTransform.rx.getAngle());

        Vector3D facing = getFacingVector(pitch, yaw);

        Vector3D right = Vector3D.crossProduct(facing, new Vector3D(0, 1, 0));
        right.normalize();

        Vector3D up = Vector3D.crossProduct(right, facing);
        up.normalize();

        facing.multiply(0.6); // reduce the speed of the camera
        right.multiply(0.6);
        up.multiply(0.6);

        if (goingToOrigin) {
            currentCamPlanetID = "";
            goOrigin();
            if (getCameraPos().distance(new Vector3D(0, 0, 0)) < 1) {
                goingToOrigin = false;
            }
        }

        if (!currentCamPlanetID.isEmpty()) {
            Planet planet = simulation.planetMap.get(currentCamPlanetID);
            if (planet != null) {
                goToCoordinate(planet.position, planet);
            }

        } else {

            if (keysPressed.contains(KeyCode.W)) {
                cameraVelocity.x += facing.x;
                cameraVelocity.y -= facing.y;
                cameraVelocity.z += facing.z;
            }
            if (keysPressed.contains(KeyCode.S)) {
                cameraVelocity.x -= facing.x;
                cameraVelocity.y += facing.y;
                cameraVelocity.z -= facing.z;
            }
            if (keysPressed.contains(KeyCode.A)) {
                cameraVelocity.x += right.x;
                cameraVelocity.y -= right.y;
                cameraVelocity.z += right.z;
            }
            if (keysPressed.contains(KeyCode.D)) {
                cameraVelocity.x -= right.x;
                cameraVelocity.y += right.y;
                cameraVelocity.z -= right.z;
            }
            if (keysPressed.contains(KeyCode.SPACE)) {
                cameraVelocity.x += up.x;
                cameraVelocity.y -= up.y;
                cameraVelocity.z += up.z;
            }
            if (keysPressed.contains(KeyCode.CONTROL)) {
                cameraVelocity.x -= up.x;
                cameraVelocity.y += up.y;
                cameraVelocity.z -= up.z;
            }
            if (keysPressed.contains(KeyCode.SHIFT)) {
                cameraVelocity.multiply(1.2);
            }
        }

        cameraTransform.t.setX(cameraTransform.t.getX() + cameraVelocity.x);
        cameraTransform.t.setY(cameraTransform.t.getY() + cameraVelocity.y);
        cameraTransform.t.setZ(cameraTransform.t.getZ() + cameraVelocity.z);

        cameraVelocity.multiply(0.8);

        updateCameraCoords();
    }

    /**
     * Moves the camera to the specified coordinates.
     *
     * @param coordinates The coordinates to move the camera to.
     * @param planet      The planet to move the camera to. (null if no planet)
     */
    private void goToCoordinate(Vector3D coordinates, Planet planet) {
        Vector3D camPos = getCameraPos();
        Vector3D direction = Vector3D.difference(coordinates, camPos);
        double distance = direction.magnitude();
        direction.normalize();

        double directionPitch = -Math.toDegrees(Math.asin(direction.y));
        double directionYaw = Math.toDegrees(Math.atan2(direction.x, direction.z));

        cameraTransform.rx.setAngle(lerpAngle(cameraTransform.rx.getAngle(), directionPitch));
        cameraTransform.ry.setAngle(lerpAngle(cameraTransform.ry.getAngle(), directionYaw));

        //Use PID controller to move towards planet
        direction.negate();
        PIDController pidController = new PIDController(1e-15, 1e-2, 1e-8);
        double pidOutput;
        if (planet != null) {
            pidOutput = pidController.calculate(planet.radius * 3, distance);
        } else {
            pidOutput = pidController.calculate(0, distance);
        }
        direction.multiply(pidOutput);
        cameraVelocity.add(direction);
    }

    /**
     * Updates the camera coordinates on the main controller.
     */
    private void updateCameraCoords() {
        this.mainController.xPosLBL.setText(" X: " + String.format("%.3f", cameraTransform.t.getX()));
        this.mainController.yPosLBL.setText(" Y: " + String.format("%.3f", cameraTransform.t.getY()));
        this.mainController.zPosLBL.setText(" Z: " + String.format("%.3f", cameraTransform.t.getZ()));
    }

    /**
     * Linearly interpolates between two angles.
     *
     * @param start The starting angle.
     * @param end   The ending angle.
     * @return The interpolated angle.
     */
    private double lerpAngle(double start, double end) {
        double diff = end - start;
        // Normalize the difference in the angle
        while (diff < -180) diff += 360;
        while (diff > 180) diff -= 360;
        return start + diff * 0.5;
    }

    /**
     * Gets the facing vector of the camera.
     *
     * @param pitch The pitch of the camera.
     * @param yaw   The yaw of the camera.
     * @return The facing vector of the camera.
     */
    private Vector3D getFacingVector(double pitch, double yaw) {
        double x = Math.cos(pitch) * Math.sin(yaw);
        double y = Math.sin(pitch);
        double z = Math.cos(pitch) * Math.cos(yaw);

        Vector3D facing = new Vector3D(x, y, z);
        facing.normalize();
        return facing;
    }

    /**
     * Moves the camera to the origin.
     */
    public void goOrigin() {
        setCurrentCamPlanetID("");
        goToCoordinate(new Vector3D(), null);
    }

    /**
     * Gets the position in front of the camera.
     *
     * @param distance The distance in front of the camera.
     * @return The position in front of the camera.
     */
    public Vector3D getPositionInFrontOfCamera(double distance) {
        //camera position
        double camX = cameraTransform.t.getX();
        double camY = cameraTransform.t.getY();
        double camZ = cameraTransform.t.getZ();

        //orientation
        double pitch = Math.toRadians(cameraTransform.rx.getAngle());
        double yaw = Math.toRadians(cameraTransform.ry.getAngle());

        double x = camX + distance * Math.sin(yaw) * Math.cos(pitch);
        double y = camY - distance * Math.sin(pitch);
        double z = camZ + distance * Math.cos(yaw) * Math.cos(pitch);

        return new Vector3D(x, y, z);
    }

    /**
     * Gets the position of the camera.
     *
     * @return The position of the camera.
     */
    private Vector3D getCameraPos() {
        return new Vector3D(cameraTransform.t.getX(), cameraTransform.t.getY(), cameraTransform.t.getZ());
    }
}
