package com.coolawesome.integrativeproject;

import com.coolawesome.integrativeproject.utils.Constants;
import com.coolawesome.integrativeproject.utils.JsonPlanetManager;
import com.coolawesome.integrativeproject.utils.Vector3D;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import java.util.*;

public class Simulation {

    Map<String, Planet> planetMap = new HashMap<>();
    TreeNode root;
    double theta = 0.5;
    double G = 0.0001;

    SimulationView simulationView;
    JsonPlanetManager planetManager = new JsonPlanetManager();

    static int collisionCount = 0;
    boolean isPaused = false;

    public Simulation(AnchorPane viewport, MainController controller) {
        simulationView = new SimulationView(viewport, this, controller);
        initialize(100);
    }

    public void initialize(int numOfBodies) {
        for (int i = 0; i < numOfBodies; i++) {
            String uniqueID = UUID.randomUUID().toString().replaceAll("-", "");
            Planet randPlanet = createRandomPlanet(uniqueID);

            // Add the Planet to the Map
            planetMap.put(uniqueID, randPlanet);
        }
        String uniqueID = UUID.randomUUID().toString().replaceAll("-", "");
        planetMap.put(uniqueID, new Planet("sun", new Vector3D(5000, 0, 0), new Vector3D(), 1000, 100000, true));
    }

    public Planet createRandomPlanet(String uniqueID) {
        Vector3D randPos = Vector3D.generateRandomVector();
        Vector3D randVel = new Vector3D();

//       boolean sun = Math.random() * 4 < 1;
        boolean sun = false;

        return new Planet(uniqueID, randPos, randVel, getRandomRadius(), Constants.defaultMass, sun);
    }

    public Planet createRandomPlanet(String uniqueID, Vector3D pos) {
        boolean sun = Math.random() * 4 < 1;
        return new Planet(uniqueID, pos, new Vector3D(), getRandomRadius(), Constants.defaultMass, sun);
    }


    private double getRandomRadius() {
        return 1 + Math.random() * 2;
    }

    public void update(double dt){
        // handle physics and collisions
        if(!isPaused) {
            planetMap.forEach((id, p) -> {
                p.update(dt);
            });

            constructTree();
            gravity();

        }

        simulationView.update(dt);
    }



    private void constructTree() {
        double[] boundingSquare = getBoundingSquare();
        root = new TreeNode(boundingSquare[0], boundingSquare[1], boundingSquare[2], boundingSquare[3]);

        planetMap.forEach((id, p) -> {
            root.insert(p);
        });
    }

    public double[] getBoundingSquare() {
        // Indices
        double minX = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;

        planetMap.forEach((id, p) -> {
            minX = Math.min(minX, p.position.x);
            maxX = Math.max(maxX, p.position.x);
            minY = Math.min(minY, p.position.y);
            maxY = Math.max(maxY, p.position.y);

        });


        return new double[]{minX, minY, Math.max(maxX - minX, maxY - minY)};
    }


    public void saveToJson(String filePath) {
        planetManager.saveToJson(planetMap, filePath);
    }

    public void loadFromJson(String filePath) {
        planetMap = planetManager.loadFromJson(filePath);
    }
}
