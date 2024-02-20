package com.coolawesome.integrativeproject.physics;


public class Vector3D {

    double x;
    double y;
    double z;

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

    public static Vector3D add(Vector3D... args) {
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

    public static Vector3D sub(Vector3D v1, Vector3D v2) {
        return new Vector3D(v1.x - v2.x, v1.y - v2.y, v2.z - v1.z);
    }

    public static Vector3D multiply(double a, Vector3D v1) {
        double x = (double) a * (double) v1.x;
        double y = (double) a * (double) v1.y;
        double z = (double) a * (double) v1.z;
        return new Vector3D(x, y, z);
    }

    public static double dotProduct(Vector3D v1, Vector3D v2) {
        return (v1.x * v2.x) + (v1.y * v2.y) + (v1.z * v2.z);
    }

    public double magnitude() {
        return Math.sqrt((this.x * this.x) + (this.y * this.y) + (this.z * this.z));
    }

    static public Vector3D unitVector(Vector3D v) {
        double magn = v.magnitude();
        return new Vector3D(v.x / magn, v.y / magn, v.z / magn);
    }

}
