package com.coolawesome.integrativeproject.physics;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;


public class Planet {

    public Vector3D position;
    public Vector3D velocity;
    public Vector3D acceleration;
    public double radius;
    public double mass;
    public Color color;

    public Sphere planetNode;

    public Planet(Vector3D position, Vector3D velocity, double radius, double mass, Color color) {
        this.position = position;
        this.velocity = velocity;
        this.acceleration = new Vector3D();
        this.mass = mass;
        this.radius = radius;
        this.color = color;
        initSphere();
    }

    private void initSphere() {
        planetNode = new Sphere(radius);
        planetNode.setTranslateX(position.x);
        planetNode.setTranslateY(position.y);
        planetNode.setTranslateZ(position.z);
        planetNode.setMaterial(new PhongMaterial(color));
    }
}
