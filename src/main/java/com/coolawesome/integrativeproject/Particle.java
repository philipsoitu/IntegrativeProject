package com.coolawesome.integrativeproject;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Particle {

    Vector position;
    Vector velocity;
    Vector acceleration;
    double radius;
    double mass;
    Color color;

    public Particle(double x, double y, double xVel, double yVel, double radius, double mass, Color color) {
        this.position = new Vector(x,y);
        this.velocity = new Vector(xVel, yVel);
        this.acceleration = new Vector();
        this.mass = mass;
        this.radius = radius;
        this.color = color;
    }

    public void draw(GraphicsContext gc) {
        gc.setFill(this.color);
        gc.fillOval(this.position.x-radius, this.position.y-radius, 2*radius, 2*radius);
    }

}