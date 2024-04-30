package com.coolawesome.integrativeproject.utils;

/**
 * Class that represents a 3D vector.
 */
public class Vector3D {

    /**
     * The x coordinate of the vector.
     */
    public double x;
    /**
     * The y coordinate of the vector.
     */
    public double y;
    /**
     * The z coordinate of the vector.
     */
    public double z;

    /**
     * Constructs a Vector3D object with specified properties.
     *
     * @param x The x-coordinate of the vector.
     * @param y The y-coordinate of the vector.
     * @param z The z-coordinate of the vector.
     */
    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Constructs a Vector3D object with default properties. (0, 0, 0)
     */
    public Vector3D() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    /**
     * Constructs a Vector3D object with the same properties as another vector.
     *
     * @param otherVector The vector to copy properties from.
     */
    public Vector3D(Vector3D otherVector) {
        this.x = otherVector.x;
        this.y = otherVector.y;
        this.z = otherVector.z;
    }

    /**
     * Adds two vectors together.
     *
     * @param vectors The vectors to add together.
     * @return The sum of the vectors.
     */
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

    /**
     * Subtracts two vectors.
     *
     * @param v1 The vector to subtract from.
     * @param v2 The vector to subtract.
     * @return The difference between the two vectors.
     */
    public static Vector3D difference(Vector3D v1, Vector3D v2) {
        double x = v1.x - v2.x;
        double y = v1.y - v2.y;
        double z = v1.z - v2.z;
        return new Vector3D(x, y, z);
    }

    /**
     * Multiplies a vector by a scalar.
     *
     * @param multiplier The scalar to multiply the vector by.
     * @param v1         The vector to multiply.
     * @return The product of the vector and the scalar.
     */
    public static Vector3D multiplication(double multiplier, Vector3D v1) {
        double x = multiplier * v1.x;
        double y = multiplier * v1.y;
        double z = multiplier * v1.z;
        return new Vector3D(x, y, z);
    }

    /**
     * Calculates the dot product of two vectors.
     *
     * @param v1 The first vector.
     * @param v2 The second vector.
     * @return The dot product of the two vectors.
     */
    public static double dotProduct(Vector3D v1, Vector3D v2) {
        return (v1.x * v2.x) + (v1.y * v2.y) + (v1.z * v2.z);
    }

    /**
     * Calculates the unit vector of a vector.
     *
     * @param v The vector to calculate the unit vector of.
     * @return The unit vector of the vector.
     */
    static public Vector3D unitVector(Vector3D v) {
        double magnitude = v.magnitude();
        return new Vector3D(v.x / magnitude, v.y / magnitude, v.z / magnitude);
    }

    /**
     * Calculates the cross product of two vectors.
     *
     * @param v1 The first vector.
     * @param v2 The second vector.
     * @return The cross product of the two vectors.
     */
    public static Vector3D crossProduct(Vector3D v1, Vector3D v2) {
        double x = (v1.y * v2.z) - (v1.z * v2.y);
        double y = (v1.z * v2.x) - (v1.x * v2.z);
        double z = (v1.x * v2.y) - (v1.y * v2.x);
        return new Vector3D(x, y, z);
    }

    /**
     * Calculates the distance between two vectors.
     *
     * @param v1 The first vector.
     * @param v2 The second vector.
     * @return The distance between the two vectors.
     */
    public static double distance(Vector3D v1, Vector3D v2) {
        Vector3D difference = Vector3D.difference(v1, v2);
        return difference.magnitude();
    }

    /**
     * Generates a random vector.
     *
     * @return A random vector.
     */
    public static Vector3D generateRandomVector() {
        // Randomizing parameters
        double x = Math.random() * 300 - 150;
        double y = Math.random() * 300 - 150;
        double z = Math.random() * 300 - 150;

        return new Vector3D(x, y, z);
    }

    /**
     * Multiplies a vector by a scalar.
     *
     * @param a The scalar to multiply the vector by.
     * @return The product of the vector and the scalar.
     */
    public Vector3D scalarProduct(double a) {
        double x = a * this.x;
        double y = a * this.y;
        double z = a * this.z;
        return new Vector3D(x, y, z);
    }

    /**
     * Normalizes the vector.
     */
    public void normalize() {
        double magnitude = magnitude();
        this.x /= magnitude;
        this.y /= magnitude;
        this.z /= magnitude;
    }

    /**
     * Adds vectors to the current vector.
     *
     * @param vectors The vectors to add.
     */
    public void add(Vector3D... vectors) {
        for (var vector : vectors) {
            this.x += vector.x;
            this.y += vector.y;
            this.z += vector.z;
        }
    }

    /**
     * Subtracts vectors from the current vector.
     *
     * @param vectors The vectors to subtract.
     */
    public void subtract(Vector3D... vectors) {
        for (var vector : vectors) {
            this.x -= vector.x;
            this.y -= vector.y;
            this.z -= vector.z;
        }
    }

    /**
     * Multiplies the vector by a scalar.
     *
     * @param multiplier The scalar to multiply the vector by.
     */
    public void multiply(double multiplier) {
        this.x *= multiplier;
        this.y *= multiplier;
        this.z *= multiplier;
    }

    /**
     * Gets the magnitude of the vector.
     *
     * @return The magnitude of the vector.
     */
    public double magnitude() {
        return Math.sqrt((this.x * this.x) + (this.y * this.y) + (this.z * this.z));
    }

    /**
     * Calculates the distance between two vectors.
     *
     * @param v The vector to calculate the distance to.
     * @return The distance between the two vectors.
     */
    public double distance(Vector3D v) {
        return Math.sqrt(Math.pow(this.x - v.x, 2) + Math.pow(this.y - v.y, 2) + Math.pow(this.z - v.z, 2));
    }

    /**
     * Negates the vector.
     */
    public void negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
    }

    /**
     * Prints the vector.
     *
     * @return The string representation of the vector.
     */
    @Override
    public String toString() {
        return "x: " + x + " y: " + y + " z: " + z;
    }

    /**
     * Checks if two vectors are equal.
     *
     * @param obj The object to compare to.
     * @return Whether the two vectors are equal.
     */
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
