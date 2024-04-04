package com.coolawesome.integrativeproject;


public class Vector {

    double x;
    double y;

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector() {
        this.x = 0;
        this.y = 0;
    }

    public static Vector add(Vector... args) {
        double x = 0;
        double y = 0;

        for (var i : args) {
            x += i.x;
            y += i.y;
        }
        return new Vector(x, y);
    }

    public static Vector sub(Vector v1, Vector v2) {
        return new Vector(v1.x - v2.x, v1.y - v2.y);
    }

    public static Vector multiply(double a, Vector v1) {
        double x = (double) a * (double) v1.x;
        double y = (double) a * (double) v1.y;
        return new Vector(x, y);
    }

    public static Vector division(Vector v1, double a) {
        return new Vector((v1.x / a), (v1.y / a));
    }

    public static double dotProduct(Vector v1, Vector v2) {
        return (v1.x * v2.x) + (v1.y * v2.y);
    }

    public static Vector fromAngle(double Angle) {
        return new Vector(Math.cos(Angle), Math.sin(Angle));
    }

    public void rotate(double angle) {
        double newX = x * Math.cos(angle) - y * Math.sin(angle);
        double newY = x * Math.sin(angle) + y * Math.cos(angle);
        x = newX;
        y = newY;
    }

    public double magnitude() {
        return Math.sqrt((this.x * this.x) + (this.y * this.y));
    }

    public Vector multiply(double scalar) {
        return new Vector(this.x * scalar, this.y * scalar);
    }

    static public Vector unitVector(Vector v) {
        return new Vector(v.x / v.magnitude(), v.y / v.magnitude());
    }

}