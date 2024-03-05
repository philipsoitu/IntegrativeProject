package com.coolawesome.integrativeproject;

import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.layout.AnchorPane;

import javafx.scene.shape.Sphere;
import org.fxyz3d.scene.Skybox;
import org.fxyz3d.utils.CameraTransformer;

import com.coolawesome.integrativeproject.utils.Vector3D;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class SimulationView extends Group {

    private final Set<KeyCode> keysPressed = new HashSet<>();

    private final Map<String, Planet> planetMap;
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

    public SimulationView(AnchorPane pane, Map<String, Planet> planetMap) {
        this.planetMap = planetMap;

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
        subScene.setOnKeyPressed(event -> keysPressed.add(event.getCode()));
        subScene.setOnKeyReleased(event -> keysPressed.remove(event.getCode()));

        subScene.requestFocus();
    }

    public void update(double dt) {
        // check if there are any planets not currently in the scene
        planetMap.forEach((id, planet) -> {
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
                for (Planet planet : planetMap.values()) {
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
                for (Planet planet : planetMap.values()) {
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
        for (var planet : planetMap.values()) {
            planet.planetNode.setTranslateX(planet.position.x);
            planet.planetNode.setTranslateY(planet.position.y);
            planet.planetNode.setTranslateZ(planet.position.z);
            if (planet.isSun){
                planet.sunLight.setTranslateX(planet.position.x);
                planet.sunLight.setTranslateY(planet.position.y);
                planet.sunLight.setTranslateZ(planet.position.z);
            }
        }

        // Look at the planet if the camera is set to do so
        if (!currentCamPlanetID.isEmpty()) {
            Planet planet = planetMap.get(currentCamPlanetID);
            if (planet != null) {
                Vector3D camPos = new Vector3D(
                        cameraTransform.t.getX(),
                        cameraTransform.t.getY(),
                        cameraTransform.t.getZ()
                );
                Vector3D planetPos = planet.position;
                Vector3D direction = Vector3D.difference(planetPos, camPos);
                direction.normalize();

                double directionPitch = -Math.toDegrees(Math.asin(direction.y));
                double directionYaw = Math.toDegrees(Math.atan2(direction.x, direction.z));

                cameraTransform.rx.setAngle(lerpAngle(cameraTransform.rx.getAngle(), directionPitch, 0.5));
                cameraTransform.ry.setAngle(lerpAngle(cameraTransform.ry.getAngle(), directionYaw, 0.5));
            }
        }

        //TODO: fix looking straight up/down
        //TODO: fix flying into planets

        // Move the camera
        double yaw = Math.toRadians(cameraTransform.ry.getAngle());
        double pitch = Math.toRadians(cameraTransform.rx.getAngle());

        double x = Math.cos(pitch) * Math.sin(yaw);
        double y = Math.sin(pitch);
        double z = Math.cos(pitch) * Math.cos(yaw);

        Vector3D facing = new Vector3D(x, y, z);
        facing.normalize();

        Vector3D right = Vector3D.crossProduct(facing, new Vector3D(0, 1, 0));
        right.normalize();

        Vector3D up = Vector3D.crossProduct(right, facing);
        up.normalize();

        facing.multiply(0.6); // reduce the speed of the camera
        right.multiply(0.6);
        up.multiply(0.6);

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

}
