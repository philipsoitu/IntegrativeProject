package com.coolawesome.integrativeproject.physics;


public class Vector3D {

    public double x;
    public double y;
    public double z;

    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3D() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public static Vector3D sum(Vector3D... args) {
        double x = 0;
        double y = 0;
        double z = 0;

        for (var i : args) {
            x += i.x;
            y += i.y;
            z += i.z;
        }
        return new Vector3D(x, y, z);
    }

    public static Vector3D difference(Vector3D v1, Vector3D v2) {
        double x = v1.x - v2.x;
        double y = v1.y - v2.y;
        double z = v1.z - v2.z;
        return new Vector3D(x, y, z);
    }

    public static Vector3D multiplication(double a, Vector3D v1) {
        double x = (double) a * (double) v1.x;
        double y = (double) a * (double) v1.y;
        double z = (double) a * (double) v1.z;
        return new Vector3D(x, y, z);
    }

    public static double dotProduct(Vector3D v1, Vector3D v2) {
        return (v1.x * v2.x) + (v1.y * v2.y) + (v1.z * v2.z);
    }

    static public Vector3D unitVector(Vector3D v) {
        double magn = v.magnitude();
        return new Vector3D(v.x / magn, v.y / magn, v.z / magn);
    }

    public static Vector3D crossProduct(Vector3D v1, Vector3D v2) {
        double x = (v1.y * v2.z) - (v1.z * v2.y);
        double y = (v1.z * v2.x) - (v1.x * v2.z);
        double z = (v1.x * v2.y) - (v1.y * v2.x);
        return new Vector3D(x, y, z);
    }

    public void normalize() {
        double magnitude = magnitude();
        this.x /= magnitude;
        this.y /= magnitude;
        this.z /= magnitude;
    }

    public void add(Vector3D... args) {
        for (var i : args) {
            this.x += i.x;
            this.y += i.y;
            this.z += i.z;
        }
    }

    public void subtract(Vector3D... args) {
        for (var i : args) {
            this.x -= i.x;
            this.y -= i.y;
            this.z -= i.z;
        }
    }

    public void multiply(double a) {
        this.x *= a;
        this.y *= a;
        this.z *= a;
    }

    public double magnitude() {
        return Math.sqrt((this.x * this.x) + (this.y * this.y) + (this.z * this.z));
    }

    public double distance(Vector3D v) {
        return Math.sqrt(Math.pow(this.x - v.x, 2) + Math.pow(this.y - v.y, 2) + Math.pow(this.z - v.z, 2));
    }

    @Override
    public String toString() {
        return "x: " + x + " y: " + y + " z: " + z;
    }
}
