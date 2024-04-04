package com.coolawesome.integrativeproject;

import com.coolawesome.integrativeproject.utils.PIDController;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.layout.AnchorPane;

import javafx.scene.shape.Sphere;
import lombok.Setter;
import org.fxyz3d.scene.Skybox;
import org.fxyz3d.utils.CameraTransformer;

import com.coolawesome.integrativeproject.utils.Vector3D;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class SimulationView extends Group {

    private final Set<KeyCode> keysPressed = new HashSet<>();

    private final Simulation simulation;
    private final MainController mainController;

    @Setter
    private String currentCamPlanetID = "";

    private final Image
        backImage = new Image("file:src/main/resources/images/skybox/back.png"),
        bottomImage = new Image("file:src/main/resources/images/skybox/bottom.png"),
        frontImage = new Image("file:src/main/resources/images/skybox/front.png"),
        leftImage = new Image("file:src/main/resources/images/skybox/left.png"),
        rightImage = new Image("file:src/main/resources/images/skybox/right.png"),
        topImage = new Image("file:src/main/resources/images/skybox/top.png"),
        cubeMap = new Image("file:src/main/resources/images/skybox/cubemap.png");
    private final Skybox skyBox;

    private final PerspectiveCamera camera;
    private final double FOV = 60;
    private final CameraTransformer cameraTransform = new CameraTransformer();
    private final Vector3D cameraVelocity = new Vector3D();

    private double deltaMouseX, deltaMouseY = 0;

    public SimulationView(AnchorPane pane, Simulation simulation, MainController mainController) {
        this.simulation = simulation;
        this.mainController = mainController;

        // Set up the subscene for 3D content
        SubScene subScene = new SubScene(this, pane.getWidth(), pane.getHeight(), true, SceneAntialiasing.BALANCED);
        subScene.widthProperty().bind(pane.widthProperty());
        subScene.heightProperty().bind(pane.heightProperty());
        subScene.setFill(Color.BLACK);
        pane.getChildren().add(subScene);
        // Set up the camera
        camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(100000.0);
        camera.setTranslateX(0);
        camera.setTranslateY(0);
        camera.setTranslateZ(0);
        camera.setFieldOfView(FOV);
        cameraTransform.getChildren().add(camera);
        cameraTransform.t.setX(0);
        cameraTransform.t.setY(0);
        cameraTransform.t.setZ(0);
        cameraTransform.rx.setPivotZ(0);
        cameraTransform.ry.setPivotZ(0);
        subScene.setCamera(camera);
        cameraTransform.t.setZ(-100);

        AmbientLight al = new AmbientLight(Color.rgb(100, 100, 100));

        skyBox = new Skybox(topImage, bottomImage, rightImage, leftImage, frontImage, backImage, 100000, camera);

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

    public void update(double dt) {
        // Update the selected planet info list
        if (!currentCamPlanetID.isEmpty()) {
            this.mainController.updateSelectedPlanetInfo(simulation.planetMap.get(currentCamPlanetID));
        }

        // check if there are any planets not currently in the scene
        simulation.planetMap.forEach((id, planet) -> {
            if (!this.getChildren().contains(planet.planetNode)) {
                this.getChildren().add(planet.planetNode);
                planet.planetNode.setOnMouseClicked(event -> {
                    currentCamPlanetID = id;
                });
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
            if (planet.isSun){
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

        if (!currentCamPlanetID.isEmpty()) {
            Planet planet = simulation.planetMap.get(currentCamPlanetID);
            if (planet != null) {
                Vector3D camPos = new Vector3D(
                        cameraTransform.t.getX(),
                        cameraTransform.t.getY(),
                        cameraTransform.t.getZ()
                );
                Vector3D planetPos = planet.position;
                Vector3D direction = Vector3D.difference(planetPos, camPos);
                double distance = direction.magnitude();
                direction.normalize();

                double directionPitch = -Math.toDegrees(Math.asin(direction.y));
                double directionYaw = Math.toDegrees(Math.atan2(direction.x, direction.z));

                cameraTransform.rx.setAngle(lerpAngle(cameraTransform.rx.getAngle(), directionPitch, 0.5));
                cameraTransform.ry.setAngle(lerpAngle(cameraTransform.ry.getAngle(), directionYaw, 0.5));

                //Use PID controller to move towards planet
                direction.negate();
                PIDController pidController = new PIDController(1e-15, 1e-2, 1e-8);
                double pidOutput = pidController.calculate(planet.radius * 3, distance);
                direction.multiply(pidOutput);
                cameraVelocity.add(direction);
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
    }

    private double lerpAngle(double start, double end, double t) {
        double diff = end - start;
        // Normalize the difference in the angle
        while (diff < -180) diff += 360;
        while (diff > 180) diff -= 360;
        return start + diff * t;
    }

    private Vector3D getFacingVector(double pitch, double yaw) {
        double x = Math.cos(pitch) * Math.sin(yaw);
        double y = Math.sin(pitch);
        double z = Math.cos(pitch) * Math.cos(yaw);

        Vector3D facing = new Vector3D(x, y, z);
        facing.normalize();
        return facing;
    }

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
}
