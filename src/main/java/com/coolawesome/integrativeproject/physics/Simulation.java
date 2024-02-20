package com.coolawesome.integrativeproject.physics;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class Simulation {

    public ArrayList<Planet3D> planets = new ArrayList<Planet3D>();


    public void initialize(int numOfBodies) {
        planets.add(new Planet3D(new Vector3D(350, 350, 0), new Vector3D(), 20, 300000, Color.YELLOW));

        for (int i = 1; i < numOfBodies; i++) {
//            Randomizing parameters
            Vector3D randPos = new Vector3D(Math.random() * 700, Math.random() * 700, Math.random() * 700);
            Vector3D randVel = new Vector3D(-1 + Math.random() * 2, -1 + Math.random() * 2, -1 + Math.random() * 2);
            double randRad = 5 + Math.random() * 5;
            double randMass = 5000;
            int r = (int) (Math.random() * 255);
            int g = (int) (Math.random() * 255);
            int b = (int) (Math.random() * 255);
            Color randColor = Color.rgb(r, g, b);

//            Adding newly randomized Planet to ArrayList
            Planet3D randPlanet = new Planet3D(randPos, randVel, randRad, randMass, randColor);
            planets.add(randPlanet);
        }
    }

    private void update(double dt) {

        double G = 0.001;

        for (Planet3D p1 : planets) {
            Vector3D ftotal = new Vector3D();
            for (Planet3D p2 : planets) {
                if (p1 != p2) {
                    Vector3D dist = Vector3D.sub(p2.position, p1.position);
                    Vector3D f = Vector3D.multiply((G * p1.mass * p2.mass) / (dist.magnitude() * dist.magnitude()), Vector3D.unitVector(dist));
                    ftotal = Vector3D.add(ftotal, f);

                }

                Vector3D newAcc = Vector3D.multiply((1 / p1.mass), ftotal);
                if (newAcc.magnitude() <= 1) {
                    p1.acceleration = Vector3D.multiply((1 / p1.mass), ftotal);
                }
                p1.velocity = Vector3D.add(p1.velocity, Vector3D.multiply(dt, p1.acceleration));
                p1.position = Vector3D.add(p1.position, Vector3D.multiply(dt, p1.velocity));


            }


        }

    }

    private void handleCollisions() {
        List<Planet3D> planetsToRemove = new ArrayList<>();
        for (Planet3D p1 : planets) {
            for (Planet3D p2 : planets) {
                if (p1 != p2 && p1 != null && p2 != null) {
                    Vector3D n = Vector3D.unitVector(Vector3D.sub(p1.position, p2.position));
                    double dist = Math.min(Vector3D.add(Vector3D.sub(p2.position, Vector3D.multiply(p2.radius, n)), p1.position).magnitude(), Vector3D.sub(Vector3D.sub(p2.position, Vector3D.multiply(p2.radius, n)), p1.position).magnitude());
                    if (p1.radius > dist) {
                        p1.mass += p2.mass;
                        p1.radius = Math.sqrt(p1.radius * p1.radius + p2.radius * p2.radius);
                        double totalMass = p1.mass + p2.mass;
                        int red = (int)Math.min(((p1.mass/totalMass)*p1.color.getRed() + (p2.mass/totalMass)*p2.color.getRed())*255,255);
                        int green = (int)Math.min(((p1.mass/totalMass)*p1.color.getGreen() + (p2.mass/totalMass)*p2.color.getGreen())*255,255);
                        int blue = (int)Math.min(((p1.mass/totalMass)*p1.color.getBlue() + (p2.mass/totalMass)*p2.color.getBlue())*255,255);

                        p1.color = Color.rgb(red, green, blue);
                        planetsToRemove.add(p2);
                    }
                }
            }
        }
        planets.removeAll(planetsToRemove);
    }
}
