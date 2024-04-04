package com.coolawesome.integrativeproject;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Particle {

    Vector position;
    Vector velocity;
    Vector acceleration;
    Vector force;
    double radius;
    double mass;
    Color color;

    public Particle(double x, double y, double xVel, double yVel, double radius, double mass, Color color) {
        this.position = new Vector(x, y);
        this.velocity = new Vector(xVel, yVel);
        this.acceleration = new Vector();
        this.mass = mass;
        this.radius = radius;
        this.color = color;
        this.force = new Vector(0, 0);
    }

    public void update(double dt) {
        acceleration = force.multiply(1.0 / mass);
        velocity = Vector.add(velocity, acceleration.multiply(dt));
        position = Vector.add(position, velocity.multiply(dt));
    }

    public void draw(GraphicsContext gc) {
        gc.setFill(this.color);
        gc.fillOval(this.position.x - radius, this.position.y - radius, 2 * radius, 2 * radius);
    }

    public void drawWithCameraOffset(GraphicsContext gc, double cameraX, double cameraY) {
        gc.setFill(color);
        gc.fillOval(position.x - radius + cameraX, position.y - radius + cameraY, radius * 2, radius * 2);
    }
}