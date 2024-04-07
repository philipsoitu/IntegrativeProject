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

    ArrayList<Particle> particles = new ArrayList<>();
    TreeNode root;

    int screenX = 1280;
    int screenY = 720;
    double cameraX = 100;
    double cameraY = 100;

    long d1 = System.currentTimeMillis();
    long d2 = System.currentTimeMillis();
    long dt;


    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Simulation");

//        particles.add(new Particle(350, 350, 0, 0, 20, 300000, Color.YELLOW));

////        DEBUG CASE:
//        particles.add(new Particle(0, 0, 0, 0, 4, 100, Color.RED));
//        particles.add(new Particle(30, 30, 0, 0, 4, 100, Color.GREEN));
//        particles.add(new Particle(50, 10, 0, 0, 4, 300, Color.YELLOW));
//        particles.add(new Particle(70, 30, 0, 0, 4, 200, Color.PINK));
//        particles.add(new Particle(80, 80, 0, 0, 4, 1000, Color.BROWN));

        for (int i = 0; i < 10; i++) {
//           Initial values randomization
            double x = 100 + Math.random() * 500;
            double y = 100 + Math.random() * 500;
            double xVel = -0.01 + Math.random() * 0.02;
            double yVel = -0.01 + Math.random() * 0.02;
            double radius = 2 + Math.random() * 4;
            double mass = 500;
            int r = (int) (Math.random() * 255);
            int g = (int) (Math.random() * 255);
            int b = (int) (Math.random() * 255);
            Color color = Color.rgb(r, g, b);

            particles.add(new Particle(x, y, xVel, yVel, radius, mass, color));

        }


        // Create a Canvas
        Canvas canvas = new Canvas(screenX, screenY);

        // Get the graphics context of the canvas
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Create a StackPane and add the Canvas to it
        StackPane root = new StackPane();
        root.getChildren().add(canvas);

        Scene scene = new Scene(root, screenX, screenY);

        scene.setOnKeyPressed(event -> {
            double movementSpeed = 10.0; // Adjust this value for faster or slower camera movement
            switch (event.getCode()) {
                case W: // Move camera up
                    cameraY += movementSpeed;
                    break;
                case A: // Move camera left
                    cameraX += movementSpeed;
                    break;
                case S: // Move camera down
                    cameraY -= movementSpeed;
                    break;
                case D: // Move camera right
                    cameraX -= movementSpeed;
                    break;
                default:
                    break;
            }
        });

        primaryStage.setScene(scene);
        primaryStage.show();

        // Set up the game loop to update at 60fps
        Timeline gameLoop = new Timeline(new KeyFrame(Duration.seconds(1.0 / 60), event -> {
            d2 = System.currentTimeMillis();
            dt = (d2 - d1);
            System.out.println(dt);

            for (Particle p : particles) {
                p.update(dt);
            }

            constructTree();

            render(gc);
            d1 = d2;


        }));
        gameLoop.setCycleCount(Animation.INDEFINITE);
        gameLoop.play();

    }

    public double[] getBoundingSquare() {
        // Indices
        double minX = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;

        for (int i = 0; i < particles.size(); i++) {
            minX = Math.min(minX, particles.get(i).position.x);
            maxX = Math.max(maxX, particles.get(i).position.x);
            minY = Math.min(minY, particles.get(i).position.y);
            maxY = Math.max(maxY, particles.get(i).position.y);
        }

        return new double[]{minX, minY, Math.max(maxX - minX, maxY - minY)};
    }

    public void constructTree() {
        double[] boundingSquare = getBoundingSquare();
        root = new TreeNode(boundingSquare[0], boundingSquare[1], boundingSquare[2]);

        for (Particle p : particles) {
            root.insert(p);
        }
    }


    private void render(GraphicsContext gc) {
        gc.clearRect(0, 0, screenX, screenY);

        root.draw(gc, cameraX, cameraY);

        for (Particle particle : particles) {
            particle.drawWithCameraOffset(gc, cameraX, cameraY);
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}