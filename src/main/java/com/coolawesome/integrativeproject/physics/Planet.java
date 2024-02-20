package com.coolawesome.integrativeproject.physics;

import javafx.scene.paint.Color;


public class Planet {

    Vector3D position;
    Vector3D velocity;
    Vector3D acceleration;
    double radius;
    double mass;
    Color color;

    public Planet(Vector3D position, Vector3D velocity, double radius, double mass, Color color) {
        this.position = position;
        this.velocity = velocity;
        this.acceleration = new Vector3D();
        this.mass = mass;
        this.radius = radius;
        this.color = color;
    }
}
