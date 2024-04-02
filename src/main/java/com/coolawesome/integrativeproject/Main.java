package com.coolawesome.integrativeproject;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    ArrayList<Particle> particles = new ArrayList<Particle>();
    int worldX = 700;
    int worldY = 700;

    long d1 = System.currentTimeMillis();
    long d2 = System.currentTimeMillis();
    long dt;


    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Simulation");

//        particles.add(new Particle(350, 350, 0, 0, 20, 300000, Color.YELLOW));


        for (int i = 1; i < 100; i++) {
//           Initial values randomization
            double x = Math.random() * 700;
            double y = Math.random() * 700;
            double xVel = -0.01 + Math.random() * 0.02;
            double yVel = -0.01 + Math.random() * 0.02;
            double radius = 2 + Math.random() * 4;
            double mass = 5000;
            int r = (int) (Math.random() * 255);
            int g = (int) (Math.random() * 255);
            int b = (int) (Math.random() * 255);
            Color color = Color.rgb(r, g, b);

            particles.add(new Particle(x, y, xVel, yVel, radius, mass, color));

        }


        // Create a Canvas
        Canvas canvas = new Canvas(worldX, worldY);

        // Get the graphics context of the canvas
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Create a StackPane and add the Canvas to it
        StackPane root = new StackPane();
        root.getChildren().add(canvas);

        // Create the scene and set it to the stage
        Scene scene = new Scene(root, worldX, worldY);
        primaryStage.setScene(scene);

        // Show the stage
        primaryStage.show();

        // Set up the game loop to update at 60fps
        Timeline gameLoop = new Timeline(new KeyFrame(Duration.seconds(1.0 / 60), event -> {
            d2 = System.currentTimeMillis();
            dt = (d2 - d1);
            updateN2(((double) dt / 1000) * 1);
//            bounce();
            handleCollisions();
            render(gc);
            d1 = d2;
        }));
        gameLoop.setCycleCount(Animation.INDEFINITE);
        gameLoop.play();

    }

    private void handleCollisions() {
        List<Particle> particlesToRemove = new ArrayList<>();
        for (Particle p1 : particles) {
            for (Particle p2 : particles) {
                if (p1 != p2 && p1 != null && p2 != null) {
                    Vector n = Vector.unitVector(Vector.sub(p1.position, p2.position));
                    double dist = Math.min(Vector.add(Vector.sub(p2.position, Vector.multiply(p2.radius, n)), p1.position).magnitude(), Vector.sub(Vector.sub(p2.position, Vector.multiply(p2.radius, n)), p1.position).magnitude());
                    if (p1.radius > dist) {
                        p1.mass += p2.mass;
                        p1.radius = Math.sqrt(p1.radius * p1.radius + p2.radius * p2.radius);
                        double totalMass = p1.mass + p2.mass;
                        int red = (int) Math.min(((p1.mass / totalMass) * p1.color.getRed() + (p2.mass / totalMass) * p2.color.getRed()) * 255, 255);
                        int green = (int) Math.min(((p1.mass / totalMass) * p1.color.getGreen() + (p2.mass / totalMass) * p2.color.getGreen()) * 255, 255);
                        int blue = (int) Math.min(((p1.mass / totalMass) * p1.color.getBlue() + (p2.mass / totalMass) * p2.color.getBlue()) * 255, 255);

                        p1.color = Color.rgb(red, green, blue);
                        particlesToRemove.add(p2);
                    }
                }
            }
        }
        particles.removeAll(particlesToRemove);
    }


    private void updateN2(double dt) {

//        double G = 0.000001;
        double G = 0.001;

        for (Particle p1 : particles) {
            double fx = 0;
            double fy = 0;
            Vector ftotal = new Vector();
            for (Particle p2 : particles) {
                if (p1 != p2) {
                    Vector dist = Vector.sub(p2.position, p1.position);
                    Vector f = Vector.multiply((G * p1.mass * p2.mass) / (dist.magnitude() * dist.magnitude()), Vector.unitVector(dist));
                    ftotal = Vector.add(ftotal, f);

                }

                Vector newAcc = Vector.multiply((1 / p1.mass), ftotal);
                if (newAcc.magnitude() <= 1) {
                    p1.acceleration = Vector.multiply((1 / p1.mass), ftotal);
                }
                p1.velocity = Vector.add(p1.velocity, Vector.multiply(dt, p1.acceleration));
                p1.position = Vector.add(p1.position, Vector.multiply(dt, p1.velocity));


            }


        }

    }

    public void bounce() {
        final double ENERGY_CONSERVATION = 1;
        for (Particle p1 : particles) {
            if (p1.position.x - p1.radius <= 0) {
                p1.velocity.x *= -ENERGY_CONSERVATION;
                p1.position.x = p1.radius;
            }
            if (p1.position.x + p1.radius >= worldX) {
                p1.velocity.x *= -ENERGY_CONSERVATION;
                p1.position.x = worldX - p1.radius;
            }
            if (p1.position.y - p1.radius <= 0) {
                p1.velocity.y *= -ENERGY_CONSERVATION;
                p1.position.y = p1.radius;
            }
            if (p1.position.y + p1.radius >= worldY) {
                p1.velocity.y *= -ENERGY_CONSERVATION;
                p1.position.y = worldY - p1.radius;
            }
        }

    }

    private void render(GraphicsContext gc) {
        gc.clearRect(0, 0, worldX, worldY);

        for (Particle planet : particles) {
            planet.draw(gc);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}