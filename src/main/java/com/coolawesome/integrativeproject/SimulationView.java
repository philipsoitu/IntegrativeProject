package com.coolawesome.integrativeproject;

import com.coolawesome.integrativeproject.physics.Planet;
import com.coolawesome.integrativeproject.physics.Simulation;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import java.util.ArrayList;

public class SimulationView extends Node {
    private ArrayList<Planet> planets;
    private PerspectiveCamera camera;
    private Simulation simulation;

    private void initializeCamera() {
        camera = new PerspectiveCamera(true);
        camera.getTransforms().addAll(
                new Translate(0, 0, -1000),
                new Rotate(-45, Rotate.Y_AXIS),
                new Rotate(-45, Rotate.X_AXIS)
        );
        camera.setFarClip(5000.0);
        camera.setNearClip(0.1);
    }

    public SimulationView( ArrayList<Planet> planets) {
        initializeCamera();
        this.planets = planets;
    }

    public void update(double dt) {
        planets.forEach(planet -> {
        });
    }

}
