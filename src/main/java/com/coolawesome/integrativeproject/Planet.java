package com.coolawesome.integrativeproject;

import com.coolawesome.integrativeproject.utils.Vector3D;
import javafx.geometry.Point3D;
import javafx.scene.PointLight;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;

/**
 * Represents a planet in a simulated solar system, including properties such as
 * position, velocity, and physical characteristics like radius and mass.
 */
public class Planet {

    /**
     * The texture of the sun.
     */
    public static final Image sunTexture = new Image("file:src/main/resources/images/planets/sun.jpg");
    /**
     * The textures of the planets.
     */
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
            new Image("file:src/main/resources/images/planets/Volcanic.png")};

    /**
     * The name of the planet.
     */
    public String name;
    /**
     * The position of the planet as a Vector3D.
     */
    public Vector3D position;
    /**
     * The velocity of the planet as a Vector3D.
     */
    public Vector3D velocity;
    /**
     * The acceleration of the planet as a Vector3D.
     */
    public Vector3D acceleration;
    /**
     * The radius of the planet.
     */
    public double radius;
    /**
     * The mass of the planet.
     */
    public double mass;
    /**
     * The spin rate of the planet.
     */
    public double spinRate;
    /**
     * The color of the planet.
     */
    public Color color;
    /**
     * The material of the planet.
     */
    public PhongMaterial material;
    /**
     * A boolean indicating if the planet is the sun.
     */
    public boolean isSun;
    /**
     * The PointLight of the sun.
     */
    public PointLight sunLight;
    /**
     * The Sphere object representing the planet.
     */
    public Sphere planetNode;

    /**
     * Constructs a Planet object with specified properties, initializing with random color and spin rate.
     *
     * @param name     The name of the planet.
     * @param position The initial position of the planet as a Vector3D.
     * @param velocity The initial velocity of the planet as a Vector3D.
     * @param radius   The radius of the planet.
     * @param mass     The mass of the planet.
     * @param isSun    A boolean indicating if the planet is the sun.
     */
    public Planet(String name, Vector3D position, Vector3D velocity, double radius, double mass, boolean isSun) {
        this.name = name;
        this.position = position;
        this.velocity = velocity;
        this.acceleration = new Vector3D();
        this.mass = mass;
        this.radius = radius;
        double hue = Math.random() * 360;
        this.color = Color.hsb(hue, 1, 1);
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

    /**
     * Constructs a Planet object with specified properties and color, initializing with random spin rate.
     *
     * @param name     The name of the planet.
     * @param position The initial position of the planet as a Vector3D.
     * @param velocity The initial velocity of the planet as a Vector3D.
     * @param radius   The radius of the planet.
     * @param mass     The mass of the planet.
     * @param isSun    A boolean indicating if the planet is the sun.
     * @param color    The color of the planet.
     */
    public Planet(String name, Vector3D position, Vector3D velocity, double radius, double mass, boolean isSun, Color color) {
        this.name = name;
        this.position = position;
        this.velocity = velocity;
        this.acceleration = new Vector3D();
        this.mass = mass;
        this.radius = radius;
        this.color = color;
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

    /**
     * Constructs a custom Planet object with specified properties, texture, and color.
     *
     * @param name     The name of the planet.
     * @param position The initial position of the planet as a Vector3D.
     * @param velocity The initial velocity of the planet as a Vector3D.
     * @param radius   The radius of the planet.
     * @param mass     The mass of the planet.
     * @param isSun    A boolean indicating if the planet is the sun.
     * @param texture  The texture image of the planet.
     * @param color    The color of the planet.
     */
    public Planet(String name, Vector3D position, Vector3D velocity, double radius, double mass, boolean isSun, Image texture, Color color) {
        this(name, position, velocity, radius, mass, isSun);

        material.setDiffuseColor(color);
        material.setDiffuseMap(texture);
        this.color = color;
        planetNode.setMaterial(material);
    }

    /**
     * Initializes the sphere representation of the planet with random texture.
     */
    private void initSphere() {
        material.setDiffuseMap(planetTextures[(int) (Math.random() * Planet.planetTextures.length)]);
        planetNode.setMaterial(material);
        planetNode.setRotationAxis(new Point3D(0, 1, 0));
    }

    /**
     * Initializes the sun representation of the planet.
     */
    private void initSun() {
        material.setSelfIlluminationMap(sunTexture);
        material.setSpecularPower(0);
        planetNode.setMaterial(material);
        planetNode.setRotationAxis(new Point3D(0, 1, 0));
        sunLight = new PointLight(this.color);
        sunLight.setTranslateX(this.position.x);
        sunLight.setTranslateY(this.position.y);
        sunLight.setTranslateZ(this.position.z);
    }

    /**
     * Updates the planet's position and velocity based on its acceleration and the time step.
     *
     * @param dt The time step for the update.
     */
    public void update(double dt) {
        velocity.add(acceleration.scalarProduct(dt));
        position.add(velocity.scalarProduct(dt));
    }

    /**
     * Provides a string representation of the planet, including its position, velocity, acceleration, radius, mass, and color.
     *
     * @return A string representation of the planet.
     */
    @Override
    public String toString() {
        return "Planet{" +
                "position=" + String.format("%.3f, %.3f, %.3f", position.x, position.y, position.z) +
                ", velocity=" + String.format("%.3f, %.3f, %.3f", velocity.x, velocity.y, velocity.z) +
                ", acceleration=" + String.format("%.3f, %.3f, %.3f", acceleration.x, acceleration.y, acceleration.z) +
                ", radius=" + String.format("%.3f", radius) +
                ", mass=" + String.format("%.3f", mass) +
                ", color=" + color + '}';
    }
}
