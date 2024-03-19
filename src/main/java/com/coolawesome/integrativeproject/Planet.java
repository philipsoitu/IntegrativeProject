package com.coolawesome.integrativeproject;

import com.coolawesome.integrativeproject.utils.Vector3D;
import javafx.geometry.Point3D;
import javafx.scene.PointLight;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;


public class Planet {

    public Vector3D position;
    public Vector3D velocity;
    public Vector3D acceleration;
    public double radius;
    public double mass;
    public double spinRate;
    public Color color;
    public PhongMaterial material;

    public boolean isSun;
    public PointLight sunLight;

    private static final Image sunTexture = new Image("file:src/main/resources/images/planets/sun.jpg");
    private static final Image[] planetTextures = {
            new Image("file:src/main/resources/images/planets/Alpine.png"),
            new Image("file:src/main/resources/images/planets/Gaseous1.png"),
            new Image("file:src/main/resources/images/planets/Gaseous2.png"),
            new Image("file:src/main/resources/images/planets/Gaseous3.png"),
            new Image("file:src/main/resources/images/planets/Gaseous4.png"),
            new Image("file:src/main/resources/images/planets/Icy.png"),
            new Image("file:src/main/resources/images/planets/Martian.png"),
            new Image("file:src/main/resources/images/planets/Savannah.png"),
            new Image("file:src/main/resources/images/planets/Swamp.png"),
            new Image("file:src/main/resources/images/planets/Terrestrial1.png"),
            new Image("file:src/main/resources/images/planets/Terrestrial2.png"),
            new Image("file:src/main/resources/images/planets/Terrestrial3.png"),
            new Image("file:src/main/resources/images/planets/Terrestrial4.png"),
            new Image("file:src/main/resources/images/planets/Tropical.png"),
            new Image("file:src/main/resources/images/planets/Venusian.png"),
            new Image("file:src/main/resources/images/planets/Volcanic.png")
    };
    public Sphere planetNode;

    // TODO: planets rings using FXyz torus?
    public Planet(Vector3D position, Vector3D velocity, double radius, double mass, boolean isSun) {
        this.position = position;
        this.velocity = velocity;
        this.acceleration = new Vector3D();
        this.mass = mass;
        this.radius = radius;
        double hue = Math.random() * 360;
        Color randColor = Color.hsb(hue, 1,1);
        this.color = randColor;
        this.spinRate = -1.5 + (Math.random() * 3);
        this.isSun = isSun;

        planetNode = new Sphere(radius);
        planetNode.setTranslateX(position.x);
        planetNode.setTranslateY(position.y);
        planetNode.setTranslateZ(position.z);
        material = new PhongMaterial(color);
        if (isSun) {
            initSun();
        } else {
            initSphere();
        }
    }

    private void initSphere() {
        material.setDiffuseMap(planetTextures[(int) (Math.random() * planetTextures.length)]);
        planetNode.setMaterial(material);
        planetNode.setRotationAxis(new Point3D(0, 1, 0));
    }

    private void initSun() {
        material.setSelfIlluminationMap(sunTexture); //TODO: add more sun textures
        material.setDiffuseColor(Color.BLACK);
        material.setSpecularPower(0);
        planetNode.setMaterial(material);
        planetNode.setRotationAxis(new Point3D(0, 1, 0));
        sunLight = new PointLight(this.color);
        sunLight.setTranslateX(this.position.x);
        sunLight.setTranslateY(this.position.y);
        sunLight.setTranslateZ(this.position.z);
    }

    @Override
    public String toString() {
        return "Planet{" +
                "position=" + String.format("%.3f, %.3f, %.3f", position.x, position.y, position.z) +
                ", velocity=" + String.format("%.3f, %.3f, %.3f", velocity.x, velocity.y, velocity.z) +
                ", acceleration=" + String.format("%.3f, %.3f, %.3f", acceleration.x, acceleration.y, acceleration.z) +
                ", radius=" + String.format("%.3f", radius) +
                ", mass=" + String.format("%.3f", mass) +
                ", color=" + color +
                '}';
    }
}
