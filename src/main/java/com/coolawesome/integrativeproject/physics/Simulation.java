package com.coolawesome.integrativeproject.physics;

import com.coolawesome.integrativeproject.SimulationView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import java.util.*;

public class Simulation {

    Map<String, Planet> planetMap = new HashMap<>();
    SimulationView simulationView;

    public Simulation(AnchorPane viewport) {
        simulationView = new SimulationView(viewport, planetMap);
        initialize(3);
    }

    public void initialize(int numOfBodies) {
        for (int i = 0; i < numOfBodies; i++) {

            // Randomizing parameters
            Vector3D randPos = new Vector3D(Math.random() * 30 - 15, Math.random() * 30 - 15, Math.random() * 30 - 15);
            // Vector3D randVel = new Vector3D(-1 + Math.random() * 2, -1 + Math.random() * 2, -1 + Math.random() * 2);
            Vector3D randVel = new Vector3D();
            double randRad = 1 + Math.random() * 2;
            double randMass = 5000;
            int r = (int) (Math.random() * 255);
            int g = (int) (Math.random() * 255);
            int b = (int) (Math.random() * 255);
            Color randColor = Color.rgb(r, g, b);

            // Generate a unique ID for the planet
            String uniqueID = UUID.randomUUID().toString().replaceAll("-", "");

            // Create the Planet object
            Planet randPlanet = new Planet(randPos, randVel, randRad, randMass, randColor);

            // Add the Planet to the ArrayList and the Map
            planetMap.put(uniqueID, randPlanet);
        }
    }

    public void update(double dt){
        // Do physics n shit here
        updatePosition(dt);
        handleCollisions();
        simulationView.update(dt);


    }

    private void updatePosition(double dt) {

        double G = 0.001;

        planetMap.forEach((id, p1) -> {
            Vector3D ftotal = new Vector3D();
            planetMap.forEach((id2, p2) -> {
                if (p1 != p2) {
                    Vector3D dist = Vector3D.difference(p2.position, p1.position);
                    Vector3D f = Vector3D.multiplication((G * p1.mass * p2.mass) / (dist.magnitude() * dist.magnitude()), Vector3D.unitVector(dist));
                    ftotal.add(f);

                }

                p1.acceleration = Vector3D.multiplication((1 / p1.mass), ftotal);
                p1.velocity.add(Vector3D.multiplication(dt, p1.acceleration));
                p1.position.add(Vector3D.multiplication(dt, p1.velocity));
            });
        });
    }

    private void handleCollisions() {
        List<String> idPlanetsToRemove = new ArrayList<>();
        planetMap.forEach((id, p1) -> {
            planetMap.forEach((id2, p2) -> {
                if (p1 != p2 && p1 != null && p2 != null) {
                    Vector3D n = Vector3D.unitVector(Vector3D.difference(p1.position, p2.position));
                    double dist = Math.min(Vector3D.sum(Vector3D.difference(p2.position, Vector3D.multiplication(p2.radius, n)), p1.position).magnitude(), Vector3D.difference(Vector3D.difference(p2.position, Vector3D.multiplication(p2.radius, n)), p1.position).magnitude());
                    if (p1.radius > dist) {
                        p1.mass += p2.mass;
                        p1.radius = Math.sqrt(p1.radius * p1.radius + p2.radius * p2.radius);
                        double totalMass = p1.mass + p2.mass;
                        int red = (int)Math.min(((p1.mass/totalMass)*p1.color.getRed() + (p2.mass/totalMass)*p2.color.getRed())*255,255);
                        int green = (int)Math.min(((p1.mass/totalMass)*p1.color.getGreen() + (p2.mass/totalMass)*p2.color.getGreen())*255,255);
                        int blue = (int)Math.min(((p1.mass/totalMass)*p1.color.getBlue() + (p2.mass/totalMass)*p2.color.getBlue())*255,255);

                        p1.color = Color.rgb(red, green, blue);
                        idPlanetsToRemove.add(id2);
                    }
                }
            });
        });
        idPlanetsToRemove.forEach((id) -> planetMap.remove(id));
//        planetMap.remove(idPlanetsToRemove);
    }
}
