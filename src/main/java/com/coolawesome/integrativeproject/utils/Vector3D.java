package com.coolawesome.integrativeproject.utils;


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

    public Vector3D(Vector3D otherVector) {
        this.x = otherVector.x;
        this.y = otherVector.y;
        this.z = otherVector.z;
    }

    public static Vector3D sum(Vector3D... vectors) {
        double x = 0;
        double y = 0;
        double z = 0;

        for (var vector : vectors) {
            x += vector.x;
            y += vector.y;
            z += vector.z;
        }
        return new Vector3D(x, y, z);
    }

    public static Vector3D difference(Vector3D v1, Vector3D v2) {
        double x = v1.x - v2.x;
        double y = v1.y - v2.y;
        double z = v1.z - v2.z;
        return new Vector3D(x, y, z);
    }

    public static Vector3D multiplication(double multiplier, Vector3D v1) {
        double x = multiplier * v1.x;
        double y = multiplier * v1.y;
        double z = multiplier * v1.z;
        return new Vector3D(x, y, z);
    }

    public Vector3D scalarProduct(double a) {
        double x = a * this.x;
        double y = a * this.y;
        double z = a * this.z;
        return new Vector3D(x, y, z);
    }

    public static double dotProduct(Vector3D v1, Vector3D v2) {
        return (v1.x * v2.x) + (v1.y * v2.y) + (v1.z * v2.z);
    }

    static public Vector3D unitVector(Vector3D v) {
        double magnitude = v.magnitude();
        return new Vector3D(v.x / magnitude, v.y / magnitude, v.z / magnitude);
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

    public void add(Vector3D... vectors) {
        for (var vector : vectors) {
            this.x += vector.x;
            this.y += vector.y;
            this.z += vector.z;
        }
    }

    public void subtract(Vector3D... vectors) {
        for (var vector : vectors) {
            this.x -= vector.x;
            this.y -= vector.y;
            this.z -= vector.z;
        }
    }

    public void multiply(double multiplier) {
        this.x *= multiplier;
        this.y *= multiplier;
        this.z *= multiplier;
    }

    public double magnitude() {
        return Math.sqrt((this.x * this.x) + (this.y * this.y) + (this.z * this.z));
    }

    public double distance(Vector3D v) {
        return Math.sqrt(Math.pow(this.x - v.x, 2) + Math.pow(this.y - v.y, 2) + Math.pow(this.z - v.z, 2));
    }

    public static double distance(Vector3D v1, Vector3D v2) {
        Vector3D difference = Vector3D.difference(v1, v2);
        double distance = difference.magnitude();
        return distance;
    }

    public void negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
    }

    public static Vector3D generateRandomVector() {
        // Randomizing parameters
        double x = Math.random() * 300 - 150;
        double y = Math.random() * 300 - 150;
        double z = Math.random() * 300 - 150;

        return new Vector3D(x,y,z);
    }

    @Override
    public String toString() {
        return "x: " + x + " y: " + y + " z: " + z;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Vector3D vector3D = (Vector3D) obj;
        return Double.compare(vector3D.x, x) == 0 && Double.compare(vector3D.y, y) == 0 && Double.compare(vector3D.z, z) == 0;
    }
}
